# D3 measurement scripts (Problems 7 to 10)

| Command | Output |
|---|---|
| `python3 oo_metrics.py ../iBank/src/main/java results_d3` | `methods_cc.csv` (v(G) and decisions per method), `classes_oo.csv` (WMC, Ce, Ca, CF i, m, a, mu per field, LCOM*, logical SLOC per class), `oo_summary.json` |
| `python3 estimates.py [actual hours]` | UCP and Basic COCOMO 81 estimates; with actual hours, MRE for both |

Requirements: Python 3 with `javalang` and `scipy`. Counting rules are documented at the top of each script.
Checks: per method v(G) equals lizard 1.26.1 on all 300 bodies (sum 385); logical SLOC per class plus
package and import lines equals the Problem 5 total of 1,762; WMC, LCOM* and CF were hand checked on
CashDispenser, Card and Session. `cfg.png` is the control flow graph of CashDispenser.plan.
