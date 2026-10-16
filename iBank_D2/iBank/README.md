# iBank ABM simulator (SOEN 6611, Deliverable 2)

A Java Swing simulator of the iBank multifunction ABM described in Deliverable 1: bank owned,
branch lobby in Montreal, Canadian dollars only, English and French, $20 and $50 notes.

## Requirements

A JDK, version 11 or later. Nothing else: no IDE, no Maven, no Gradle. The JUnit 5 console
launcher used by the tests is included in `lib` (Eclipse Public License 2.0).

## Build, run and test

| Task | Linux and macOS | Windows |
|---|---|---|
| Compile | `./build.sh` | `build.bat` |
| Start the ABM | `./run.sh` | `run.bat` |
| Run the 90 tests | `./test.sh` | `test.bat` |

The scripts call `javac` and `java` directly, so the code builds the same way in any editor or
on the command line.

## Demonstration data

| Card holder | PIN | Purpose |
|---|---|---|
| Amélie Tremblay (card ending 1234) | 2468 | Chequing $2,500, savings $15,000, two payees |
| Jordan Lee (card ending 5678) | 1357 | Chequing $45.50, used to show insufficient funds |
| Priya Singh (card ending 0000) | any | Blocked card, used to show card retention |

Cardless withdrawal code: `482915` ($100 from Amélie's chequing account, valid 30 minutes after
start up).

The **Simulation** menu switches the bank system offline (assumption A6) and simulates a cash
dispenser fault, to show the error messages and the automatic reversal.

## Structure

| Package | Responsibility |
|---|---|
| `ibank.domain` | Money, accounts, cards, clients, transaction records |
| `ibank.error` | `BankingException` and its subclasses, each with a message key |
| `ibank.host` | The bank host interface and an in memory implementation with the bank's rules |
| `ibank.device` | Cash dispenser and receipt printer |
| `ibank.atm` | `AtmService`, the single entry point used by the user interface |
| `ibank.i18n` | English and French texts and currency formats |
| `ibank.ui` | Swing screens and reusable components |

Code style: Google Java Style, checked with Checkstyle 8.36 and `google_checks.xml`
(0 violations in `src/main/java` and `src/test/java`).
