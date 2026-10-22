# Scan 00: Trial scan of auth0/java-jwt

| Field | Value |
| --- | --- |
| Target | https://github.com/auth0/java-jwt |
| Commit | 29f252b2d6fea74df6576803d5fce5f6d6799cf3 (Release 4.6.1) |
| Tool | cbomkit v2.3.0, run locally via `make production`, scan started from the web UI |
| Date | 2026-10-09 |

## Result
cbomkit reported "Scan finished": 47 files, about 4.7K lines, **0 cryptographic assets**.
`cbom.json` contains metadata only and no `components`. It is the raw tool output and has not been edited.

The repository has 77 Java files, but only 47 were scanned. The cbomkit-action run of [scan 01](../01-java-jwt-build-aware/) reports the same 47 files and shows why: tests, `module-info.java` and `package-info.java` are excluded by default, and the JMH benchmark is included.

## Manual review of the same commit
- The repository contains 77 Java files: 48 in `src/main`, 28 in `src/test`, and 1 JMH benchmark (`lib/src/jmh/java/com/auth0/jwt/benchmark/JWTDecoderBenchmark.java`).
- `src/main` contains 5 JCA call sites, all in `lib/src/main/java/com/auth0/jwt/algorithms/CryptoHelper.java`:
  - `Signature.getInstance(algorithm)` at lines 157, 209, 302
  - `Mac.getInstance(algorithm)` at lines 235, 255
- In every call, `algorithm` is a `String` method parameter, not a literal.
- The literal names (for example `"SHA256withRSA"`, `"HmacSHA256"`, `"SHA256withECDSA"`) are set in the factory methods of `Algorithm.java` (for example line 32). They reach `CryptoHelper` through the subclass constructor, `super(id, algorithm)`, the `description` field, and `getDescription()`.

## Status
The cause of the zero result is not yet established. Working hypothesis: cbomkit misses calls whose algorithm name arrives through a method parameter. This will be tested with a controlled fixture.

**Update (2026-10-22):** a build-aware scan was inconclusive ([scan 01](../01-java-jwt-build-aware/)), and a controlled fixture showed that this kind of indirection alone makes the scanner miss a JCA call ([scan 03](../03-fixture-case-a-b/)).
