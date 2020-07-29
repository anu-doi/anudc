package au.edu.anu.datacommons.security.service;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.ldap.LdapPerson;
import au.edu.anu.datacommons.ldap.LdapRequest;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.user.Person;
import au.edu.anu.datacommons.user.RegisteredPerson;
import au.edu.anu.datacommons.util.Util;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
	static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public List<Person> findPeople(Boolean registered, String firstname, String lastname, String uniId, String email) {
		LOGGER.debug("findPeople: {}, {}, {}, {}", firstname, lastname, uniId, email);
		if (registered == null) {
			throw new ValidateException("No indication whether the user is a registered vs ANU user");
		}
		
		if (!registered) {
			return findLdapPeople(firstname, lastname, uniId, email);
		}
		else {
			return findRegisteredUsers(firstname, lastname, email);
		}
	}
	
	private List<Person> findLdapPeople(String firstname, String lastname, String uniId, String email) {
		LOGGER.debug("findLdapPeople: {}, {}, {}", firstname, lastname, uniId);
		LdapRequest ldapRequest = new LdapRequest();
		boolean hasInfo = false;
		StringBuilder sb = new StringBuilder();
		sb.append("(&");
		if (Util.isNotEmpty(firstname)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_GIVENNAME), firstname));
			hasInfo = true;
		}
		if (Util.isNotEmpty(lastname)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_FAMILYNAME), lastname));
			hasInfo = true;
		}
		if (Util.isNotEmpty(uniId)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID), uniId));
			hasInfo = true;
		}
		if (Util.isNotEmpty(email)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_EMAIL), email));
			hasInfo = true;
		}
		sb.append(")");
		if (!hasInfo) {
			throw new ValidateException("A given name, surname, university id or email address is required for the search");
		}
		ldapRequest.setQuery(sb.toString());
		List<LdapPerson> people = null;
		try {
			people = ldapRequest.search();
			LOGGER.debug("Query [fn={}, ln={}, uid={}]. Results: {}", firstname, lastname, uniId, people.size());
			
			return new ArrayList<Person>(people);
		} catch (NamingException e) {
			LOGGER.error("Error querying ldap", e);
		}
		return null;
	}
	
	private String addLdapVariable(String variable, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(variable);
		sb.append("=");
		sb.append(value);
		sb.append(")");
		return sb.toString();
	}
	
	public List<Person> findRegisteredUsers(String firstname, String lastname, String email) {
		LOGGER.debug("findRegisteredUsers: {}, {}, {}", firstname, lastname, email);
		
		if (!Util.isNotEmpty(firstname) && !Util.isNotEmpty(lastname) && !Util.isNotEmpty(email)) {
			throw new ValidateException("A given name, surname, or email address is required for the search");
		}
		
		UsersDAO usersDAO = new UsersDAOImpl();
		List<Users> users = usersDAO.findRegisteredUsers(firstname, lastname, email);
		List<Person> people = new ArrayList<Person>();
		for (Users user : users) {
			LOGGER.debug("User: {}, {}, {}", user.getEmail(), user.getDisplayName(), user.getFamilyName());
			people.add(new RegisteredPerson(user));
		}
		
		return people;
	}
}
