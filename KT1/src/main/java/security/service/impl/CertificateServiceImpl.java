package security.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import security.model.StringResponse;
import security.pki.keystores.KeyStoreReader;
import security.service.CertificateService;

import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {
	
	KeyStoreReader keyStoreReader = new KeyStoreReader();
	
	public List<String> getAllCertificates() {
		
		List<X509Certificate>certificates=getCertificates();

        List<String> subjectData = new ArrayList<>();
        for (X509Certificate c : certificates)
        {
            subjectData.add(c.getSubjectX500Principal().getName() + "+" + c.getNotBefore().toString() + "+" + c.getNotAfter().toString() + "+");
        }

        List<String> nameAndUid = new ArrayList<>();
        for (String s : subjectData)
        {
            String split = s.split(",")[5].split("=")[1] + "(" + s.split(",")[0] + ")";
            nameAndUid.add(split.split("\\+")[0] + split.split("\\+")[3] + "+" + s.split("\\+")[1] + "+" + s.split("\\+")[2] + "+" + s.split(",")[1].split("=")[1]);
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
}

