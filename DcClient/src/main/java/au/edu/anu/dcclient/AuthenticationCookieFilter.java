package au.edu.anu.dcclient;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Cookie;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

public class AuthenticationCookieFilter extends ClientFilter {
	private List<Object> cookies = new ArrayList<Object>();
	
	public AuthenticationCookieFilter(List<Cookie> cookies) {
		this.cookies.clear();
		for (Cookie cookie : cookies) {
			this.cookies.add(cookie);
		}
	}
	
	@Override
	public ClientResponse handle(ClientRequest request)
			throws ClientHandlerException {
		if (cookies != null) {
			request.getHeaders().put("Cookie", cookies);
		}
		ClientResponse response = getNext().handle(request);
		return response;
	}
}
