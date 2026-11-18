# Measurement scripts for iBank (Problems 5 and 6)

These scripts are not part of iBank and are not included in its SLOC count.

`python3 measure.py ../iBank/src/main/java results` writes:

| File | Content |
|---|---|
| `results/sloc_by_file.csv` | Total, blank and comment only lines, physical SLOC and logical SLOC for every source file |
| `results/readability_by_method.csv` | Halstead counts, volume V, lines L, entropy H, z and the Posnett readability score for every method and constructor |
| `results/summary.json` | Totals per package, logical statements per category, readability summary |
| `results/lizard_functions.csv` | Cyclomatic complexity per method from lizard, used for the D1 GQM targets |

Requirements: Python 3 with `javalang` (`pip install javalang`). Test code in `src/test/java` is
excluded, as Problem 5 requires. The counting rules are documented at the top of `measure.py`.
Physical SLOC was cross checked with cloc 1.98 (code column: 2,293, identical).
