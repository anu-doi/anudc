<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" subject="" title="Contribute" description="">
	<link rel="stylesheet" type="text/css" href="<c:url value='/static/css/datacommons.css' />" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="full" title="Contribute">
The ANU Data Commons collects, maintains and disseminates research data from the ANU Community.

The University has an open access policy that outline who can contribute material to the Data Commons and what kinds of data will be accepted.

</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div>
			<img alt="Contribute your data" src='<c:url value="/static/image/contribute-your-data.jpg"/>' />
			<div>
				<h2><a class="nounderline" href='<c:url value="/rest/contribute/data"/>'>Contribute your data 
				</a></h2>
			</div>
				 
		</div>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div>
			<img alt="Contribute your research" src='<c:url value="/static/image/contribute-your-research.jpg"/>' />
			<div>
				<h2><a class="nounderline" href="https://openresearch.anu.edu.au/node/34">Contribute your research <img alt="external link" src='<c:url value="https://style.anu.edu.au/_anu/images/icons/web/link.png"/>' /></a></h2>
			</div>
		</div>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div>
			<img alt="Contribute your thesis" src='<c:url value="/static/image/contribute-your-thesis.jpg"/>' />
			<div>
				<h2><a class="nounderline" href="https://openresearch.anu.edu.au/node/33">Contribute your thesis <img alt="external link" src='<c:url value="https://style.anu.edu.au/_anu/images/icons/web/link.png"/>' /></a></h2>
			</div>
		</div>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />