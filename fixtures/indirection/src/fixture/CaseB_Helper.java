package fixture;

import java.security.PrivateKey;
import java.security.Signature;

class CaseB_Helper {
    byte[] createSignatureFor(String algorithm, PrivateKey key, byte[] data) throws Exception {
        Signature s = Signature.getInstance(algorithm);
        s.initSign(key);
        s.update(data);
        return s.sign();
    }
}