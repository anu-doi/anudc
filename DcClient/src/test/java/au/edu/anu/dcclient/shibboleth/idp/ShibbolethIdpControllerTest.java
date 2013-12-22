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

		/*TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
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
			//if (hostname.equals("localhost")) {
				return true;
			//}
			//return false;
		}
		});
		} catch (Exception e) {
		    ;
		}*/
		
		ShibbolethIdpController controller = new ShibbolethIdpController();
		List<ShibbolethIdp> idpList = controller.getShibbolethIdpList();
		printList(idpList);
	}
	
	private  void printList(List<ShibbolethIdp> idpList) {
		if (idpList != null) {
			LOGGER.info("Number of Idp's found: {}", idpList.size());
			for (ShibbolethIdp idp : idpList) {
				LOGGER.info("Entity ID: {}", idp.getEntityID());
				for (ShibbolethDisplayName displayName : idp.getDisplayNames()) {
					LOGGER.info("Language: {}, Value: {}", displayName.getLanguage(), displayName.getValue());
				}
			}
		}
		else {
			LOGGER.info("No Shibboleth List Found");
		}
	}
}
