package security.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;

import org.bouncycastle.cert.CertIOException;

import java.security.cert.Certificate;

import security.model.CertificateForAdding;
import security.model.StringResponse;

public interface CertificateService {
	
	List<String> getAllCertificates();
	void pullCertificate(String uid) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException;
	Boolean checkCertificate(String uid) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException;
	StringResponse downloadCertificate(String uid) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException;
	void addCertificate(CertificateForAdding certificate)  throws CertIOException, KeyStoreException, NoSuchProviderException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, IOException ;
}
