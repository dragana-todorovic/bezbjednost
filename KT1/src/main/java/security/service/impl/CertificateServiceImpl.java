package security.service.impl;

import java.util.List;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.CertIOException;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import security.model.CertificateForAdding;
import security.model.StringResponse;
import security.pki.GenerateTestsCertificates;
import security.pki.certificates.CertificateGenerator;
import security.pki.data.IssuerData;
import security.pki.data.SubjectData;
import security.pki.keystores.KeyStoreReader;
import security.pki.keystores.KeyStoreWriter;
import security.service.CertificateService;

import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {
	
	KeyStoreReader keyStoreReader = new KeyStoreReader();
	KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
	
	public List<String> getAllCertificates() {
		
		List<X509Certificate>certificates=getCertificates();

        List<String> subjectData = new ArrayList<>();
        for (X509Certificate c : certificates)
        {
            subjectData.add(c.getSubjectX500Principal().getName() + "+" + c.getNotBefore().toString() + "+" + c.getNotAfter().toString() + "+" + c.getIssuerX500Principal().getName() + "+");
        }

        List<String> nameAndUid = new ArrayList<>();
        for (String s : subjectData)
        {
        	
            String split = s.split(",")[5].split("=")[1]+ "(" + s.split(",")[0] + ")" + s.split(",")[10].split("=")[1]  ;
            nameAndUid.add(split.split("\\+")[0] + split.split("\\+")[3] + "+" + s.split("\\+")[1] + "+" + s.split("\\+")[2] + "+" + s.split(",")[1].split("=")[1]);
            System.out.println(nameAndUid);
        }
        
        return nameAndUid;
      
    }

	@Override
	public void pullCertificate(String uid) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException{

			List<X509Certificate>certificates=getCertificates();

	        Certificate cert = null;
	        String certificateUID = "";

	        for (X509Certificate certificate : certificates)
	        {
	        	certificateUID = certificate.getSubjectX500Principal().getName().split(",")[0].split("\\=")[1];
	        	
	            if(certificateUID.equals(uid))
	            {
	            	if (certificate != null)
	    	            cert = (X509Certificate) certificate;
	            }
	        }
	        
	        String allPulledFromFile = readFromFileAllPulledCertificates();
	        

	        //Pisanje
	        try(BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("./src/main/resources/keystores/ocsp.txt"))) {
	            String certificateForWrite = cert.toString() + "KRAJ";

	            if(allPulledFromFile != "") {
	                if (!doesPulledCertificateAlreadyExistInFile(allPulledFromFile, uid)) { // Upisujemo novi sertifikat samo ako ga nismo pronasli da je vec upisan, jer nema smisla 2x upisati isti sertiikat
	                	allPulledFromFile = allPulledFromFile + certificateForWrite;
	                }
	                bufferedOutputStream.write(allPulledFromFile.getBytes());
	            }
	            else { //Prvi put ako upisujemo u fajl
	                bufferedOutputStream.write(certificateForWrite.getBytes());
	            }
	        } catch (IOException e) {
	            // exception handling
	        }
	    
	
		
	}
	

	@Override
	public Boolean checkCertificate(String uid) throws NoSuchProviderException, KeyStoreException, IOException,
			CertificateException, NoSuchAlgorithmException {
		   	
			Boolean isValid = true; 
			List<X509Certificate>certificates=getCertificates();	       

	        X509Certificate x509Certificate = null;



	        String uidIssuar = "";
	        String uidSubject = "";
	        uidSubject = uid;

	        boolean endWhile = false;
	        do {
	            for (X509Certificate certificate : certificates)
	            {
	           
	                if(certificate.getSubjectX500Principal().getName().split(",")[0].split("\\=")[1].equals(uidSubject))
	                {	                   
	                    x509Certificate = certificate;
	                    if(new Date().compareTo(x509Certificate.getNotAfter()) == 1) {
	                        pullCertificate(uid);
	                        System.out.println("usaoooo");
	                        isValid = false;
	                    }
	                    
	                    uidIssuar = x509Certificate.getIssuerX500Principal().getName().split(",")[0];
	                    System.out.println("uidIsuuar"+uidIssuar);
	                }
	            }
	          

	            String allPulledFromFile = readFromFileAllPulledCertificates();

	          
	            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("./src/main/resources/keystores/ocsp.txt"))) {

	                if (allPulledFromFile != "") {
	                    String certs[] = allPulledFromFile.split("KRAJ");

	                    for (String s : certs) {
	                        String str[] = s.split(" ");
	                        for (int i = 0; i < str.length; i++)

	                            if (str[i].split("=")[0].equals("UID")) {
	                                if (str[i].split("=")[1].split(",")[0].equals(uidSubject)) {
	                                    isValid = false;
	                                    break;
	                                }
	                                break;
	                            }
	                    }
	                    bufferedOutputStream.write(allPulledFromFile.getBytes());
	                }

	            } catch (IOException e) {
	                e.printStackTrace();
	            }

	            if(uidSubject.equals(uidIssuar.split("\\=")[1]))
	                endWhile = true;

	            uidSubject = uidIssuar.split("\\=")[1];
	        } while(endWhile != true);	       

	        return isValid;
		
	}
	public String readFromFileAllPulledCertificates() throws IOException {
		String allPulledFromFile = "";
		try(BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("./src/main/resources/keystores/ocsp.txt"))) {
            int ch = bufferedInputStream.read();
            while(ch != -1) {
                allPulledFromFile = allPulledFromFile + (char)ch;
                ch = bufferedInputStream.read();
            }
        } catch (FileNotFoundException e) {

        }
		return allPulledFromFile;
	}
	
	public Boolean doesPulledCertificateAlreadyExistInFile(String allPulledFromFile, String uid) {
		 String certs[] = allPulledFromFile.split("KRAJ");
         Boolean n = false;
         for(String s : certs){
             String str[] = s.split(" ");
             for (int i=0; i < str.length; i++)
                 if (str[i].split("=")[0].equals("UID"))
                 {
                     if ( str[i].split("=")[1].split(",")[0].equals(uid)){
                         n = true;
                         break;
                     }
                     break;
                 }
	}
         return n;
}
	  
	public List<X509Certificate> getCertificates() {
		 List<X509Certificate> certificates = keyStoreReader.getX509Certificates("./src/main/resources/keystores/root.jks", "12345");
	        List<X509Certificate> certificates1 = keyStoreReader.getX509Certificates("./src/main/resources/keystores/intermediate.jks", "12345");
	        List<X509Certificate> certificates2 = keyStoreReader.getX509Certificates("./src/main/resources/keystores/endEntity.jks", "12345");

	        certificates.addAll(certificates1);
	        certificates.addAll(certificates2);
		
	        return certificates;		
	}

	@Override
	public StringResponse downloadCertificate(String uid) throws NoSuchProviderException, KeyStoreException, IOException,
			CertificateException, NoSuchAlgorithmException {
		StringResponse response = new StringResponse();
		List<X509Certificate> certificates = getCertificates();
		
		String certificateUID = "";
		
		Certificate cert = null;
		
		 for (X509Certificate certificate : certificates)
	        {
	        	certificateUID = certificate.getSubjectX500Principal().getName().split(",")[0].split("\\=")[1];
	        	
	            if(certificateUID.equals(uid))
	            {
	            	if (certificate != null)
	            		
	    	            cert = (X509Certificate) certificate;
	            }
	        }
		 String encodedCert = Base64.getEncoder().encodeToString((cert.getEncoded()));
		 response.setResponse(encodedCert);
		 return response;
	}

	@Override
	public void addCertificate(CertificateForAdding certificate) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		//Pravimo Subject
        GenerateTestsCertificates grc = new GenerateTestsCertificates();
        KeyPair keyPairSubject = grc.generateKeyPair();


        X500NameBuilder x500NameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        x500NameBuilder.addRDN(BCStyle.CN, certificate.getFullName());
        x500NameBuilder.addRDN(BCStyle.SURNAME, certificate.getSurname());
        x500NameBuilder.addRDN(BCStyle.GIVENNAME, certificate.getGivenName());
        x500NameBuilder.addRDN(BCStyle.E, certificate.getEmail());
        x500NameBuilder.addRDN(BCStyle.C, certificate.getSpeciality());
        x500NameBuilder.addRDN(BCStyle.UID, certificate.getUid());

        //Kreiranje random serijskog broja koji ne postoji ni u jednom sertifikatu
        List<X509Certificate>certificates = getCertificates();

        String serialNumber_1;
        boolean p;
        SecureRandom rand = new SecureRandom();

        do {
            int newSerialNumberOfSubject = rand.nextInt(1000);
            serialNumber_1 = String.valueOf(newSerialNumberOfSubject);

            p = false;
            for (X509Certificate cer : certificates) {
                if ( cer.getSerialNumber() == BigInteger.valueOf(Integer.parseInt(serialNumber_1))) {
                    p = true;
                }
            }
        }while(p);

        SubjectData subjectData = new SubjectData(keyPairSubject.getPublic(), x500NameBuilder.build(), serialNumber_1, certificate.getValidFrom(), certificate.getValidTo());
        //-----------------------------------------------
        CertificateGenerator certificateGenerator2 = new CertificateGenerator();
        char[] password ={ '1', '2', '3', '4', '5' };
        KeyStore ks = KeyStore.getInstance("JKS", "SUN");
        //Pravimo Issuar
        if(certificate.getSpeciality().equals("ca")) {
        	List<X509Certificate> certificatesRoot = keyStoreReader.getX509Certificates("./src/main/resources/keystores/root.jks", "12345");
        
        	 X509Certificate x509Certificate = null;
             Certificate cert = null;
             
             BufferedInputStream in = new BufferedInputStream(new FileInputStream("./src/main/resources/keystores/root.jks"));
             ks.load(in, "12345".toCharArray());
             
             String alias = "";
             
             
             IssuerData issuerDataRoot = null;
             
             for (X509Certificate c : certificatesRoot)
             {
                 if(c.getSubjectX500Principal().toString().split(",")[0].split("=")[1].equals(certificate.getIssuer()))
                 {
                     x509Certificate = c;
                 }
             }
         	cert = (X509Certificate)x509Certificate;
         	alias = ks.getCertificateAlias(cert);
         	
         	try{
                issuerDataRoot = keyStoreReader.readIssuerFromStore("./src/main/resources/keystores/root.jks", alias, password, password);
               
            }
            catch(Exception e){
                issuerDataRoot = null;
            }
         	
             X509Certificate x509Certificate2 = certificateGenerator2.generateCertificate(subjectData, issuerDataRoot);
        
             PrivateKey privKey = (PrivateKey) ks.getKey(alias, password);
             keyStoreWriter.loadKeyStore("./src/main/resources/keystores/intermediate.jks", password);
             keyStoreWriter.write(certificate.getAlias(), privKey, password, x509Certificate2);
             keyStoreWriter.saveKeyStore("./src/main/resources/keystores/intermediate.jks", password);
        } else if (certificate.getSpeciality().equals("endEntity")) {
        	
        	List<X509Certificate> certificatesCA = keyStoreReader.getX509Certificates("./src/main/resources/keystores/intermediate.jks", "12345");
            
       	 	X509Certificate x509Certificate = null;
            Certificate cert = null;
            BufferedInputStream in = new BufferedInputStream(new FileInputStream("./src/main/resources/keystores/intermediate.jks"));
            ks.load(in, "12345".toCharArray());
            
            String alias = "";
            IssuerData issuerDataCA = null;
            
            for (X509Certificate c : certificatesCA)
            {
                if(c.getSubjectX500Principal().toString().split(",")[0].split("=")[1].equals(certificate.getIssuer()))
                {
                    x509Certificate = c;
                }
            }
        	cert = (X509Certificate)x509Certificate;
        	alias = ks.getCertificateAlias(cert);
        	
        	try{
               issuerDataCA = keyStoreReader.readIssuerFromStore("./src/main/resources/keystores/intermediate.jks", alias, password, password);
              
           }
           catch(Exception e){
               issuerDataCA = null;
           }
        	
        	
            X509Certificate x509Certificate2 = certificateGenerator2.generateCertificate(subjectData, issuerDataCA);
       
            PrivateKey privKey = (PrivateKey) ks.getKey(alias, password);
            keyStoreWriter.loadKeyStore("./src/main/resources/keystores/endEntity.jks", password);
            keyStoreWriter.write(certificate.getAlias(), privKey, password, x509Certificate2);
            keyStoreWriter.saveKeyStore("./src/main/resources/keystores/endEntity.jks", password);
        	
        }
        
        
        else {
           IssuerData issuerDataRoot = null;
           KeyPair keyPair = grc.generateKeyPair();
	       x500NameBuilder.addRDN(BCStyle.CN, certificate.getFullName());
	       x500NameBuilder.addRDN(BCStyle.SURNAME, certificate.getSurname());
	       x500NameBuilder.addRDN(BCStyle.GIVENNAME, certificate.getGivenName());
	       x500NameBuilder.addRDN(BCStyle.E, certificate.getEmail());
	       x500NameBuilder.addRDN(BCStyle.C, certificate.getSpeciality());
	       x500NameBuilder.addRDN(BCStyle.UID, certificate.getUid());

	       issuerDataRoot = new IssuerData(keyPair.getPrivate(), x500NameBuilder.build());
	       
	       X509Certificate x509Certificate2 = certificateGenerator2.generateCertificate(subjectData, issuerDataRoot);
	       
	       keyStoreWriter.loadKeyStore("./src/main/resources/keystores/root.jks", password);
           keyStoreWriter.write(certificate.getAlias(), keyPair.getPrivate(), password, x509Certificate2);
           keyStoreWriter.saveKeyStore("./src/main/resources/keystores/root.jks", password);
        }
        
	}     
}

