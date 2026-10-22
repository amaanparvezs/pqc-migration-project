# PQC Migration Project

An end-to-end post-quantum migration of an open-source codebase, driven by its Cryptographic Bill of Materials (CBOM).

## Goal

Take a real open-source repository through the full migration path a client engagement would follow:

1. **Discovery:** generate a CBOM with cbomkit, then verify it manually against the source.
2. **Prioritization:** classify the cryptographic assets and rank them by harvest-now-decrypt-later exposure and migration cost.
3. **Migration:** move two or three assets to hybrid or post-quantum schemes (liboqs, oqs-provider, BouncyCastle).
4. **Benchmarking:** measure before and after, with warm-up, repeated runs and reported spread.
5. **Report:** a client-style write-up of findings and recommendations.

## Status

| Phase | State |
|---|---|
| 0. Trial scan and tool validation | In progress |
| 1. Discovery on the main target | Not started (target not yet chosen) |
| 2. Prioritization | Not started |
| 3. Migration | Not started |
| 4. Benchmarking | Not started |
| 5. Report | Not started |

## Finding so far

On a trial target, [auth0/java-jwt](https://github.com/auth0/java-jwt) at release 4.6.1, cbomkit v2.3.0 reported **zero cryptographic assets**. A manual review of the same commit found **five JCA call sites** (three `Signature.getInstance` and two `Mac.getInstance` calls), all in one helper class that receives the algorithm name as a method parameter.

A build-aware scan in GitHub Actions (Gradle build, dependency jars in the workspace, then cbomkit-action v2.3.0) also reported zero. Its log gives no evidence that the build output was used and reports unresolved types, so that test was inconclusive.

A controlled fixture then isolated the pattern. The same `Signature.getInstance` call was **detected** when the algorithm name was written at the call, and was **absent from the CBOM** when the name was passed through a factory method, a constructor, a field, a getter and a helper method in another class. Both cases ran in the same scan, with no unresolved types reported. java-jwt passes its algorithm names to all five call sites through a longer chain of the same kind.

Still open: the exact step at which tracking stops, and whether java-jwt's unresolved types also contribute.

Details: [`scans/00-trial-java-jwt`](scans/00-trial-java-jwt/), [`scans/01-java-jwt-build-aware`](scans/01-java-jwt-build-aware/), [`scans/02-fixture-case-a`](scans/02-fixture-case-a/), [`scans/03-fixture-case-a-b`](scans/03-fixture-case-a-b/) and the fixture, [`fixtures/indirection`](fixtures/indirection/).

## Repository layout

```
.
├── scans/          one folder per scan: raw tool output plus a README with target, commit, tool version and analysis
├── fixtures/       small test programs that check what the scanner detects
├── .github/
│   └── workflows/  reproducible scan workflows (GitHub Actions)
├── DEVLOG.md       dated log of decisions, results, findings and next steps
└── README.md
```

Target repositories are cloned into `targets/`, which is git-ignored.

## Conventions

- Raw tool output is committed exactly as produced and never edited. Analysis lives in separate files.
- Every scan records the tool version and the target's full commit hash.
- Claims are marked as verified or as hypotheses until tested.

## Author

Amaan Parvez, Junior Research Fellow in post-quantum cryptography, IIITDM Kurnool.