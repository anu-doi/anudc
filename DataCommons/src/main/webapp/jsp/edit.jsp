<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:content layout="narrow">
	<jsp:include page="status.jsp" />
	<anu:box style="solid">
	<p>Edit Fields:</p>
	<p>
		<select id="editSelect">
			<option value="">- No Value Selected -</option>
			<c:forEach var="anItem" items="${it.template.items}">
				<c:if test="${empty anItem.disabled and empty anItem.readOnly}">
					<option value="${anItem.name}">${anItem.label}</option>
				</c:if>
			</c:forEach>
		</select>
	</p>
	
	<form id="form" method="post" action="">
		<div id="extraFields">
			
		</div>
		<input id="editSubmit" type="submit" class="editSubmit" value="Submit" />
		<input id="deleteItem" type="button" class="deleteItem" value="Delete" />
	</form>
	<form>
	<c:url value="/rest/display/${it.fedoraObject.object_id}" var="cancelLink">
		<c:param name="tmplt" value="${param.tmplt}" />
		<c:param name="layout" value="${param.layout}" />
	</c:url>
	<p>
		<input type="button" id="cancelEdit" name="cancelEdit" value="Cancel Edit" onclick="window.location='${cancelLink}'" />
	</p>
	</form>
		<jsp:include page="add_reference.jsp" />
	</anu:box>
	<jsp:include page="listrelated.jsp" />
</anu:content>