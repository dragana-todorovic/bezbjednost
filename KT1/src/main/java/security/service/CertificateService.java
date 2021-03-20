package security.service;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;

public interface CertificateService {
	
	List<String> getAllCertificates();
	void pullCertificate(String uid) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException;
	Boolean checkCertificate(String uid) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException;
}
