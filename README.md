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

On a trial target, [auth0/java-jwt](https://github.com/auth0/java-jwt) at release 4.6.1, cbomkit v2.3.0 reported **zero cryptographic assets**. A manual review of the same commit found **five JCA operations** (three `Signature.getInstance` and two `Mac.getInstance` calls), all in one helper class that receives the algorithm name as a method parameter.

The cause is not yet established. The working hypothesis is that the scanner misses algorithm choices passed through helper methods. A build-aware scan and a controlled test fixture are being used to test this. Details: [`scans/00-trial-java-jwt`](scans/00-trial-java-jwt/).

## Repository layout

```
.
├── scans/          one folder per scan: raw tool output plus a README with target, commit, tool version and analysis
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