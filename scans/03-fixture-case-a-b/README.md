# Scan 03: Indirection fixture, cases (a) and (b)

| Field | Value |
| --- | --- |
| Target | This repository, `fixtures/indirection/` |
| Commit | <full commit hash of the clean rerun> |
| Tool | `cbomkit/cbomkit-action@v2.3.0`, with `CBOMKIT_LANGUAGES: java` |
| Workflow | [`.github/workflows/scan-fixture-indirection.yml`](../../.github/workflows/scan-fixture-indirection.yml) |
| Run | GitHub Actions run #<number>, artifact `CBOM-fixture-indirection` |
| Date | <date of the rerun> |

## Purpose

Test whether the java-jwt code pattern alone causes the scanner to miss a JCA call. Case (b) performs exactly the same signature operation as case (a); the only difference is how the algorithm name reaches `Signature.getInstance`. Case (a) is kept in the same run as a control.

| Case | Files | How `"SHA256withRSA"` reaches `getInstance` |
| --- | --- | --- |
| (a) | `CaseA_Literal.java` | Written at the call site |
| (b) | `CaseB_Indirect.java`, `CaseB_Helper.java` | Factory method `rsa256()`, then constructor, then field `description`, then `getDescription()`, then a parameter of `CaseB_Helper.createSignatureFor(...)` in another class |

Case (b) mirrors java-jwt 4.6.1: `Algorithm.java` line 32 (factory), the `RSAAlgorithm` constructor and `super(id, algorithm)`, the `description` field, `getDescription()`, `RSAAlgorithm.java` line 80, and `CryptoHelper.java` line 157.

The cases are defined in [`fixtures/indirection/README.md`](../../fixtures/indirection/README.md).

## Result

Log: `Found project module 'fixtures/indirection/src/fixture' [3 .java files]`, one `Detected (Signature) RSA-PKCS1-1.5-SHA-256`, both CBOMs written `with 2 findings`. No "unresolved imports/types" warning.

| Case | Detected | Components |
| --- | --- | --- |
| (a) | Yes | `RSA-PKCS1-1.5-SHA-256` and `SHA-256`, at `CaseA_Literal.java` line 8 |
| (b) | No | None. The `getInstance(algorithm)` call in `CaseB_Helper.java` does not appear in the CBOM at all, not even as an unknown algorithm. |

Both files are committed exactly as produced.

## What this shows

- cbomkit-action v2.3.0 does not report a JCA call whose algorithm name is passed through a factory method, constructor, field, getter and a method parameter in another class, while it detects the identical call with a literal name in the same run.
- No types were unresolved in this run, so the miss is caused by the indirection itself.
- java-jwt uses this pattern for all five of its JCA calls, which is sufficient to explain the zero result of scans 00 and 01.

## What this does not show

- Which step of the chain stops detection (the field, the getter, or the call into another class). Further cases will narrow this down.
- Whether java-jwt's unresolved types (scan 01 log) also contribute to its result.

## Notes

- An earlier run of the same cases at commit `eca650c`, with compiled `.class` files accidentally committed in the workspace, gave the same result. This scan was rerun after removing them.