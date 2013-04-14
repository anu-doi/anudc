<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="226" title="Catalogue - PAMBU - ANU" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<c:set var="pambusite" value="http://asiapacific.anu.edu.au/pambu" scope="page" />
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="pambuheader.jsp" />
<anu:content layout="doublewide">
	<h1 class="doublewide nopadbottom">Catalogue</h1>
	<form name="longForm" method="post" action="">
		<div class="doublewide nomargintop">
			<h2>Search options</h2>
			<div class="w-narrow right">
				<anu:box style="uni">
					<input type="submit" value="Browse all PMB holdings" name="browseAll" />
					<br /><br />
					(Select <strong>Manuscripts Series</strong> or <strong>Printed Document series</strong> prior to browsing)
				</anu:box>
				<div class='divline-dotted marginbottom'></div>
				<anu:box style="uni">
					<img src='http://styles.anu.edu.au/_anu/images/icons/silk/zoom.png' alt='search icon'/> <strong>
					<a href='http://asiapacific.anu.edu.au/pambu/reels/'>Search reel lists</a></strong>
	
					<br />
					<br />(Detailed list of documents on microfilm)
				</anu:box>
			</div>
			<div class="left" style="width: 260px;">
				<p>
					<strong>SEARCH FIELDS:</strong><br />
					<input type="radio" name="selection" value="author" />Author<br />
					<input type="radio" name="selection" value="title" />Title<br />
					<input type="radio" name="selection" value="serial" />PMB Number<br />
					<input type="radio" name="selection" value="notes" />Notes<br />
					<input type="radio" name="selection" value="all" checked />All<br />
				</p>
				<p>
					<strong>PAMBU HOLDING:</strong><br />
					<input type="radio" name="pmbHolding" value="ms" />Manuscript Series<br />
					<input type="radio" name="pmbHolding" value="doc" />Printed Document Series<br />
					<input type="radio" name="pmbHolding" value="all" checked />Manuscripts &amp; Document Series<br/>
					<input type="radio" name="pmbHolding" value="audio" />Sound Recordings<br/>
					<input type="radio" name="pmbHolding" value="map" />Maps<br/>
					<input type="radio" name="pmbHolding" value="pic" />Photographs<br/>
				</p>
			</div>
			<div class="left" style="width: 140px;">
				<p>
					<strong>Search logic:</strong><br />
					<input type="radio" name="modifier" value="AND" checked />AND<br />
					<input type="radio" name="modifier" value="OR" />OR
				</p>
				<p>
					<strong>Show results by:</strong><br />
					<input type="radio" name="preferredOrder" value="sortVal" checked />PMB Number<br />
					<input type="radio" name="preferredOrder" value="author" />Author<br />
					<input type="radio" name="preferredOrder" value="date" />Date<br />
				</p>
				<p>
					<strong>Output length:</strong><br />
					<input type="radio" name="output" value="long" checked />Long<br />
					<input type="radio" name="output" value="short" />Short<br />
				</p>
			</div>
			<div class="w-doublenarrow clear">
				<p>
					<strong>Search text:</strong>
					<em> (Case is not important)</em>
					<input type="text" name="entry" size="40" maxlength="100"><br />
					<input type="submit" value="Submit this search" name="submit" />
					<input type="submit" value="Clear" name="clear">
				</p>
			</div>
		</div>
	</form>
</anu:content>

<jsp:include page="pambufooter.jsp" />