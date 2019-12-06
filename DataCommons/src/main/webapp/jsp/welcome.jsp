<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="ANU Data Commons" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/static/css/datacommons.css' />" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="full">
	<jsp:include page="/jsp/searchbox.jsp" />
</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div>
			<img alt="Contirbute your research data" src='<c:url value="/static/image/contribute.jpg"/>' />
			<div>
				<h2><a class="nounderline" href='<c:url value="/rest/list/template"/>'>Contribute your research data</a></h2>
			</div>
		</div>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div>
			<img alt="Contirbute your research data" src='<c:url value="/static/image/data-search.jpg"/>' />
			<div>
				<h2><a class="nounderline" href='<c:url value="/rest/upload/search"/>'>Data search</a></h2>
			</div>
		</div>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div class="div1 box bg-grey10 colbox">
		<div><h2><a class="nounderline" href="#">Research data management</a></h2></div>
	</div>
</anu:content>

<anu:content layout="two-third">
	<c:if test="${it.resultSet != null and it.resultSet.numFound > 0}">
		<hr/>
		<h2>Recent Submissions</h2>
		<ul>
		<c:forEach items="${it.resultSet.documentList}" var="row">
			<li class="media marginbottom">
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
	<div class="box bdr-solid bdr-uni">
		<a class="twitter-timeline" data-dnt="true" href="https://twitter.com/ANUOpenAccess" height="250px" data-widget-id="706691559580774403">Tweets by @ANUOpenAccess</a>
		<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>  
 	</div>
	<div class="box bdr-solid bdr-uni">
		<h3 class="nopadtop">Related links</h3>
		<ul class="linklist single-multiple-list">
			<li>
				<a class="acton-tabs-link-processed" href="https://researchdata.ands.org.au/">Rsearch Data Australia</a>
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

<jsp:include page="/jsp/footer.jsp" />
