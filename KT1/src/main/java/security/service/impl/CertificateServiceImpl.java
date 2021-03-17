package security.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

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
}

