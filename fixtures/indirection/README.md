# Indirection fixture

A minimal Java program for testing whether a CBOM scanner detects a cryptographic call when the algorithm name is not written at the call itself.

## Why it exists

cbomkit v2.3.0 reported zero cryptographic assets for auth0/java-jwt 4.6.1, although its source contains five JCA call sites (see [scan 00](../../scans/00-trial-java-jwt/)). In all five, the algorithm name reaches `getInstance` as a method parameter whose value is set elsewhere. This fixture isolates that pattern so it can be tested on its own.

## Design

Every case performs exactly the same operation:

    Signature s = Signature.getInstance(<algorithm name>);
    s.initSign(key);
    s.update(data);
    return s.sign();

The only difference between cases is **how the algorithm name `"SHA256withRSA"` reaches `getInstance`**. If one case is detected and another is not, that difference is the cause. The fixture uses only the JDK, so there are no dependencies for the scanner to resolve.

## Cases

| Case | Files | How the algorithm name reaches `getInstance` | Mirrors |
| --- | --- | --- | --- |
| (a) Literal | `CaseA_Literal.java` | Written directly at the call: `getInstance("SHA256withRSA")` | The simplest possible case; used as the control |
| (b) Indirect | `CaseB_Indirect.java`, `CaseB_Helper.java` | Written in a factory method, passed to a constructor, stored in a field, read back by a getter, and passed as a parameter to a helper method in another class | A simplified version of auth0/java-jwt 4.6.1: `Algorithm.java` (factory method, `description` field, `getDescription()`), `RSAAlgorithm.java` (constructor, call into the helper), `CryptoHelper.java` (the `getInstance` call). java-jwt additionally passes the name through a superclass and between constructors. |

## Results

| Scan | Cases present | Detected |
| --- | --- | --- |
| [Scan 02](../../scans/02-fixture-case-a/) | (a) | (a) |
| [Scan 03](../../scans/03-fixture-case-a-b/) | (a), (b) | (a) only; (b) is absent from the CBOM |

## How to reproduce

Compile locally:

    javac --release 11 -d fixtures/indirection/build $(find fixtures/indirection/src -name '*.java')

Scan: run the workflow [`scan-fixture-indirection.yml`](../../.github/workflows/scan-fixture-indirection.yml) from the Actions tab and download the `CBOM-fixture-indirection` artifact.