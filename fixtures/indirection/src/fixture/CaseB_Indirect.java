package fixture;

import java.security.PrivateKey;

public class CaseB_Indirect {
    private final String description;
    private final CaseB_Helper crypto;

    CaseB_Indirect(String description) {
        this.description = description;
        this.crypto = new CaseB_Helper();
    }

    public static CaseB_Indirect rsa256() {
        return new CaseB_Indirect("SHA256withRSA");
    }

    String getDescription() {
        return description;
    }

    public byte[] sign(PrivateKey key, byte[] data) throws Exception {
        return crypto.createSignatureFor(getDescription(), key, data);
    }
}