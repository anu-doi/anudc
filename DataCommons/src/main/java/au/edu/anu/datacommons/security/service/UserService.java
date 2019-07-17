package au.edu.anu.datacommons.security.service;

import java.util.List;

import au.edu.anu.datacommons.user.Person;

public interface UserService {
	public List<Person> findPeople(Boolean registered, String firstname, String lastname, String uniId, String email);
}
