<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/linkItem.css' />" />
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/jquery-ui-1.8.20.custom.css' />" />
		<script type="text/javascript" src="<c:url value='/js/linkItem.js' />"></script>
		<script type="text/javascript" src="<c:url value='/js/popup.js' />"></script>
		<script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.20.custom.min.js' />"></script>
	</head>
	<body>
		<p><input type="button" id="itemLinkButton" name="itemLinkButton" value="Link to Item" /></p>
		<div id="popupLink">
			<a id="popupLinkClose">x</a>
			<h1>Add Reference</h1>
			
			<form id="formAddLink" method="post" onsubmit="return false;"  action="">
				<p>
					<label for="itemType">Item Type</label>
					<select id="itemType" name="itemType">
						<option value="Activity">Activity</option>
						<option value="Collection">Collection</option>
						<option value="Party">Party</option>
						<option value="Service">Service</option>
					</select>
				</p>
				<p><label for="itemId">Item Id</label><input type="text" id="itemId" name="itemId" /></p>
				<p><label for="linkType">Link Type</label>
					<select id="linkType" name="linkType">
						<option value="isPartOf">Is Part Of</option>
						<option value="hasPart">hasPart</option>
						<option value="hasOutput">hasOutput</option>
						<option value="isManagedBy">isManagedBy</option>
						<option value="isOwnedBy">isOwnedBy</option>
						<option value="hasParticipant">hasParticipant</option>
						<option value="hasAssociationWith">hasAssociationWith</option>
						<option value="describes">describes</option>
						<option value="isDescribedBy">isDescribedBy</option>
						<option value="isLocatedIn">isLocatedIn</option>
						<option value="isLocationFor">isLocationFor</option>
						<option value="isDerivedFrom">isDerivedFrom</option>
						<option value="hasDerivedCollection">hasDerivedCollection</option>
						<option value="hasCollector">hasCollector</option>
						<option value="isEnrichedBy">isEnrichedBy</option>
						<option value="isOutputOf">isOutputOf</option>
						<option value="supports">supports</option>
						<option value="isAvailableThrough">isAvailableThrough</option>
						<option value="isProducedBy">isProducedBy</option>
						<option value="isPresentedBy">isPresentedBy</option>
						<option value="isOperatedOnBy">isOperatedOnBy</option>
						<option value="hasValueAddedBy">hasValueAddedBy</option>
						<option value="hasMember">hasMember</option>
						<option value="isMemberOf">isMemberOf</option>
						<option value="isFundedBy">isFundedBy</option>
						<option value="isFunderOf">isFunderOf</option>
						<option value="enriches">enriches</option>
						<option value="isCollectorOf">isCollectorOf</option>
						<option value="isParticipantIn">isParticipantIn</option>
						<option value="isManagerOf">isManagerOf</option>
						<option value="isOwnerOf">isOwnerOf</option>
						<option value="isSupportedBy">isSupportedBy</option>
						<option value="makesAvailable">makesAvailable</option>
						<option value="produces">produces</option>
						<option value="presents">presents</option>
						<option value="operatesOn">operatesOn</option>
						<option value="addsValueTo">addsValueTo</option>
					</select>
				</p>
				<p><input id="btnAddLink" type="submit" value="Submit" /></p>
			</form>
		</div>
		<div id="backgroundPopup"></div>
	</body>
</html>