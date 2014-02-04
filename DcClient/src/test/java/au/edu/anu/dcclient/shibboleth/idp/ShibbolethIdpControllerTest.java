package au.edu.anu.dcclient.shibboleth.idp;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShibbolethIdpControllerTest {
	static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethIdpControllerTest.class);
	
	@Test
	public void test() {
		// Uncomment to ignore the SSL trust i.e. who the person is, and who has one issued by a certificate authority
		/*
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    public X509Certificate[] getAcceptedIssuers(){return null;}
		    public void checkClientTrusted(X509Certificate[] certs, String authType){}
		    public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession sslSession) {
					return true;
			}
			});
		} catch (Exception e) {
		    ;
		}*/
		
		ShibbolethIdpController controller = new ShibbolethIdpController();
		List<IdentityProvider> idpList = controller.getShibbolethIdpList();
		printList(idpList);
	}
	
	private void printList(List<IdentityProvider> idpList) {
		if (idpList != null) {
			LOGGER.info("Number of IdP's found: {}", idpList.size());
			for (IdentityProvider idp : idpList) {
				LOGGER.info("Entity ID: {}, Display Name: {}, ECP Location: {}", idp.getEntityID(), idp.getDisplayName(), idp.getEcpURL());
			}
		}
		else {
			LOGGER.info("No Shibboleth List Found");
		}
	}
}
