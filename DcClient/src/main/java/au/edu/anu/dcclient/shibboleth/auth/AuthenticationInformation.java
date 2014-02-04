package au.edu.anu.dcclient.shibboleth.auth;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.opensaml.saml2.ecp.RelayState;
import org.opensaml.ws.soap.soap11.Body;

/*
 * AuthenticationInformation
 *
 * Australian National University Data Commons
 * 
 * Holds the Authentication Information
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class AuthenticationInformation {
	private String assertionConsumerServiceURL;
	private Body body;
	private RelayState relayState;
	private List<NewCookie> cookies;
	
	/**
	 * Constructor
	 */
	public AuthenticationInformation() {
		
	}

	/**
	 * Get the Assertion Consumer Service URL
	 * 
	 * @return The assertion consumer service url
	 */
	public String getAssertionConsumerServiceURL() {
		return assertionConsumerServiceURL;
	}

	/**
	 * Set the assertion consumer service url
	 * 
	 * @param assertionConsumerServiceURL The assertion consumer service url
	 */
	public void setAssertionConsumerServiceURL(String assertionConsumerServiceURL) {
		this.assertionConsumerServiceURL = assertionConsumerServiceURL;
	}

	/**
	 * Get the message body
	 * 
	 * @return The body
	 */
	public Body getBody() {
		return body;
	}

	/**
	 * Set the message body
	 * 
	 * @param body The body
	 */
	public void setBody(Body body) {
		this.body = body;
	}

	/**
	 * Get the relay state
	 * 
	 * @return The relay state
	 */
	public RelayState getRelayState() {
		return relayState;
	}

	/**
	 * Set the relay state
	 * 
	 * @param relayState The  relay state
	 */
	public void setRelayState(RelayState relayState) {
		this.relayState = relayState;
	}

	/**
	 * Get the list of cookies
	 * 
	 * @return The list of cookies
	 */
	public List<NewCookie> getCookies() {
		return cookies;
	}

	/**
	 * Set the list of cookies
	 * 
	 * @param cookies The list of cookies
	 */
	public void setCookies(List<NewCookie> cookies) {
		this.cookies = cookies;
	}
	
	/**
	 * Get a list of cookies
	 * 
	 * @return The list of cookies
	 */
	public List<Cookie> getPlainCookies() {
		List<Cookie> plainCookies = new ArrayList<Cookie>();
		
		for (NewCookie cookie : cookies) {
			plainCookies.add(cookie);
		}
		
		return plainCookies;
	}
}
