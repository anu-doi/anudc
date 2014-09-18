/**
 * 
 */
package au.edu.anu.datacommons.external.github;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.external.ExternalMetadataException;
import au.edu.anu.datacommons.external.github.GitHubRepoDetailsResponse;
import au.edu.anu.datacommons.external.github.MetadataProviderGitHub;
import au.edu.anu.datacommons.external.github.GitHubRepoDetailsResponse.Owner;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author Rahul Khanna
 *
 */
public class MetadataSourceGitHubTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataSourceGitHubTest.class);
	
	@Mock
	private Client client;
	
	@InjectMocks
	private MetadataProviderGitHub metadataSource;
	
	MultivaluedMap<String, String> params;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		params = new MultivaluedMapImpl();
		
		WebResource wr = mock(WebResource.class);
		
		// Create a GitHub Response object.
		GitHubRepoDetailsResponse githubResp = new GitHubRepoDetailsResponse();
		githubResp.setHtmlUrl("https://github.com/anu-doi/anudc");
		githubResp.setDescription("ANU DataCommons");
		githubResp.setId("5974792");
		githubResp.setName("anudc");
		Owner owner = new Owner();
		owner.setUsername("anu-doi");
		githubResp.setOwner(owner);
		
		ClientResponse httpResp = mock(ClientResponse.class);
		when(httpResp.getClientResponseStatus()).thenReturn(Status.OK);
		when(wr.get(ClientResponse.class)).thenReturn(httpResp);
		when(wr.get(GitHubRepoDetailsResponse.class)).thenReturn(githubResp);
		doReturn(wr).when(client).resource(anyString());
		when(client.resource(anyString())).thenReturn(wr);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = ExternalMetadataException.class)
	public void testRetrieveMetadataInvalidUrl() throws Exception {
		params.add("repoUrl", "invalidUrl");
		metadataSource.retrieveMetadata(params);
	}

	@Test
	public void testRetrieveMetadata() throws Exception {
		params.add("repoUrl", "https://github.com/anu-doi/anudc");
		FedoraItem fedoraItem = metadataSource.retrieveMetadata(params);
		assertThat(fedoraItem.getType(), is("Collection"));
		Map<String, List<String>> dataMap = fedoraItem.generateDataMap();
		assertThat(dataMap.containsKey("name"), is(true));
		assertThat(dataMap.containsKey("briefDesc"), is(true));
		assertThat(dataMap.containsKey("citCreatorGiven"), is(true));
		assertThat(dataMap.containsKey("websiteAddress"), is(true));
	}
}
