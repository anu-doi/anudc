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
		// Ensure that characters such as + are url encoded
		url = url + "?username=" + encodeURIComponent(username);
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
	jQuery.ajax({
		url: "/DataCommons/rest/user/find",
		dataType: "json",
		data: {
			firstname: jQuery("#firstname").val(),
			lastname: jQuery("#lastname").val(),
			email: jQuery("#email").val()
		},
		success: function(data) {
			jQuery("#peopleList").text('');
			var table = jQuery("<table width='100%'></table>");
			var headerrow = jQuery("<tr></tr>");
			// Add a header
			headerrow.append(jQuery("<th></th>"));
			headerrow.append(jQuery("<th></th>").text('Name'));
			headerrow.append(jQuery("<th></th>").text('Email'));
			table.append(headerrow);
			// Process the returned data
			jQuery.map(data, function(item, i) {
				var row = jQuery("<tr></tr>");
				var radiobutton = jQuery("<input name='username' type='radio' />").attr("value",item.username);
				row.append(jQuery("<td></td>").html(radiobutton));
				row.append(jQuery("<td></td>").text(item.displayName));
				row.append(jQuery("<td></td>").text(item.email));
				table.append(row);
			});
			jQuery("#peopleList").append(table);
		}
	});
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
	jQuery("#message").hide();
	var selGroup = jQuery("#groups").val();
	if (selGroup) {
		jQuery("#groups").click();
	}
});

/**
 * Updates the permission list when the save button has been pressed.
 * 
 * Version	Date		Developer			Description
 * 0.1		20/08/2012	Genevieve Turner	Initial
 */
jQuery("#updatePerm").live('click', function(){
	var value = jQuery("#groups").val();
	var url = "/DataCommons/rest/user/permissions/" + value;
	var username = jQuery("input[name='username']:checked").val();
	var permissions = new Array();
	jQuery("input[name='group_perm']:checked").each(function(i) {
		permissions.push(jQuery(this).val());
	});
	jQuery("#message").hide();
	jQuery.ajax({
		url: url,
		type: 'POST',
		dataType: "json",
		data: {
			username: username,
			group_perm: permissions,
		},
		success: function(data,textStatus,jqXHR) {
			jQuery("#message").addClass("msg-success");
			jQuery("#message").text("User permissions updated.");
			jQuery("#message").show();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			jQuery("#message").addClass("msg-error");
			jQuery("#message").text("Error updating user permissions");
			jQuery("#message").show();
		}
	});
});
