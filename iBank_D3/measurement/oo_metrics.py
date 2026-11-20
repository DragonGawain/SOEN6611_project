"""Cyclomatic complexity, WMC, CF, LCOM* and logical SLOC per class for iBank (SOEN 6611, D3).

Usage: python3 oo_metrics.py <path to iBank/src/main/java> <output directory>

Classes: every class, enum and interface declared in src/main/java, nested ones included (64).
Test code is excluded, as in Problem 5.

Cyclomatic complexity v(G) of a method (McCabe 1976) = number of decisions + 1, where a decision
is an if, for, enhanced for, while, do, case label (not default), catch, ?: operator, && or ||.
Lambda bodies count toward the method that contains them. Abstract interface methods have no
control flow graph; they are given v(G) = 1, the value of a single path.
System cyclomatic number: v(G) = e - n + 2p summed over the p method graphs = sum of method v(G).

WMC (Chidamber and Kemerer 1994), weights not normalized: WMC = sum of v(G) over the methods and
constructors declared in the class (inherited methods are not counted).

CF, Coupling Factor (Brito e Abreu 1995, MOOD):
  CF = sum_i sum_j is_client(Ci, Cj) / (TC^2 - TC)
  is_client(Cc, Cs) = 1 when Cc references Cs (field, parameter, local or return type, object
  creation, static call or constant, throws or catch clause, type argument), Cc != Cs, and Cs is
  not an ancestor of Cc (extends or implements, transitively). JDK types are not counted.
  Per class we report Ce (classes it is a client of), Ca (classes that are its clients) and
  CF_i = Ce / (TC - 1), the class's share of the possible links.

LCOM* (Henderson-Sellers 1996): LCOM* = ((1/a) sum_j mu(A_j) - m) / (1 - m)
  m = instance methods declared in the class (constructors and static methods excluded, since
  neither works on an object's state); a = attributes (instance and static
  fields, compile time constants 'static final' excluded); mu(A_j) = methods that access A_j
  directly (bare name, this.name, or name qualified by a variable of the same class).
  Undefined (n/a) when m < 2 or a = 0.

Logical SLOC per class: the statement categories of measure.py (Problem 5), counted inside the
class body, nested classes excluded. package and import lines belong to the file, not to a class.
"""
import csv
import json
import os
import sys
from collections import defaultdict

import javalang
from javalang import tree as T

TYPE_NODES = (T.ClassDeclaration, T.InterfaceDeclaration, T.EnumDeclaration)
LOGICAL = (T.ClassDeclaration, T.InterfaceDeclaration, T.EnumDeclaration,
           T.EnumConstantDeclaration, T.FieldDeclaration, T.MethodDeclaration,
           T.ConstructorDeclaration, T.LocalVariableDeclaration, T.StatementExpression,
           T.ReturnStatement, T.ThrowStatement, T.BreakStatement, T.ContinueStatement,
           T.IfStatement, T.SwitchStatement, T.SwitchStatementCase, T.ForStatement,
           T.WhileStatement, T.DoStatement, T.TryStatement, T.CatchClause,
           T.SynchronizedStatement, T.AssertStatement)


def children(node):
    for child in node.children:
        if isinstance(child, T.Node):
            yield child
        elif isinstance(child, (list, tuple, set)):
            for item in child:
                if isinstance(item, T.Node):
                    yield item
                elif isinstance(item, (list, tuple)):
                    for sub in item:
                        if isinstance(sub, T.Node):
                            yield sub


def walk(node, stop_at_types=True):
    """Yields node and its descendants, without entering nested type declarations."""
    yield node
    for child in children(node):
        if stop_at_types and isinstance(child, TYPE_NODES):
            continue
        yield from walk(child, stop_at_types)


def body_of(type_node):
    if isinstance(type_node, T.EnumDeclaration):
        return list(type_node.body.constants or []) + list(type_node.body.declarations or [])
    return list(type_node.body or [])


def decisions(method):
    found = []
    for node in walk(method):
        if node is method:
            continue
        if isinstance(node, (T.IfStatement, T.ForStatement, T.WhileStatement, T.DoStatement)):
            found.append(type(node).__name__.replace('Statement', '').lower())
        elif isinstance(node, T.SwitchStatementCase):
            found.extend('case' for c in node.case if c != 'default')
        elif isinstance(node, T.CatchClause):
            found.append('catch')
        elif isinstance(node, T.TernaryExpression):
            found.append('?:')
        elif isinstance(node, T.BinaryOperation) and node.operator in ('&&', '||'):
            found.append(node.operator)
    return found


def ref_names(node):
    """Simple type names a subtree refers to."""
    names = set()
    for n in walk(node):
        if isinstance(n, T.ReferenceType):
            names.add(n.name)
            sub = n.sub_type
            while sub is not None:
                names.add(sub.name)
                sub = sub.sub_type
        if isinstance(n, (T.MethodInvocation, T.MemberReference)) and n.qualifier:
            names.update(n.qualifier.split('.'))
        if isinstance(n, T.MethodDeclaration) and n.throws:
            names.update(n.throws)
        if isinstance(n, T.ConstructorDeclaration) and n.throws:
            names.update(n.throws)
        if isinstance(n, T.CatchClauseParameter):
            names.update(n.types)
        if isinstance(n, T.MethodReference) and isinstance(n.expression, T.MemberReference):
            names.add(n.expression.member)
    return names


def declared_names(method):
    """Parameter, local and lambda parameter names, with their declared type when known."""
    names = {}
    for n in walk(method):
        if isinstance(n, (T.FormalParameter, T.CatchClauseParameter)):
            names[n.name] = getattr(getattr(n, 'type', None), 'name', None)
        elif isinstance(n, T.LocalVariableDeclaration):
            for d in n.declarators:
                names[d.name] = n.type.name
        elif isinstance(n, T.LambdaExpression):
            for p in n.parameters:
                if isinstance(p, T.MemberReference):
                    names[p.member] = None
                elif hasattr(p, 'name'):
                    names[p.name] = getattr(getattr(p, 'type', None), 'name', None)
        elif isinstance(n, T.EnhancedForControl):
            for d in n.var.declarators:
                names[d.name] = n.var.type.name
    return names


def fields_accessed(method, fields, class_name):
    local = declared_names(method)
    used = set()
    for n in walk(method):
        if isinstance(n, T.This):
            for s in n.selectors or []:
                if isinstance(s, T.MemberReference) and s.member in fields:
                    used.add(s.member)
        if isinstance(n, T.MemberReference) and n.member in fields:
            q = n.qualifier or ''
            if q == 'this' or (q == '' and n.member not in local):
                used.add(n.member)
            elif q in local and local[q] == class_name:
                used.add(n.member)
        if isinstance(n, T.MemberReference) and n.qualifier in fields and n.qualifier not in local:
            used.add(n.qualifier)
        if isinstance(n, T.MethodInvocation) and n.qualifier:
            head = n.qualifier.split('.')[0]
            if head in fields and head not in local:
                used.add(head)
    return used


def logical_count(type_node):
    count = 0
    for n in walk(type_node):
        if isinstance(n, LOGICAL):
            count += 1
        if isinstance(n, T.IfStatement) and n.else_statement is not None \
                and not isinstance(n.else_statement, T.IfStatement):
            count += 1
        if isinstance(n, T.TryStatement) and n.finally_block:
            count += 1
    return count


def collect_types(root):
    types = []
    for directory, _, names in sorted(os.walk(root)):
        for name in sorted(names):
            if not name.endswith('.java'):
                continue
            path = os.path.join(directory, name)
            source = open(path, encoding='utf-8').read()
            cu = javalang.parse.parse(source)
            package = cu.package.name if cu.package else ''
            file_logical = 1 + len(cu.imports)

            def visit(node, outer):
                qualified = (outer + '.' if outer else '') + node.name
                types.append(dict(node=node, name=node.name, qualified=qualified,
                                  package=package, file=os.path.relpath(path, root),
                                  kind=type(node).__name__.replace('Declaration', '')))
                for member in body_of(node):
                    if isinstance(member, TYPE_NODES):
                        visit(member, qualified)

            for top in cu.types:
                visit(top, '')
            types[-1].setdefault('file_logical', 0)
            for t in types:
                if t['file'] == os.path.relpath(path, root) and t['qualified'] == cu.types[0].name:
                    t['file_logical'] = file_logical
    return types


def main(root, out):
    os.makedirs(out, exist_ok=True)
    types = collect_types(root)
    project = {t['name'] for t in types}
    assert len(project) == len(types), 'simple type names must be unique'
    by_name = {t['name']: t for t in types}

    # ancestors inside the project
    parents = {}
    for t in types:
        node = t['node']
        sup = set()
        ext = getattr(node, 'extends', None)
        if isinstance(ext, list):
            sup.update(e.name for e in ext)
        elif ext is not None:
            sup.add(ext.name)
        sup.update(i.name for i in (getattr(node, 'implements', None) or []))
        parents[t['name']] = {s for s in sup if s in project}

    def ancestors(name, seen=None):
        seen = set() if seen is None else seen
        for p in parents[name]:
            if p not in seen:
                seen.add(p)
                ancestors(p, seen)
        return seen

    method_rows, class_rows = [], []
    clients = {}
    for t in types:
        node, name = t['node'], t['name']
        members = body_of(node)
        methods = [m for m in members if isinstance(m, (T.MethodDeclaration, T.ConstructorDeclaration))]
        wmc = 0
        for m in methods:
            if m.body is None and isinstance(m, T.MethodDeclaration):
                d, cc = [], 1
            else:
                d = decisions(m)
                cc = len(d) + 1
            wmc += cc
            kind = 'constructor' if isinstance(m, T.ConstructorDeclaration) else (
                'abstract' if m.body is None else 'method')
            method_rows.append(dict(cls=t['qualified'], package=t['package'], method=m.name,
                                    kind=kind, line=m.position.line if m.position else '',
                                    decisions=' '.join(d), cc=cc))
        # coupling, from everything in the class body except nested types
        refs = set()
        for member in members:
            if isinstance(member, TYPE_NODES):
                continue
            refs |= ref_names(member)
        refs &= project
        refs.discard(name)
        refs -= ancestors(name)
        clients[name] = refs
        # LCOM*
        fields = []
        for member in members:
            if isinstance(member, T.FieldDeclaration):
                if 'static' in member.modifiers and 'final' in member.modifiers:
                    continue
                fields.extend(d.name for d in member.declarators)
        plain = [m for m in methods if isinstance(m, T.MethodDeclaration) and m.body is not None
                 and 'static' not in m.modifiers]
        mu = {f: 0 for f in fields}
        access = {}
        for m in plain:
            used = fields_accessed(m, set(fields), name)
            access[m.name + '@' + str(m.position.line)] = sorted(used)
            for f in used:
                mu[f] += 1
        a, mcount = len(fields), len(plain)
        if a == 0 or mcount < 2:
            lcom = None
        else:
            lcom = (sum(mu.values()) / a - mcount) / (1 - mcount)
        class_rows.append(dict(cls=t['qualified'], name=name, package=t['package'], kind=t['kind'],
                               file=t['file'], nom=len(methods), wmc=wmc,
                               max_cc=max([r['cc'] for r in method_rows if r['cls'] == t['qualified']] or [0]),
                               m=mcount, a=a, sum_mu=sum(mu.values()),
                               lcom=None if lcom is None else round(lcom, 3),
                               mu=json.dumps(mu), access=json.dumps(access),
                               logical=logical_count(node), file_logical=t.get('file_logical', 0)))

    tc = len(types)
    afferent = defaultdict(int)
    for c, refs in clients.items():
        for s in refs:
            afferent[s] += 1
    links = sum(len(r) for r in clients.values())
    for row in class_rows:
        row['ce'] = len(clients[row['name']])
        row['ca'] = afferent[row['name']]
        row['cf_i'] = round(row['ce'] / (tc - 1), 4)
        row['clients_of'] = ' '.join(sorted(clients[row['name']]))

    with open(os.path.join(out, 'methods_cc.csv'), 'w', newline='') as f:
        w = csv.DictWriter(f, fieldnames=list(method_rows[0]))
        w.writeheader()
        w.writerows(method_rows)
    with open(os.path.join(out, 'classes_oo.csv'), 'w', newline='') as f:
        w = csv.DictWriter(f, fieldnames=list(class_rows[0]))
        w.writeheader()
        w.writerows(class_rows)
    ccs = [r['cc'] for r in method_rows]
    summary = dict(classes=tc, methods=len(method_rows),
                   bodies=sum(1 for r in method_rows if r['kind'] != 'abstract'),
                   system_vg=sum(ccs), max_cc=max(ccs), mean_cc=round(sum(ccs) / len(ccs), 3),
                   cc_hist={str(k): ccs.count(k) for k in sorted(set(ccs))},
                   links=links, possible=tc * tc - tc, cf=round(links / (tc * tc - tc), 4),
                   logical_total=sum(r['logical'] + r['file_logical'] for r in class_rows))
    json.dump(summary, open(os.path.join(out, 'oo_summary.json'), 'w'), indent=2)
    print(json.dumps(summary, indent=2))


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])
