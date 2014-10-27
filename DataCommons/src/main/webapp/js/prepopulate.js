// Call prepopulate function when document's ready.
jQuery(document).ready(prepopulate);

/**
 * Prepopulates some of the fields on a new record page by reading their provided values from the query parameter
 * string.
 */
function prepopulate() {
	var queryParams = {
			name: null,
			abbrName: null,
			altName: null,
			websiteAddress: null,

			significanceStatement: null,
			briefDesc: null,
			fullDesc: null,
			createdDate: null,
			citationYear: null,
			citCreatorGiven:null,
			citCreatorSurname: null,
			citationPublisher: null,
			
			email: null,
			postalAddress: null,
			principalInvestigator: null,
			supervisor: null,
			collaborator: null,
			
			websiteAddress: null,
		
	}

	// Read the value for each of the recognised parameters from the query string.
	for (iParam in queryParams) {
		queryParams[iParam] = getQueryParam(iParam);
	}

	// Fill the form field for each of the recognised query parameter with provided values.
	for (iParam in queryParams) {
		if (queryParams[iParam] != null) {
			if (jQuery("input[name='" + iParam + "']").length > 0) {
				jQuery("input[name='" + iParam + "']").val(queryParams[iParam]);
			} else if (jQuery("textarea[name='" + iParam + "']").length > 0) {
				jQuery("textarea[name='" + iParam + "']").val(queryParams[iParam]);
			}
		}
	}
}

/**
 * Returns the value of a provided query parameter. Src: http://css-tricks.com/snippets/javascript/get-url-variables/
 * 
 * @param paramName
 *            Query parameter as String
 * @returns Value of Query parameter as String
 */
function getQueryParam(paramName) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == paramName) {
			return decodeURIComponent(pair[1].replace(/\+/g, " "));
		}
	}
	return null;
}