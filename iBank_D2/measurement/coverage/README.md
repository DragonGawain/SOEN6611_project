# Test coverage (JaCoCo 0.8.11)

1. Build and test iBank once with `../../iBank/test.sh` so that `out/classes` and `out/test-classes` exist.
2. Run the tests with the JaCoCo agent:
   `java -javaagent:jacocoagent.jar=destfile=jacoco.exec -jar ../../iBank/lib/junit-platform-console-standalone-1.9.1.jar --class-path ../../iBank/out/classes:../../iBank/out/test-classes --scan-class-path`
3. Compile and run `Cov.java` with the JaCoCo core jar and ASM on the class path:
   `java -cp .:org.jacoco.core.jar:asm.jar:asm-commons.jar:asm-tree.jar Cov jacoco.exec ../../iBank/out/classes`

Result on 2026/10/19: line coverage 564 of 1,089 (51.8%), branches 111 of 166 (66.9%). Outside
`ibank.ui` and `App`: 547 of 592 lines (92.4%). `ibank.ui`: 17 of 480 lines (3.5%).
