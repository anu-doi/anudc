<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:content layout="narrow">
<p>Edit Fields:</p>
	<p>
		<select id="editSelect">
			<option value="">- No Value Selected -</option>
			<c:forEach var="anItem" items="${it.template.items}">
				<option value="${anItem.name}">${anItem.label}</option>
			</c:forEach>
		</select>
	</p>
	
	<c:url value="/rest/display/edit" var="actionURL">
		<c:param name="layout">def:test2</c:param>
		<c:param name="tmplt">${param.tmplt}</c:param>
		<c:param name="item">${param.item}</c:param>
	</c:url>
	<form method="post" action="${actionURL}">
		<div id="extraFields">
			
		</div>
		<input id="editSubmit" type="submit" value="Submit" />
	</form>
</anu:content>