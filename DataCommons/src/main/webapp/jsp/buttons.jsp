<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<anu:content layout="narrow">
<p>Testing side bar</p>
	<sec:authorize access="isAnonymous()">
		Please login to request access to this dataset
	</sec:authorize>
	<sec:authorize access="isAuthenticated()">
		<p><input type="button" id="editButton" name="editButton" value="Edit" onclick="window.location='display/edit?item=${param.item}&amp;tmplt=${param.tmplt}&amp;layout=${param.layout}'" /></p>
		<p><input type="button" id="itemLinkButton" name="itemLinkButton" value="Link to Item" /></p>
	</sec:authorize>
</anu:content>