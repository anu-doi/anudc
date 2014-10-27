/**
 * 
 */
package au.edu.anu.datacommons.external.github;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rahul Khanna
 *
 */
@XmlRootElement
public class GitHubRepoDetailsResponse {
	/**
	 * Repository ID in GitHub's system
	 */
	private String id;

	/**
	 * Name of repository
	 */
	private String name;

	/**
	 * Repository description
	 */
	private String description;

	/**
	 * Repository owner
	 */
	private Owner owner;
	
	/**
	 * Repository URL
	 */
	private String htmlUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}
	
	@XmlElement(name = "html_url")
	public String getHtmlUrl() {
		return htmlUrl;
	}

	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}



	/**
	 * Information about repository owner.
	 * 
	 * @author Rahul Khanna
	 *
	 */
	public static class Owner {
		/**
		 * Username of owner
		 */
		private String username;

		@XmlElement(name = "login")
		public String getUsername() {
			return username;
		}

		public void setUsername(String login) {
			this.username = login;
		}
	}
}
