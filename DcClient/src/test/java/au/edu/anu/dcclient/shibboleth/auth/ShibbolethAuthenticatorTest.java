package au.edu.anu.dcclient.shibboleth.auth;

import static org.junit.Assert.fail;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.parse.BasicParserPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShibbolethAuthenticatorTest {
	static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethAuthenticatorTest.class);
	
	@Before
	public void setUp() {
		try {
			Registry.initialise();
			BasicParserPool ppMgr = new BasicParserPool();
			ppMgr.setNamespaceAware(true);
		}
		catch (ConfigurationException e) {
			LOGGER.error("Exception configuring bootstrap", e);
		}
	}
	
	@Test
	public void authenticationTest() {
		// Create a trust manager that does not validate certificate chains
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
				//if (hostname.equals("localhost")) {
					return true;
				//}
				//return false;
			}
			});
		} catch (Exception e) {
		    ;
		}
		ShibbolethAuthenticator authenticator = new ShibbolethAuthenticator("myself", "testpassword","http://23wj72s.uds.anu.edu.au/idp/shibboleth");
		try {
			authenticator.authenticate();
		}
		catch (ShibbolethAuthenticationException e) {
			LOGGER.error("Exception authenticating", e);
			fail("Failed to authenticate");
		}
	}
}
