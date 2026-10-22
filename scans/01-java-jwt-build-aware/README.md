# Scan 01: Build-aware scan of auth0/java-jwt

| Field | Value |
| --- | --- |
| Target | https://github.com/auth0/java-jwt |
| Commit | 29f252b2d6fea74df6576803d5fce5f6d6799cf3 (Release 4.6.1), the same commit as scan 00 |
| Tool | `cbomkit/cbomkit-action@v2.3.0`, with `CBOMKIT_LANGUAGES: java` |
| Workflow | [`.github/workflows/scan-java-jwt-build-aware.yml`](../../.github/workflows/scan-java-jwt-build-aware.yml), at this repository's commit `a1c2bc8` |
| Artifact | `CBOM-java-jwt-build-aware` |
| Date | 2026-10-15 |

## How it differs from scan 00

Scan 00 ran cbomkit on unbuilt source. This scan first built java-jwt with Gradle (`./gradlew assemble`, Java 11), copied every jar from the runner's Gradle dependency cache (which includes java-jwt's resolved dependencies) into `deps/` in the workspace, and then ran cbomkit-action.

## Result

The action produced two CBOMs, both committed here unedited:

| File | Scope | Components |
| --- | --- | --- |
| `cbom.json` | Whole checkout | 0 |
| `cbom_lib.json` | `lib` subfolder (the Gradle module containing `CryptoHelper.java`) | 0 |

Running the scan after a Gradle build, with the jars copied into the workspace, did not change the result: the scanner again reported zero cryptographic assets for a commit with five confirmed JCA call sites (see [scan 00](../00-trial-java-jwt/)).

## Metadata caveat

The `metadata.properties` in both files record `gitUrl` as `https://github.com/amaanparvezs/pqc-migration-project` and `commit` as `a1c2bc8ea44dd11d682430ca6a7a3a856ed4c188`. These identify the repository and commit that ran the workflow, not the code that was scanned. The scanned code is auth0/java-jwt at `29f252b`, as set by the checkout step in the workflow. When a CBOM is generated in one repository's CI for another repository's code, its provenance metadata cannot be taken at face value.

## Run log review (2026-10-22)

The log of the "Create CBOM" step in run #3 shows:

- `Found project module 'lib' [47 .java files]`, `No "Test" source files to scan`, `Scanned 47 files with 4692 lines`, and both CBOMs written `with 0 findings`.
- `WARN org.sonar.java.SonarComponents - Unresolved imports/types have been detected during analysis.` The log does not say which types; cbomkit-action v2.3.0 has no documented option to enable DEBUG output.
- No line that mentions `deps/`, jars, compiled classes or a classpath.

The tool's source explains why the absence of an error proves nothing about the build:

- cbomkit-action always adds the workspace directory to the scanner's jar and class-directory lists ([`Main.java` lines 105 to 115](https://github.com/cbomkit/cbomkit-action/blob/v2.3.0/src/main/java/org/pqca/Main.java#L105-L115)).
- Its "project must be built" check fails only if both lists are empty, so it always passes ([`JavaScannerService.java` lines 127 to 143](https://github.com/cbomkit/cbomkit-lib/blob/v1.3.0/src/main/java/org/pqca/scanning/java/JavaScannerService.java#L127-L143), cbomkit-lib v1.3.0).

**Why 47 files:** by default the indexer excludes `src/test/`, `module-info.java`, `package-info.java` and files ending in `Test.java` ([`JavaIndexService.java`](https://github.com/cbomkit/cbomkit-lib/blob/v1.3.0/src/main/java/org/pqca/indexing/java/JavaIndexService.java)). Module `lib` therefore contains the 48 `src/main` files minus `module-info.java` and `package-info.java`, plus the JMH benchmark in `lib/src/jmh`: 47.

## What this does and does not show

- **Shows:** a Gradle build followed by cbomkit-action v2.3.0 still reports zero assets for this commit.
- **Does not show:** that the compiled classes or the dependency jars were used. The log gives no evidence of it and reports unresolved types, and the action's build check passes either way.
- **Effect on the hypotheses (updated 2026-10-22):** the build explanation remains open; this scan is inconclusive about it. The indirection hypothesis was then tested with a controlled fixture: see [scan 03](../03-fixture-case-a-b/).
