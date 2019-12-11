<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" subject="" title="Contribute" description="">
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="full" title="Contribute">
The Open Research repository is the University
</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div>
			<!-- <img alt="Contribute your research data" src='<c:url value="/static/image/contribute.jpg"/>' /> -->
			<div>
				<h2><a class="nounderline" href="https://openresearch.anu.edu.au/node/34">Contribute your research</a></h2>
			</div>
		</div>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div>
			<!-- <img alt="Contribute your research data" src='<c:url value="/static/image/contribute.jpg"/>' /> -->
			<div>
				<h2><a class="nounderline" href='<c:url value="/rest/contribute/data"/>'>Contribute your data</a></h2>
			</div>
		</div>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div>
			<!-- <img alt="Contribute your research data" src='<c:url value="/static/image/contribute.jpg"/>' /> -->
			<div>
				<h2><a class="nounderline" href="https://openresearch.anu.edu.au/node/33">Contribute your thesis</a></h2>
			</div>
		</div>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />