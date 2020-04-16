(function($) {
	var filterTitles = {'title_str':'Title','author_str':'Author','ext':'Extension','name':'Query'};
	
	AjaxSolr.CurrentSearchWidget = AjaxSolr.AbstractWidget.extend({
		start : 0,

		afterRequest : function() {
			var self = this;
			var links = [];

			var q = this.manager.store.get('q').val();
			if (q != '*:*') {
				links.push($('<a href="#"></a>').text('(x) ' + q).click(
						function() {
							self.manager.store.get('q').val('*:*');
							self.doRequest();
							return false;
						}));
			}

			var fq = this.manager.store.values('fq');
			for ( var i = 0, l = fq.length; i < l; i++) {
				var filterFields = fq[i].split(':');
				var filterValue = fq[i];
				if (filterFields.length>1) {
					filterValue = filterTitles[filterFields[0]] + ':' + filterFields[1];
				}
				
				links.push($('<a href="#"></a>').text('(x) ' + filterValue).click(
						self.removeFacet(fq[i])));
			}

			if (links.length > 1) {
				links.unshift($('<a href="#"></a>').text('Clear all options').click(
						function() {
							self.manager.store.get('q').val('*:*');
							self.manager.store.remove('fq');
							self.doRequest();
							return false;
						}));
			}
			if (links.length) {
				var $target = $(this.target);
				$target.empty();
				for ( var i = 0, l = links.length; i < l; i++) {
					$target.append($('<li></li>').append(links[i]));
				}
			} else {
				$(this.target).html('<li>Viewing all documents</li>');
			}
		},

		removeFacet : function(facet) {
			var self = this;
			return function() {
				if (self.manager.store.removeByValue('fq', facet)) {
					self.doRequest();
				}
				return false;
			};
		}
	});
})(jQuery);