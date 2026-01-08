<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="ANU Data Commons" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/static/css/datacommons.css' />" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<anu:content layout="full">
	<jsp:include page="/jsp/searchbox.jsp" />
</anu:content>

<anu:container type="container" extraClass="pb-2">
	<div class="pt-2">
		<div class="row equal g-4">
			<anu:content layout="one-third">
				<div class="bg-tint p-1 mb-2 h-100">
					<div>
						<img class="w-100" alt="Contribute your research data" src='<c:url value="/static/image/contribute.jpg"/>' />
						<div>
							<h2><a class="nounderline" href='<c:url value="/rest/contribute/data"/>'>Contribute your research data</a></h2>
						</div>
					</div>
			</div>
			</anu:content>

			<anu:content layout="one-third">
					<div class="bg-tint p-1 mb-2 h-100">
						<img class="w-100" alt="Data search" src='<c:url value="/static/image/data-search.jpg"/>' />
						<div>
							<h2><a class="nounderline" href='<c:url value="/rest/upload/search"/>'>Data search</a></h2>
						</div>
					</div>
			</anu:content>

			<anu:content layout="one-third">
				<div class="bg-tint p-1 mb-2 h-100">
				<img class="w-100" alt="Research data management" src='<c:url value="/static/image/homepage-research-data-management.jpg"/>' />
					<div>
					<h2>
					<a class="nounderline" href="https://anulib.anu.edu.au/research-learn/research-data-management">Research data management
					<img alt="External link" src='<c:url value="https://style.anu.edu.au/_anu/images/icons/web/link.png"/>' />
					</a>
					</h2>
					</div>
					</div>
			</anu:content>
		</div>
	</div>
</anu:container>
<div class="row">
			<anu:content layout="two-third">
				<c:if test="${it.resultSet != null and it.resultSet.numFound > 0}">
					<hr/>
					<h2>Recent Submissions</h2>
					<ul class="noindent">
					<c:forEach items="${it.resultSet.documentList}" var="row">
						<li class="media mb-1">
							<div class="media-body">
								<c:choose>
								<c:when test="${not empty row['published.name']}">
									<a href="<c:url value="/rest/display/${row['id']}?layout=def:display" />"><c:out value="${row['published.name']}" /></a>&nbsp;&nbsp;<span class="text-grey50">[${row['id']}]</span><br />
								</c:when>
								<c:when test="${not empty row['unpublished.name']}">
									<a href="<c:url value="/rest/display/${row['id']}?layout=def:display" />"><c:out value="${row['unpublished.name']}" /></a>&nbsp;&nbsp;<span class="text-grey50">[${row['id']}]</span><br />
								</c:when>
								</c:choose>
							</div>
						</li>
					</c:forEach>
					</ul>
				</c:if>
			</anu:content>

			<anu:content layout="one-third">
					<div class="box-header-tint bg-tint mb-0">
						<p class="large m-0">Related link</p>
					</div>
					<div class="box-bdr-gold mb-0">
					<ul class="linklist single-multiple-list">
						<li>
							<a class="acton-tabs-link-processed" href="https://researchdata.edu.au/">Research Data Australia</a>
						</li>
						<li>
							<a class="acton-tabs-link-processed" href="https://ardc.edu.au/">Australian Research Data Commons</a>
						</li>
						<li>
							<a class="acton-tabs-link-processed" href="https://openresearch-repository.anu.edu.au/">ANU Open Research</a>
						</li>
					</ul>
					</div>
			</anu:content>
		</div>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />
