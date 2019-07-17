package au.edu.anu.datacommons.user;

import au.edu.anu.datacommons.data.db.model.Users;

public class RegisteredPerson implements Person {
	private Users user;
	
	public RegisteredPerson(Users user) {
		this.user = user;
	}
	
	@Override
	public String getEmail() {
		return user.getEmail();
	}

	@Override
	public String getGivenName() {
		return user.getGivenName();
	}

	@Override
	public String getFamilyName() {
		return user.getFamilyName();
	}

	@Override
	public String getDisplayName() {
		return user.getDisplayName();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

}
