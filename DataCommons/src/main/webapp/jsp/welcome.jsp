<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Welcome" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="ANU Data Commons">
	<p>Welcome to the ANU Data Commons.</p>
	<p>This project will allow people to add information about their datasets, catalogues etc.</p>
	<form class="anuform" name="frmCreateNewObject" action="">
		<!-- Using tables for layout as other means not available for a 4 column setup. -->
		<anu:table>
			<anu:tr>
				<anu:th>Activities</anu:th>
				<anu:th>Collections</anu:th>
				<anu:th>Party</anu:th>
				<anu:th>Service</anu:th>
			</anu:tr>
			<anu:tr>
				<anu:td>
					<select size="8">
						<option>Project</option>
						<option>Course</option>
						<option>Award</option>
						<option>Event</option>
						<option>Program</option>
					</select>
				</anu:td>
				<anu:td>
					<select size="8">
						<option>Catalogue</option>
						<option>Collection</option>
						<option>Dataset</option>
						<option>Registry</option>
						<option>Repository</option>
					</select>
				</anu:td>
				<anu:td>
					<select size="8">
						<option>Admin Position</option>
						<option>Group</option>
						<option>Person</option>
					</select>
				</anu:td>
				<anu:td>
					<select size="8">
						<option>Offline</option>
						<option>Workflow</option>
						<option>Web</option>
						<option>Software</option>
					</select>
				</anu:td>
			</anu:tr>
			<anu:tr>
				<anu:td>
					<input type="submit" name="newObjType" value="New" />
				</anu:td>
				<anu:td>
					<input type="submit" name="newObjType" value="New" />
				</anu:td>
				<anu:td>
					<input type="submit" name="newObjType" value="New" />
				</anu:td>
				<anu:td>
					<input type="submit" name="newObjType" value="New" />
				</anu:td>
			</anu:tr>
		</anu:table>
	</form>
</anu:content>

<anu:content layout="narrow">
	<anu:boxheader text="Updates" />
	<anu:box style="solid">Updates, changelog etc.</anu:box>
</anu:content>
<jsp:include page="/jsp/footer.jsp" />
