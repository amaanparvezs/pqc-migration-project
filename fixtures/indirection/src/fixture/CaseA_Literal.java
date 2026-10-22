package fixture;

import java.security.PrivateKey;
import java.security.Signature;

public class CaseA_Literal {
    public byte[] sign(PrivateKey key, byte[] data) throws Exception {
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(key);
        s.update(data);
        return s.sign();
    }
}