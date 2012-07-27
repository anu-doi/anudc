package au.edu.anu.datacommons.data.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SolrUtils
 * 
 * Australian National University Data Commons
 * 
 * Utlities to use with Solr
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		23/07/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SolrUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrUtils.class);
	
	/**
	 * escapeSpecialCharacters
	 *
	 * Escapes some of the special characters used in Solr.  This is used rather than
	 * ClientUtils as ClientUtils escapes characters that we do not want escaped.<br />
	 * Currently escapes: ':'
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		23/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param query The query to escape special characters from
	 * @return Returns the modified string
	 */
	public static String escapeSpecialCharacters(String query) {
		query = query.replace(":", "\\:");
		LOGGER.info("Replaced String: {}", query);
		return query;
	}
}
