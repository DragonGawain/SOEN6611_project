"""Size and readability measurement for iBank (SOEN 6611, Deliverable 2).

Usage: python3 measure.py <path to iBank/src/main/java> <output directory>

Physical SLOC: a physical line is counted when it holds at least one Java token, that is, any
character that is neither white space nor part of a comment. Blank lines and comment only lines
are not counted. A line with code and a trailing comment is counted once.

Logical SLOC: one count per logical statement, using the AST from javalang. The categories
counted are listed in LOGICAL_NODES plus 'else' and 'finally'. Braces, blocks, empty
statements, annotations, comments and blank lines are not counted. A for header counts once,
however many semicolons it has. Statements inside block lambdas count like any other statement.

Readability: the model of Posnett, Hindle and Devanbu (MSR 2011), applied per method or
constructor body. z = 8.87 - 0.033 V + 0.40 L - 1.5 H, score = 1 / (1 + e^-z), where V is the
Halstead volume of the method's tokens, L its non blank lines and H the Shannon entropy (bits)
of its token distribution. Operands are identifiers and literals; every other token is an
operator.
"""
import collections
import csv
import json
import math
import os
import sys

import javalang

LOGICAL_NODES = {
    'PackageDeclaration': 'package or import',
    'Import': 'package or import',
    'ClassDeclaration': 'type declaration',
    'InterfaceDeclaration': 'type declaration',
    'EnumDeclaration': 'type declaration',
    'EnumConstantDeclaration': 'enum constant',
    'FieldDeclaration': 'field declaration',
    'MethodDeclaration': 'method or constructor header',
    'ConstructorDeclaration': 'method or constructor header',
    'LocalVariableDeclaration': 'local variable declaration',
    'StatementExpression': 'expression statement',
    'ReturnStatement': 'jump (return, throw, break, continue)',
    'ThrowStatement': 'jump (return, throw, break, continue)',
    'BreakStatement': 'jump (return, throw, break, continue)',
    'ContinueStatement': 'jump (return, throw, break, continue)',
    'IfStatement': 'selection (if, else, switch, case)',
    'SwitchStatement': 'selection (if, else, switch, case)',
    'SwitchStatementCase': 'selection (if, else, switch, case)',
    'ForStatement': 'iteration (for, while, do)',
    'WhileStatement': 'iteration (for, while, do)',
    'DoStatement': 'iteration (for, while, do)',
    'TryStatement': 'exception (try, catch, finally)',
    'CatchClause': 'exception (try, catch, finally)',
    'SynchronizedStatement': 'other',
    'AssertStatement': 'other',
}
OPERAND_TYPES = (javalang.tokenizer.Identifier, javalang.tokenizer.Literal)


def java_files(root):
    for directory, _, names in os.walk(root):
        for name in sorted(names):
            if name.endswith('.java'):
                yield os.path.join(directory, name)


def physical_counts(source, tokens):
    lines = source.split('\n')
    if lines and lines[-1] == '':
        lines = lines[:-1]
    code_lines = {token.position.line for token in tokens}
    blank = sum(1 for line in lines if not line.strip())
    return {'total': len(lines), 'blank': blank, 'physical': len(code_lines),
            'comment_only': len(lines) - blank - len(code_lines)}, code_lines


def logical_statements(tree):
    found = []
    for _, node in tree:
        kind = type(node).__name__
        if kind in LOGICAL_NODES:
            found.append((LOGICAL_NODES[kind], kind, node.position.line if node.position else None))
        if kind == 'IfStatement' and node.else_statement is not None \
                and type(node.else_statement).__name__ != 'IfStatement':
            found.append(('selection (if, else, switch, case)', 'else', None))
        if kind == 'TryStatement' and node.finally_block:
            found.append(('exception (try, catch, finally)', 'finally', None))
    return found


def method_spans(tree, tokens):
    """Yields (kind, name, first token index, last token index) for each method with a body."""
    index_of = {(tok.position.line, tok.position.column): i for i, tok in enumerate(tokens)}
    for _, node in tree:
        kind = type(node).__name__
        if kind not in ('MethodDeclaration', 'ConstructorDeclaration') or node.body is None:
            continue
        pos = (node.position.line, node.position.column)
        start = index_of.get(pos)
        if start is None:
            start = min(i for i, tok in enumerate(tokens) if (tok.position.line,
                        tok.position.column) >= pos)
        while start > 0 and isinstance(tokens[start - 1], javalang.tokenizer.Modifier):
            start -= 1
        i = start
        while tokens[i].value != '{':
            i += 1
        depth = 0
        for j in range(i, len(tokens)):
            if tokens[j].value == '{':
                depth += 1
            elif tokens[j].value == '}':
                depth -= 1
                if depth == 0:
                    yield kind, node.name, start, j
                    break


def posnett(method_tokens, source_lines):
    operators = [t.value for t in method_tokens if not isinstance(t, OPERAND_TYPES)]
    operands = [t.value for t in method_tokens if isinstance(t, OPERAND_TYPES)]
    n1, n2 = len(set(operators)), len(set(operands))
    big_n = len(operators) + len(operands)
    small_n = n1 + n2
    volume = big_n * math.log2(small_n) if small_n > 1 else 0.0
    counts = collections.Counter(t.value for t in method_tokens)
    entropy = -sum(c / big_n * math.log2(c / big_n) for c in counts.values())
    first, last = method_tokens[0].position.line, method_tokens[-1].position.line
    lines = sum(1 for k in range(first, last + 1) if source_lines[k - 1].strip())
    z = 8.87 - 0.033 * volume + 0.40 * lines - 1.5 * entropy
    score = 1.0 / (1.0 + math.exp(-z))
    return {'N1': len(operators), 'N2': len(operands), 'n1': n1, 'n2': n2, 'N': big_n,
            'n': small_n, 'V': round(volume, 2), 'L': lines, 'H': round(entropy, 3),
            'z': round(z, 3), 'score': round(score, 4)}


def main(root, out):
    os.makedirs(out, exist_ok=True)
    file_rows, method_rows, categories = [], [], collections.Counter()
    for path in java_files(root):
        source = open(path, encoding='utf-8').read()
        tokens = list(javalang.tokenizer.tokenize(source))
        tree = javalang.parse.parse(source)
        phys, _ = physical_counts(source, tokens)
        logical = logical_statements(tree)
        categories.update(c for c, _, _ in logical)
        rel = os.path.relpath(path, root)
        package = os.path.dirname(rel).replace(os.sep, '.')
        file_rows.append(dict(file=rel, package=package, **phys, logical=len(logical)))
        source_lines = source.split('\n')
        for kind, name, start, end in method_spans(tree, tokens):
            metrics = posnett(tokens[start:end + 1], source_lines)
            method_rows.append(dict(file=rel, package=package, method=name,
                                    line=tokens[start].position.line, **metrics))
    with open(os.path.join(out, 'sloc_by_file.csv'), 'w', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=list(file_rows[0]))
        writer.writeheader()
        writer.writerows(file_rows)
    with open(os.path.join(out, 'readability_by_method.csv'), 'w', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=list(method_rows[0]))
        writer.writeheader()
        writer.writerows(method_rows)
    by_package = collections.OrderedDict()
    for row in sorted(file_rows, key=lambda r: r['package']):
        p = by_package.setdefault(row['package'], collections.Counter())
        p.update({k: row[k] for k in ('total', 'blank', 'comment_only', 'physical', 'logical')})
        p['files'] += 1
    scores = sorted(r['score'] for r in method_rows)
    summary = {
        'files': len(file_rows),
        'totals': {k: sum(r[k] for r in file_rows)
                   for k in ('total', 'blank', 'comment_only', 'physical', 'logical')},
        'by_package': {k: dict(v) for k, v in by_package.items()},
        'logical_by_category': dict(categories.most_common()),
        'methods': len(method_rows),
        'readable_methods': sum(1 for s in scores if s >= 0.5),
        'median_score': scores[len(scores) // 2],
        'mean_score': round(sum(scores) / len(scores), 4),
    }
    json.dump(summary, open(os.path.join(out, 'summary.json'), 'w'), indent=2)
    print(json.dumps(summary, indent=2))


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])
