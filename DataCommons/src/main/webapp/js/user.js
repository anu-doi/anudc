/**
 * user.js
 * 
 * Australian National University Data Commons
 * 
 * Javascript for the user and user permissions pages
 * 
 * Version	Date		Developer			Description
 * 0.1		20/08/2012	Genevieve Turner	Initial
 */

jQuery(document).ready(function () {
	jQuery("#message").hide();
	jQuery("#updateGroups").hide();
	jQuery("#permissions").hide();
	jQuery("#permissions2").hide();
});

/**
 * Groups click event.  It selects and processes the information for either the selected user or the logged
 * in user
 * 
 * Version	Date		Developer			Description
 * 0.1		20/08/2012	Genevieve Turner	Initial
 */
jQuery("#groups").live('click', function() {
	jQuery("#message").hide();
	jQuery(".chk_perm").attr('checked', false);
	var value = jQuery("#groups").val();
	var url =  "/DataCommons/rest/user/permissions/" + value;
	if (jQuery("#peopleList").text()) {
		var username = jQuery("input[name='username']:checked").val();
		if (!username) {
			return;
		}
		url = url + "?username=" + username;
	}
	
	jQuery.ajax({
		url: url,
		dataType: "json",
		success: function(data) {
			jQuery.map(data, function(item, i) {
				jQuery(".chk_perm[value=" + item + "]").attr('checked',true);
			});
		}
	});
});

/**
 * Searches for people and displays those returned
 * 
 * Version	Date		Developer			Description
 * 0.1		20/08/2012	Genevieve Turner	Initial
 */
jQuery("#findPeople").live('click', function() {
	jQuery("#message").hide();
	jQuery("#updateGroups").hide();
	jQuery("#permissions").hide();
	jQuery("#permissions2").hide();
	jQuery.ajax({
		url: "/DataCommons/rest/user/find",
		dataType: "json",
		data: {
			registered: jQuery("input[name='registered']:checked").val(),
			firstname: jQuery("#firstname").val(),
			lastname: jQuery("#lastname").val(),
			uniId: jQuery("#uniId").val(),
			email: jQuery("#email").val()
		},
		success: function(data) {
			jQuery("#peopleList").text('');
			var table = jQuery("<table width='100%'></table>");
			var headerrow = jQuery("<tr></tr>");
			// Add a header
			headerrow.append(jQuery("<th></th>"));
			headerrow.append(jQuery("<th></th>").text('Uni ID'));
			headerrow.append(jQuery("<th></th>").text('Name'));
			headerrow.append(jQuery("<th></th>").text('Email'));
			table.append(headerrow);
			// Process the returned data
			jQuery.map(data, function(item, i) {
				var row = jQuery("<tr></tr>");
				var radiobutton = jQuery("<input name='username' type='radio' />").attr("value",item.username);
				row.append(jQuery("<td></td>").html(radiobutton));
				row.append(jQuery("<td></td>").text(item.uniId));
				row.append(jQuery("<td></td>").text(item.displayName));
				row.append(jQuery("<td></td>").text(item.email));
				table.append(row);
			});
			jQuery("#peopleList").append(table);
		}
	});
});

/**
 * 
 * @returns
 */
jQuery("input[name='registered']").live('click', function() {
	var id = jQuery(this).attr('id');
	console.log(id);
	if (id == "registered-true") {
		jQuery("#uniId").val('');
		jQuery("#uniId").attr('readonly', true);
	}
	else {
		jQuery("#uniId").removeAttr('readonly');
	}
});

/**
 * Performs actions when a user has been selected
 * 
 * Version	Date		Developer			Description
 * 0.1		20/08/2012	Genevieve Turner	Initial
 */
jQuery("input[name='username']").live('click', function() {
	jQuery(".chk_perm").attr('checked',false);
	jQuery("#updateGroups").show();
	jQuery("#permissions").show();
	jQuery("#permissions2").show();
	jQuery("#message").hide();
	var selGroup = jQuery("#groups").val();
	if (selGroup) {
		jQuery("#groups").click();
	}
	jQuery(".chk_template").attr('checked', false);
	jQuery(".chk_location").attr('checked', false);
	var username = jQuery("input[name='username']:checked").val();
	jQuery.ajax({
		url: "/DataCommons/rest/user/permissions/template",
		data: {
			username: username
		},
		dataType: "json",
		success: function(data) {
			jQuery.map(data, function(item, i) {
				jQuery(".chk_template[value=" + item + "]").attr('checked', true);
			});
		}
	});
	jQuery.ajax({
		url: "/DataCommons/rest/user/permissions/publish-location",
		data: {
			username: username
		},
		dataType: "json",
		success: function(data) {
			jQuery.map(data, function(item, i) {
				jQuery(".chk_location[value=" + item + "]").attr('checked', true);
			});
		}
	});
});

/**
 * Updates the permission list when the save button has been pressed.
 * 
 * Version	Date		Developer			Description
 * 0.1		20/08/2012	Genevieve Turner	Initial
 */
jQuery("#updatePerm").live('click', function(){
	var groupId = jQuery("#groups").val();
	if (jQuery.isEmptyObject(groupId)) {
		console.log('Object empty');
		groupId = '';
	}
	var username = jQuery("input[name='username']:checked").val();
	var permissions = new Array();
	jQuery("input[name='group_perm']:checked").each(function(i) {
		permissions.push(jQuery(this).val());
	});
	var publishPermissions = new Array();
	jQuery("input[name='publish_location']:checked").each(function(i) {
		publishPermissions.push(jQuery(this).val());
	});
	var templatePermissions = new Array();
	jQuery("input[name='template']:checked").each(function(i) {
		templatePermissions.push(jQuery(this).val());
	});
	jQuery("#message").hide();
	jQuery.ajax({
		url: "/DataCommons/rest/user/permissions/",
		type: 'POST',
		dataType: "json",
		data: {
			username: username,
			group_id: groupId,
			group_perm: permissions,
			publish_location: publishPermissions,
			template: templatePermissions,
		},
		success: function(data,textStatus,jqXHR) {
			jQuery("#message").removeClass("msg-error");
			jQuery("#message").addClass("msg-success");
			jQuery("#message").text("User permissions updated.");
			jQuery("#message").show();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			jQuery("#message").removeClass("msg-success");
			jQuery("#message").addClass("msg-error");
			jQuery("#message").text("Error updating user permissions");
			jQuery("#message").show();
		}
	});
});