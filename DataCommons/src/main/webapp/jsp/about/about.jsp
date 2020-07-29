<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Page" description="Administration links page" subject="administration" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
</anu:header>

<jsp:include page="../header.jsp" />

<fmt:bundle basename='global'>
	<fmt:message var="rejectedTitle" key="review.rejected.title" />
</fmt:bundle>

<fmt:setBundle basename='global'/>

<anu:content layout="full" title="About Data Commons at ANU">
<p>The Australian National University, through its <a  href="<c:url value='/' />">Data Commons</a> repository collects, maintains, preserves, promotes and disseminates research data from the ANU community. </p>
<p>Data Commons holds a variety of ANU Research Data about research projects, research datasets, services and researchers. The wider community is free to browse this material and all members of the ANU community are encouraged to deposit their data.</p>
<p>Embargo arrangements are available to control data accessibility, if required. </p>

	<h2>Benefits of open access data publishing</h2>
	<p>
	Research data repositories provide the best option for storing and publishing research data in the long term. Specific repositories may be recommended by funders or publishers. There are also discipline-specific repositories that may be available.
	</p>
	<p>
	If no discipline-specific repository is available, staff and postgraduate students at the Australian National University can deposit their research data in Data Commons, the University’s data repository. 
	</p>
	
	
	<h3>For authors</h3>
	<ul>
		<li>Helps researchers satisfy the requirements of funding bodies and publishers, such as the <a href = "http://www.arc.gov.au/">Australian Research Council (ARC)</a> and the <a href="https://www.nhmrc.gov.au/">National Health and Medical Research Council (NHMRC)</a>.</li>
		<li>Enables researchers to manage and promote their research data.</li>
		<li>Provides a persistent web address for each article that can be used for ongoing citation purposes. </li>
	</ul>
	
	<h3>For the University</h3>
	<ul>
		<li>Helps meet the objectives of The Australian National University's <a href="https://policies.anu.edu.au/ppl/document/ANUP_008802">Open Access Policy</a> and <a href="https://policies.anu.edu.au/ppl/document/ANUP_008803">Procedure</a> which state: "The Australian National University is a research-intensive, research-led university. Our commitment to the dissemination of research findings is essential both to differentiate the University's research excellence and support national and international research excellence."</li>
		<li>Creates a permanent record of university research data.</li>
		<li>Promotes visibility of the University's research data. Publications deposited in Data Commons are visible and accessible through other services such as <a href="https://researchdata.edu.au">Research Data Australia</a>.</li>
	</ul>
</anu:content>