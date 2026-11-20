"""Effort estimates for iBank (SOEN 6611, D3, Problem 9).

Use case points: Karner (1993), with the Schneider and Winters (1998) rule for hours per UCP.
Basic COCOMO 81: Boehm (1981), organic mode, size from Problem 5 (physical SLOC, test code excluded).
All ratings below are team judgements; change them here and rerun.
Usage: python3 estimates.py [actual person-hours]
"""
import json
import sys

ACTORS = [  # name, Karner class, weight (simple 1, average 2, complex 3)
    ('Client (person using the GUI)', 'complex', 3),
    ('Bank host system (network protocol)', 'average', 2),
    ('ABM devices: card reader, dispenser, printer (defined API)', 'simple', 1),
]

USE_CASES = {  # name: transactions (main and alternative flows); includes and extends folded in
    'Authenticate': ['choose language', 'insert card', 'enter PIN', 'PIN accepted',
                     'wrong PIN, attempts left', 'card retained after third wrong PIN'],
    'Withdraw cash': ['choose account', 'choose fast cash or type amount', 'check notes possible',
                      'check funds', 'check daily limit', 'debit account', 'dispense notes',
                      'reverse debit on dispenser fault'],
    'Cardless withdrawal': ['enter code', 'reject unknown or used code', 'reject expired code',
                            'plan notes', 'debit and mark code used', 'dispense or reverse'],
    'Deposit cash': ['choose account', 'count notes', 'reject invalid bundle', 'credit account',
                     'flag $10,000 cash for reporting'],
    'Deposit cheque': ['choose account', 'enter amount', 'reject cheque above $20,000',
                       'credit account'],
    'Transfer between accounts': ['choose accounts', 'enter amount', 'reject same account',
                                  'check funds', 'move money'],
    'Pay a bill': ['choose payee', 'choose account', 'enter amount', 'check funds', 'pay'],
    'View balance': ['request balances', 'read balances from host', 'print receipt'],
    'Change PIN': ['enter current PIN', 'enter new PIN twice', 'apply PIN rules',
                   'replace PIN hash', 'count wrong current PIN as attempt'],
}

TECHNICAL = [  # factor, weight, rating 0 to 5, reason
    ('T1 Distributed system', 2, 1, 'host simulated in process'),
    ('T2 Response time', 1, 2, 'interactive, no hard limit'),
    ('T3 End user efficiency', 1, 4, 'fast cash, short flows'),
    ('T4 Complex internal processing', 1, 2, 'note planning, limits'),
    ('T5 Reusable code', 1, 3, 'explicit P4 requirement'),
    ('T6 Easy to install', 0.5, 1, 'build scripts only'),
    ('T7 Easy to use', 0.5, 5, 'bilingual, accessible'),
    ('T8 Portable', 2, 3, 'IDE and OS independent'),
    ('T9 Easy to change', 1, 4, 'maintainability goal (D1)'),
    ('T10 Concurrent', 1, 0, 'one session at a time'),
    ('T11 Security features', 1, 4, 'PIN hashing, blocking, masking'),
    ('T12 Access for third parties', 1, 0, 'none'),
    ('T13 Special training', 1, 0, 'none'),
]

ENVIRONMENTAL = [
    ('E1 Familiar with the process', 1.5, 2, 'agile elements, new to UCP'),
    ('E2 Application experience', 0.5, 1, 'first banking system'),
    ('E3 OO experience', 1, 3, 'graduate students'),
    ('E4 Lead analyst capability', 0.5, 3, 'average'),
    ('E5 Motivation', 1, 4, 'graded course project'),
    ('E6 Stable requirements', 2, 4, 'fixed by the course outline'),
    ('E7 Part time workers', -1, 5, 'all members part time'),
    ('E8 Difficult language', -2, 1, 'Java, well known'),
]

KDSI = 2.293            # physical SLOC from Problem 5 / 1000
HOURS_PER_PM = 152      # Boehm 1981
COCOMO = {'organic': (2.4, 1.05, 2.5, 0.38), 'semidetached': (3.0, 1.12, 2.5, 0.35),
          'embedded': (3.6, 1.20, 2.5, 0.32)}


def uc_weight(n):
    return ('simple', 5) if n <= 3 else (('average', 10) if n <= 7 else ('complex', 15))


def main():
    uaw = sum(w for _, _, w in ACTORS)
    uucw = sum(uc_weight(len(t))[1] for t in USE_CASES.values())
    uucp = uaw + uucw
    tfactor = sum(w * r for _, w, r, _ in TECHNICAL)
    efactor = sum(w * r for _, w, r, _ in ENVIRONMENTAL)
    tcf = 0.6 + 0.01 * tfactor
    ecf = 1.4 - 0.03 * efactor
    ucp = uucp * tcf * ecf
    low = sum(1 for n, _, r, _ in ENVIRONMENTAL[:6] if r < 3)
    high = sum(1 for n, _, r, _ in ENVIRONMENTAL[6:] if r > 3)
    sw = 20 if low + high <= 2 else (28 if low + high <= 4 else 36)
    out = dict(uaw=uaw, uucw=uucw, uucp=uucp, tfactor=tfactor, tcf=round(tcf, 3),
               efactor=efactor, ecf=round(ecf, 3), ucp=round(ucp, 2),
               schneider_winters_count=low + high, hours_per_ucp=sw,
               ucp_hours_karner=round(ucp * 20, 1), ucp_hours=round(ucp * sw, 1),
               use_cases={k: [len(v), uc_weight(len(v))[0], uc_weight(len(v))[1]]
                          for k, v in USE_CASES.items()})
    for mode, (a, b, c, d) in COCOMO.items():
        effort = a * KDSI ** b
        tdev = c * effort ** d
        out['cocomo_' + mode] = dict(pm=round(effort, 2), hours=round(effort * HOURS_PER_PM, 1),
                                     tdev_months=round(tdev, 2), staff=round(effort / tdev, 2))
    org = out['cocomo_organic']['hours']
    out['ucp_vs_cocomo_ratio'] = round(out['ucp_hours'] / org, 2)
    if len(sys.argv) > 1:
        actual = float(sys.argv[1])
        out['actual_hours'] = actual
        out['mre_ucp'] = round(abs(out['ucp_hours'] - actual) / actual, 3)
        out['mre_cocomo'] = round(abs(org - actual) / actual, 3)
    print(json.dumps(out, indent=2))


if __name__ == '__main__':
    main()
