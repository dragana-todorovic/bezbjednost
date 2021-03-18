package security.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import security.pki.keystores.KeyStoreReader;
import security.service.CertificateService;

import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {
	
	KeyStoreReader keyStoreReader = new KeyStoreReader();
	
	public List<String> getAllCertificates() {
        List<X509Certificate> certificates = keyStoreReader.getX509Certificates("./src/main/resources/keystores/root.jks", "12345");
        List<X509Certificate> certificates1 = keyStoreReader.getX509Certificates("./src/main/resources/keystores/intermediate.jks", "12345");
        List<X509Certificate> certificates2 = keyStoreReader.getX509Certificates("./src/main/resources/keystores/endEntity.jks", "12345");

        certificates.addAll(certificates1);
        certificates.addAll(certificates2);

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

	        List<X509Certificate> certificates = keyStoreReader.getX509Certificates("./src/main/resources/keystores/root.jks", "12345");
	        List<X509Certificate> certificates1 = keyStoreReader.getX509Certificates("./src/main/resources/keystores/intermediate.jks", "12345");
	        List<X509Certificate> certificates2 = keyStoreReader.getX509Certificates("./src/main/resources/keystores/endEntity.jks", "12345");

	        certificates.addAll(certificates1);
	        certificates.addAll(certificates2);

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
                         return true;
                     }
                 }
	}
         return false;
}}

