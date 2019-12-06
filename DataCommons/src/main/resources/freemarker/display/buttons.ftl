<div class="btn-large btn-group-vertical">
	<#assign canEdit=security.checkPermission(2)>
	<#assign canDelete=security.checkPermission(8)>
	<#assign canReview=security.checkPermission(32)>
	<#assign canPublish=security.checkPermission(64)>
	<#if canEdit>
	<a class="btn btn-primary mb-1" href="/DataCommons/rest/display/edit/${item.object_id}?style=full">Edit</a>
	</#if>
	<#if canDelete>
	<button class="btn btn-primary mb-1" formmethod="post" formaction="/DataCommons/rest/display/delete/${item.object_id}" onclick="return confirmDelete();">Delete</button>
	</#if>
	<#if canPublish>
	<input class="btn btn-primary mb-1" id="mintDoi" type="button" name="mintDoi" value="Mint DOI" onclick="if (confirm('This will mint a Digital Object Identifier for this collection. Are you sure?')) window.location='/DataCommons/rest/publish/mintdoi/${item.object_id}'" />
	</#if>
	<#if canEdit>
	<input class="btn btn-primary mb-1" type="button" id="validateButton" name="validateButton" value="Validation Check" onclick="window.location='/DataCommons/rest/publish/validate/${item.object_id}'" />
	<input class="btn btn-primary mb-1" id="itemLinkButton" type="button" name="itemLinkButton" value="Link to Item" data-toggle="modal" data-target="#modalLink" />
	<input class="btn btn-primary mb-1" id="editLinkButton" type="button" name="editLinkButton" value="Edit Item Links" data-toggle="modal" data-target="#modalEditLink" />
	</#if>
	
	<#if canPublish && item.publishReady??>
	<input class="btn btn-primary mb-1" type="button" id="publishButton" name="publishButton" value="Publish" onclick="window.location='/DataCommons/rest/publish/${item.object_id}'" />
	<#elseif canReview && item.reviewReady??>
	<form id="formPublishReady" method="post" action="/DataCommons/rest/ready/publish/${item.object_id}">
		<input class="btn btn-primary mb-1" type="submit" id="publishReadyButton" name="publishReadyButton" value="Ready for Publish"/>
	</form>
	<#elseif canEdit>
	<form id="formReviewReady" method="post" action="/DataCommons/rest/ready/review/${item.object_id}">
		<input class="btn btn-primary mb-1" type="submit" id="reviewReadyButton" name="reviewReadyButton" value="Ready for Review"/>
	</form>
	</#if>
	
	
	<div id="modalLink" class="modal fade" role="dialog" aria-labelledby="addReferenceModal" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
				<h5 class="modal-title" id="addReferenceModal">Add Reference</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
				<span aria-hidden="true">&times;</span>
				</button>
				</div>
				<div class="modal-body">
					<form id="formAddLink" method="post" onsubmit="return false;" action="">
					<p><input type="hidden" name="previousLinkType" id="previousLinkType" /></p>
					<div class="container">
						<div class="row">
							<div class="col-sm">
								<label for="linkItemType">Item Type</label>
							</div>
							<div class="col-sm">
								<select id="linkItemType" name="itemType">
									<option value="Activity">Activity</option>
									<option value="Collection">Collection</option>
									<option value="Party">Party</option>
									<option value="Service">Service</option>
								</select>
							</div>
						</div>
						<div class="row">
							<div class="col-sm">
								<label for="linkType">Link Type</label>
							</div>
							<div class="col-sm">
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
							</div>
						</div>
						<div class="row">
							<div class="col-12">
							Please either search for an item in ANU Data Commons or provide an External Identifier such as a NLA Identifier or a PURL
							</div>
						</div>
						<div class="row">
							<div class="col-sm">
								<label for="itemSearch" class="awesomplete" title="Search for an item to link to from ANU Data Commons">Item Search</label>
							</div>
							<div class="col-sm">
								<input type="text" id="itemSearch" name="itemSearch" />
							</div>
						</div>
						<div class="row">
							<div class="col">
								<label for="itemIdentifier">Item Id</label>
							</div>
							<div id="itemIdentifier" class="col">
								None Selected
							</div>
						</div>
						<div class="row">
							<div class="col">
							<label for="itemName">Item Name</label>
							</div>
							<div id="itemName" class="col">
								None Selected
							</div>
						</div>
						<div class="row">
							<div class="col hidden">
								<label class="hidden" for="itemId">Item Full Id</label>
							</div>
							<div id="itemId" class="col hidden">
								None Selected
							</div>
						</div>
						<div class="row">
							<div class="col">
								<label for="linkExternal">External Identifier</label>
							</div>
							<div class="col">
								<input type="text" id="linkExternal" name="linkExternal" />
							</div>
						</div>
						<div class="row">
							<div class="col">
								<input id="btnAddLink" type="submit" value="Submit" />
							</div>
							<!-- <div class="col">
								<input type="text" id="linkExternal" name="linkExternal" />
							</div> -->
						</div>
					</div>
					<p></p>
					</form>
				</div>
			</div>
		</div>
	</div>
	<div id="modalEditLink" class="modal fade" role="dialog" aria-labelledby="editLinkModal" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
				<h5 class="modal-title" id="editLinkModal">Edit Links</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
				<span aria-hidden="true">&times;</span>
				</button>
				</div>
				<div class="modal-body">
					<div id="editLinkContent">
						edit blah 2
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="/DataCommons/static/js/jquery.easy-autocomplete.min.js"></script>
	<script type="text/javascript" src="/DataCommons/static/js/link-item.js"></script>
</div>
	<#--
	Get Report
Delete
Edit Metadata
Edit Whole Metadata
Set Request Questions
Mint DOI
Validation Check
Publish
Reject
Link to Item
Edit Item Links
	-->