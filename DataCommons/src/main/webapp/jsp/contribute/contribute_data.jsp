<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" subject="" title="Contribute" description="">
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="two-third" title="Contribute your research data">
<div>To contribute your research data collection you must first <a href='<c:url value="/login-select" />' class="text-link">Login</a> using your ANU ID and password or your registered account details.</div>
</anu:content>

<anu:content layout="one-third">
<div class="marginbottom">
<a class="btn-action" href="<c:url value='/rest/display/new?layout=def:new&tmplt=tmplt:1' />">Contribute your data</a>
</div>
<div class="marginbottom">
<a class="btn-action" href="<c:url value='/rest/extmetadata' />">Import your data</a>
</div>
<div class="div1 box bg-grey10 colbox">
		<div>
			<h2>Related Guidance 
			</h2>
			This handy <a href="https://openresearch.anu.edu.au/files/guidance/Submitting-to-Data-Commons-Quick-Guide.pdf">quick guide</a> or a <a href="https://openresearch.anu.edu.au/files/guidance/Submitting_to_Data_Commons_repository.pdf">full guide</a> will assist you in contributing your data to the data repository
		</div>
</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />