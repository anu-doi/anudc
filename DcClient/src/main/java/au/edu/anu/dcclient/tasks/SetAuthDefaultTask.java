package au.edu.anu.dcclient.tasks;

import java.net.Authenticator;
import java.util.List;

import javax.ws.rs.core.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.CustomClient;
import au.edu.anu.dcclient.shibboleth.auth.ShibbolethAuthenticator;

public class SetAuthDefaultTask extends AbstractDcBagTask<Void, Void> {
	static final Logger LOGGER = LoggerFactory.getLogger(SetAuthDefaultTask.class);
	
	private final String username;
	private final String password;
	private final String idp;

//	private Authenticator authenticator;
	
	public SetAuthDefaultTask(String username, String password, String idp) {
		this.username = username;
		this.password = password;
		this.idp = idp;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		try {
//			authenticator = new DcAuthenticator(username, password);
//			Authenticator.setDefault(authenticator);
			ShibbolethAuthenticator authenticator = new ShibbolethAuthenticator(username, password, idp);
			List<Cookie> cookies = authenticator.authenticate();
			CustomClient.setAuth(cookies);
		} catch (Exception e) {
			Authenticator.setDefault(null);
			LOGGER.error("Exception Authenticating", e);
			throw e;
		}
		return null;
	}
}
