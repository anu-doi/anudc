package au.edu.anu.dcclient.shibboleth.auth;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.opensaml.saml2.ecp.RelayState;
import org.opensaml.ws.soap.soap11.Body;

public class AuthenticationInformation {
	private String assertionConsumerServiceURL;
	private Body body;
	private RelayState relayState;
	private List<NewCookie> cookies;
	
	public AuthenticationInformation() {
		
	}

	public String getAssertionConsumerServiceURL() {
		return assertionConsumerServiceURL;
	}

	public void setAssertionConsumerServiceURL(String assertionConsumerServiceURL) {
		this.assertionConsumerServiceURL = assertionConsumerServiceURL;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public RelayState getRelayState() {
		return relayState;
	}

	public void setRelayState(RelayState relayState) {
		this.relayState = relayState;
	}

	public List<NewCookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<NewCookie> cookies) {
		this.cookies = cookies;
	}
	
	public List<Cookie> getPlainCookies() {
		List<Cookie> plainCookies = new ArrayList<Cookie>();
		
		for (NewCookie cookie : cookies) {
			plainCookies.add(cookie);
		}
		
		return plainCookies;
	}
}
