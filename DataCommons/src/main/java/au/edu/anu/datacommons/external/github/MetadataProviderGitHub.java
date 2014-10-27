/**
 * 
 */
package au.edu.anu.datacommons.external.github;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

import au.edu.anu.datacommons.external.ExternalMetadataException;
import au.edu.anu.datacommons.external.ExternalMetadataProvider;
import au.edu.anu.datacommons.external.ParamInfo;
import au.edu.anu.datacommons.webservice.bindings.Collection;
import au.edu.anu.datacommons.webservice.bindings.Creator;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;
import au.edu.anu.datacommons.webservice.bindings.RelatedWebsites;

/**
 * Retrieves metadata from a GitHub repository and returns a FedoraItem object with that metadata.
 * 
 * @author Rahul Khanna
 *
 */
public class MetadataProviderGitHub implements ExternalMetadataProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataProviderGitHub.class);
	
	private static final String API_URL = "api.github.com";
	private static final ClientResponse.Status[] SUCCESS_STATUSES = {ClientResponse.Status.OK};
	private static final String FRIENDLY_NAME = "GitHub";
	
	private static final List<ParamInfo> requiredParams = new ArrayList<>();
	
	static {
		requiredParams.add(new ParamInfo("repoUrl", "Repository URL"));
	}
	
	@Autowired
	Client client;
	
	public MetadataProviderGitHub() {
		super();
	}

	@Override
	public String getFriendlyName() {
		return FRIENDLY_NAME;
	}

	@Override
	public String getFqClassName() {
		return this.getClass().getName();
	}

	@Override
	public List<ParamInfo> getRequiredParams() {
		return requiredParams;
	}

	@Override
	public FedoraItem retrieveMetadata(MultivaluedMap<String, String> params) throws ExternalMetadataException {
		String repoUrl = getRepoUrl(params);
		LOGGER.trace("Retrieving metadata from external provider at {}...", repoUrl);
		
		WebResource webRes = client.resource(repoUrl);
		ClientResponse clientResp = webRes.get(ClientResponse.class);
		GitHubRepoDetailsResponse resp = null;
		if (isSuccess(clientResp.getClientResponseStatus())) {
			resp = webRes.get(GitHubRepoDetailsResponse.class);
		} else {
			String entity = clientResp.getEntity(String.class);
			throw new ExternalMetadataException(MessageFormat.format("{0} returned unsuccessful HTTP Status: {1}, {2}",
					FRIENDLY_NAME, clientResp.getStatus(), entity));
		}
		return createFedoraItem(resp);
	}

	private String getRepoUrl(MultivaluedMap<String, String> params) throws ExternalMetadataException {
		String repoUrl;
		try {
			repoUrl = convertToApiUrl(params.getFirst("repoUrl"));
		} catch (MalformedURLException e) {
			throw new ExternalMetadataException(e);
		}
		return repoUrl;
	}
	
	private boolean isSuccess(Status clientResponseStatus) {
		for (ClientResponse.Status iStatus : SUCCESS_STATUSES) {
			if (iStatus.equals(clientResponseStatus)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts a GitHub repository URL from the format https://github.com/{user}/{repo} to
	 * https://api.github.com/repos/{user}/{repo} for API use.
	 * 
	 * @param url
	 *            URL of the HTML view of a repository
	 * @return URL of a JSON view of a repository as String
	 * @throws MalformedURLException 
	 */
	private String convertToApiUrl(String url) throws MalformedURLException {
		URL jsonUrl;
		URL htmlUrl = new URL(url);
		
		if (!htmlUrl.getHost().equalsIgnoreCase(API_URL)) {
			jsonUrl = new URL(htmlUrl.getProtocol(), API_URL, htmlUrl.getPort(), "/repos" + htmlUrl.getFile());
		} else {
			jsonUrl = htmlUrl;
		}
		return jsonUrl.toString();
	}
	
	/**
	 * Maps metadata fields in GitHubRepoDetailsResponse to their corresponding fields in a Collection.
	 * 
	 * @param gitHubResp
	 *            GitHubRepoDetailsResponse object to read fields from
	 * @return FedoraItem with metadata mapped from the GitHubRepoDetailsResponse object
	 */
	private FedoraItem createFedoraItem(GitHubRepoDetailsResponse gitHubResp) {
		Collection coll = new Collection();
		// Title
		coll.setTitle(gitHubResp.getName());
		// Brief Description
		coll.setBriefDesc(gitHubResp.getDescription());
		// Creator
		Creator creator = new Creator();
		creator.setCitCreatorGiven(gitHubResp.getOwner().getUsername());
		coll.setCreators(Arrays.asList(creator));
		// Website address
		coll.setWebsiteAddress(gitHubResp.getHtmlUrl());
		return coll;
	}
}
