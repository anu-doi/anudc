var manager;
(function($) {

	$(function() {

		manager = new AjaxSolr.Manager({
			solrUrl : solrUrl
		});

		manager.addWidget(new AjaxSolr.ResultWidget({
			id : 'result',
			target : '#docs'
		}));
		manager.addWidget(new AjaxSolr.PagerWidget({
			id : 'pager',
			target : '#pager',
			prevLabel : '&lt;',
			nextLabel : '&gt;',
			innerWindow : 1,
			renderHeader : function(perPage, offset, total) {
				jQuery('#pager-header').html(
						jQuery('<span></span>').text(
								'Displaying ' + Math.min(total, offset + 1)
										+ ' to '
										+ Math.min(total, offset + perPage)
										+ ' of ' + total));
			}
		}));
		var fields = [ 'title_str', 'author_str', 'ext'];
		for (var i = 0, l = fields.length; i < l; i++) {
		  manager.addWidget(new AjaxSolr.TagcloudWidget({
		    id: fields[i],
		    target: '#' + fields[i],
		    field: fields[i]
		  }));
		}
		manager.addWidget(new AjaxSolr.CurrentSearchWidget({
			  id: 'currentsearch',
			  target: '#selection',
			}));

		manager.addWidget(new AjaxSolr.AutocompleteWidget({
			  id: 'text',
			  target: '#search',
			  fields: [ 'name', 'title_str', 'author_str']
			}));
		
		manager.init();

		manager.store.addByValue('q', '*:*');
		
	    var params = {
	    	      facet: true,
	    	      'facet.field': [ 'title_str', 'author_str', 'ext'],
	    	      'facet.limit': 20,
	    	      'facet.mincount': 1,
	    	      'f.topics.facet.limit': 50,
//	    	      'f.countryCodes.facet.limit': -1,
//	    	      'facet.date': 'date',
//	    	      'facet.date.start': '1987-02-26T00:00:00.000Z/DAY',
//	    	      'facet.date.end': '1987-10-20T00:00:00.000Z/DAY+1DAY',
//	    	      'facet.date.gap': '+1DAY',
	    	      'json.nl': 'map'
	    	    };

	    for (var name in params) {
	        manager.store.addByValue(name, params[name]);
	      }
		
		manager.doRequest();
	});
})(jQuery);
