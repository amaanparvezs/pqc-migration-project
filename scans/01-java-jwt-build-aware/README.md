# Scan 01: Build-aware scan of auth0/java-jwt

| Field | Value |
| --- | --- |
| Target | https://github.com/auth0/java-jwt |
| Commit | 29f252b2d6fea74df6576803d5fce5f6d6799cf3 (Release 4.6.1), the same commit as scan 00 |
| Tool | `cbomkit/cbomkit-action@v2.3.0`, with `CBOMKIT_LANGUAGES: java` |
| Workflow | [`.github/workflows/scan-java-jwt-build-aware.yml`](../../.github/workflows/scan-java-jwt-build-aware.yml), at this repository's commit `a1c2bc8` |
| Run | GitHub Actions run #3 (ID 38724925875), artifact `CBOM-java-jwt-build-aware` |
| Date | 2026-10-15 |

## How it differs from scan 00

Scan 00 ran cbomkit on unbuilt source. This scan first built java-jwt with Gradle (`./gradlew assemble`, Java 11), copied the resolved dependency jars into the workspace, and then ran cbomkit-action.

## Result

The action produced two CBOMs, both committed here unedited:

| File | Scope | Components |
| --- | --- | --- |
| `cbom.json` | Whole checkout | 0 |
| `cbom_lib.json` | `lib` subfolder (the Gradle module containing `CryptoHelper.java`) | 0 |

Building the target and adding its dependency jars did not change the result: the scanner again reported zero cryptographic assets for a commit with five confirmed JCA operations (see [scan 00](../00-trial-java-jwt/)).

## Metadata caveat

The `metadata.properties` in both files record `gitUrl` as `https://github.com/amaanparvezs/pqc-migration-project` and `commit` as `a1c2bc8ea44dd11d682430ca6a7a3a856ed4c188`. These identify the repository and commit that ran the workflow, not the code that was scanned. The scanned code is auth0/java-jwt at `29f252b`, as set by the checkout step in the workflow. When a CBOM is generated in one repository's CI for another repository's code, its provenance metadata cannot be taken at face value.

## What this does and does not show

- **Shows:** a Gradle build followed by cbomkit-action v2.3.0 still reports zero assets for this commit.
- **Does not yet show:** whether the scanner actually used the compiled classes or the dependency jars. This has not been checked in the run log.
- **Effect on the hypothesis:** "the scan missed the calls because the repository was not built" is now less likely, though not ruled out until the log is checked. The working hypothesis, that algorithm names passed through method parameters are not traced, remains untested. A controlled fixture is the next test.