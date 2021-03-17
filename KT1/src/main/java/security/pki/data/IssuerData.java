package security.pki.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.PrivateKey;

@NoArgsConstructor
@AllArgsConstructor
public class IssuerData {

    private X500Name x500Name;
    private PrivateKey privateKey;

    public IssuerData(PrivateKey privateKey, X500Name x500name) {
        this.privateKey = privateKey;
        this.x500Name = x500name;
    }
    public X500Name getX500Name() {
    	return x500Name;
    }
    public void set500Name(X500Name x500Name) {
    	this.x500Name = x500Name;
    }
    public PrivateKey getPrivateKey() {
    	return privateKey;
    }
    public void setPrivateKey(PrivateKey privateKey) {
    	this.privateKey = privateKey;
    }
}
