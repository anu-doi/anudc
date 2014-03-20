jQuery("#aaflist").live('click', function() {
	console.log('clicked!');
	console.log('stuff');
	jQuery.ajax({
		url: "/idplist/idp_web_list.xml",
		dataType: "xml",
		success: function(data) {
			console.log('successful response!');
			console.log(data['identity-provider']);
			
			var ul = jQuery("<ul class='noindent'></ul>");
			
			var identityProviders = jQuery(data).find("identity-provider");
			identityProviders.sort(function(a, b) {
				return jQuery(a).find("display-name").text().toLowerCase().localeCompare(jQuery(b).find("display-name").text().toLowerCase());
			});
			jQuery.each(identityProviders, function(index, item) {
				var displayName = jQuery(item).find("display-name").text();
				console.log(displayName);
				var li = jQuery("<li></li>").html(displayName);
				ul.append(li);
			});
			jQuery("#idplist").html('');
			jQuery("#idplist").append("<h2>AAF Member Institutions</h2>").append(ul);
		}
	});
});
