# Scan 02: Indirection fixture, case (a) only

| Field | Value |
| --- | --- |
| Target | This repository, `fixtures/indirection/` |
| Commit | ff7e606f1cde0e76a86b55f89ad7ea41f9969673 |
| Tool | `cbomkit/cbomkit-action@v2.3.0`, with `CBOMKIT_LANGUAGES: java` |
| Workflow | [`.github/workflows/scan-fixture-indirection.yml`](../../.github/workflows/scan-fixture-indirection.yml) |
| Artifact | `CBOM-fixture-indirection` |
| Date | 2026-10-22 |

## Purpose

Control case. Before testing indirection, confirm that this scanning setup detects the simplest possible JCA call: an algorithm name written as a literal at the call site. The fixture uses only the JDK, so there are no dependency jars to resolve.

Code scanned (`CaseA_Literal.java`, line 8):

    Signature s = Signature.getInstance("SHA256withRSA");

The cases are defined in [`fixtures/indirection/README.md`](../../fixtures/indirection/README.md).

## Result

Log: `Found project module 'fixtures/indirection/src/fixture' [1 .java files]`, `Detected (Signature) RSA-PKCS1-1.5-SHA-256`, both CBOMs written `with 2 findings`. No "unresolved imports/types" warning (present in the java-jwt scan 01 log).

| File | Components |
| --- | --- |
| `cbom.json` | 2 |
| `cbom_fixtures.indirection.src.fixture.json` | 2 |

Both components are located at `CaseA_Literal.java` line 8:

| Component | Primitive | Function |
| --- | --- | --- |
| `RSA-PKCS1-1.5-SHA-256` | signature | sign |
| `SHA-256` | hash | digest (the digest inside the signature) |

Both files are committed exactly as produced.

## What this shows

- The setup works: the scanner resolves JDK types and detects a literal JCA signature call. A general failure of the tool or of this CI setup is ruled out. This does not rule out problems specific to the java-jwt scans, such as the unresolved types reported in scan 01.
- The CBOM metadata correctly identifies this repository and commit, because the scanned code lives in the repository that ran the workflow (unlike scan 01).

## Notes

- The CBOM records `parameterSetIdentifier` 2048 for the RSA signature. The code never sets a key size (the key is a method parameter), so this value is supplied by the tool, not derived from the source.
- At this commit, a compiled `CaseA_Literal.class` was also committed in `src/fixture/` by mistake. It was removed later; scan 03 was run on a clean commit.
