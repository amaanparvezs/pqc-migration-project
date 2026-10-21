# Devlog

Dated record of project decisions, results, findings and next steps. Newest entries at the bottom.

---

## 2026-10-09

**Did:** Ran a trial CBOM scan of auth0/java-jwt at commit `29f252b2d6fea74df6576803d5fce5f6d6799cf3` (release 4.6.1) with cbomkit v2.3.0, run locally via `make production`.

**Decision:** Used java-jwt as a small Phase 0 trial target to validate the tooling before committing to a main target. A first attempt on keycloak was abandoned as too slow for a trial.

**Result:** cbomkit reported "Scan finished" with 47 files, about 4.7K lines and 0 cryptographic assets. The CBOM contains metadata only, no `components`.

**Finding:** Manual review of the same commit found 5 JCA calls in `src/main`, all in `CryptoHelper.java`: `Signature.getInstance(algorithm)` at lines 157, 209 and 302, and `Mac.getInstance(algorithm)` at lines 235 and 255. In every call the algorithm name is a `String` method parameter. The literal names are set in the factory methods of `Algorithm.java` and reach `CryptoHelper` through the subclass constructor and `getDescription()`. cbomkit's README notes that it does not build repositories, so some Java symbols may not be resolved.

**Next:** Run a build-aware scan of the same commit to see whether building changes the result.

---

## 2026-10-15

**Did:** Committed the trial scan (`scans/00-trial-java-jwt`). Added a GitHub Actions workflow (`scan-java-jwt-build-aware.yml`) that checks out java-jwt at the trial commit, builds it with Gradle, copies the dependency jars into the workspace and runs `cbomkit/cbomkit-action@v2.3.0`.

**Decision:** Used cbomkit-action in CI rather than a local SonarQube setup, because it can scan after a real build and the workflow itself documents exactly how the scan was produced.

**Result:** Run 1 failed; the follow-up commit fixed a Java version mismatch (the workflow now uses Java 11). Runs 2 and 3 completed. Run 3 (commit `a1c2bc8`, with dependency jars copied into the workspace) uploaded an artifact, `CBOM-java-jwt-build-aware` (974 bytes).

**Finding:** None yet. The artifact has not been downloaded or analyzed.

**Next:** Download the artifact, commit it unedited as `scans/01-java-jwt-build-aware/cbom.json`, and compare its component count with the 0 from the unbuilt scan.

---

## 2026-10-21

**Did:** Downloaded the `CBOM-java-jwt-build-aware` artifact from run #3 and committed its two files unedited as `scans/01-java-jwt-build-aware/` (`cbom.json` and `cbom_lib.json`).

**Result:** Both CBOMs contain 0 components. `cbom.json` covers the whole checkout; `cbom_lib.json` covers the `lib` subfolder, which is the Gradle module containing `CryptoHelper.java`.

**Finding:** Building java-jwt with Gradle and adding its dependency jars did not change the result from scan 00. Separately, the CBOM metadata records this repository (`amaanparvezs/pqc-migration-project` at `a1c2bc8`) as the scanned source, not auth0/java-jwt at `29f252b`. When cbomkit-action runs in one repository's CI against another repository's code, the provenance fields describe the CI repository.

**Next:** Check the run #3 log to confirm whether the scanner loaded the compiled classes and dependency jars. Then build the controlled fixture: one Java file per algorithm-choice pattern (string literal at the call site, name passed through a helper method parameter, name read from configuration), scanned with the same workflow.