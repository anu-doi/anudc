package au.edu.anu.dcclient.tasks;

import java.net.Authenticator;

import au.edu.anu.dcclient.DcAuthenticator;

public class SetAuthDefaultTask extends AbstractDcBagTask<Void, Void> {
	private final String username;
	private final String password;

	private Authenticator authenticator;
	
	public SetAuthDefaultTask(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		try {
			authenticator = new DcAuthenticator(username, password);
			Authenticator.setDefault(authenticator);
		} catch (Exception e) {
			Authenticator.setDefault(null);
			throw e;
		}
		return null;
	}
}
