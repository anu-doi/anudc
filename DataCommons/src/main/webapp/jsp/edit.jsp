<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:content layout="narrow">
	<anu:box style="solid">
		<c:if test="${it.fedoraObject.published}">
			<b>Status:</b> Published<br />
		</c:if>
		<c:if test="${not it.fedoraObject.published}">
			<b>Status:</b> Unpublished<br />
		</c:if>
		<c:if test="${not empty it.fedoraObject.object_id}">
			<b>Identifier:</b> ${it.fedoraObject.object_id}<br />
		</c:if>
	</anu:box>
	<anu:box style="solid">
		<c:if test="${it.fedoraObject.published}">
			<b>Status:</b> Published<br />
		</c:if>
		<c:if test="${not it.fedoraObject.published}">
			<b>Status:</b> Unpublished<br />
		</c:if>
		<c:if test="${not empty it.fedoraObject.object_id}">
			<b>Identifier:</b> ${it.fedoraObject.object_id}<br />
		</c:if>
	</anu:box>
	<anu:box style="solid">
	<p>Edit Fields:</p>
	<p>
		<select id="editSelect">
			<option value="">- No Value Selected -</option>
			<c:forEach var="anItem" items="${it.template.items}">
				<option value="${anItem.name}">${anItem.label}</option>
			</c:forEach>
		</select>
	</p>
	
	<form id="form" method="post" action="" onsubmit="return jQuery('#form').validate().form()" action="">
		<div id="extraFields">
			
		</div>
		<input id="editSubmit" type="submit" class="editSubmit" value="Submit" />
		<jsp:include page="add_reference.jsp" />
		<input id="deleteItem" type="button" class="deleteItem" value="Delete" />
	</form>
	</anu:box>
	<jsp:include page="listrelated.jsp" />
</anu:content>