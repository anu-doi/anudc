<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" subject="" title="Contribute" description="">
	<link rel="stylesheet" type="text/css" href="<c:url value='/static/css/datacommons.css' />" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<div class="row">
<div class="col">
<anu:container type="container" extraClass="pb-2">

<anu:content layout="full" title="Contribute">
The ANU Data Commons collects, maintains and disseminates research data from the ANU Community.

The University has an open access policy that outline who can contribute material to the Data Commons and what kinds of data will be accepted.

</anu:content>

<div class="pt-2">
<div class="row equal g-4">
<anu:content layout="one-third">
	<div class="bg-tint p-1 mb-2 h-100">
		<div>
			<img class="w-100" alt="Contribute your data" src='<c:url value="/static/image/contribute-your-data.jpg"/>' />
			<div>
				<h2><a class="nounderline" href='<c:url value="/rest/contribute/data"/>'>Contribute your data </a></h2>
			</div>
		</div>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div class="bg-tint p-1 mb-2 h-100">
		<div>
			<img class="w-100" alt="Contribute your research" src='<c:url value="/static/image/contribute-your-research.jpg"/>' />
			<div>
				<h2><a class="nounderline" href="https://openresearch.anu.edu.au/node/34">Contribute your research <img alt="external link" src='<c:url value="https://style.anu.edu.au/_anu/images/icons/web/link.png"/>' /></a></h2>
			</div>
		</div>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div class="bg-tint p-1 mb-2 h-100">
		<div>
			<img class="w-100" alt="Contribute your thesis" src='<c:url value="/static/image/contribute-your-thesis.jpg"/>' />
			<div>
				<h2><a class="nounderline" href="https://openresearch.anu.edu.au/node/33">Contribute your thesis <img alt="external link" src='<c:url value="https://style.anu.edu.au/_anu/images/icons/web/link.png"/>' /></a></h2>
			</div>
		</div>
	</div>
</anu:content>
</div>
</div>
</anu:container>
</div>
</div>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />