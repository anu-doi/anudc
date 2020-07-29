(function(callback) {
	if (typeof define === 'function' && define.amd) {
		define([ 'core/AbstractWidget' ], callback);
	} else {
		callback();
	}
}
		(function() {

			(function($) {

				AjaxSolr.ResultWidget = AjaxSolr.AbstractWidget
						.extend({
							start : 0,

							beforeRequest : function() {
								$(this.target)
										.html(
												$('<img>')
														.attr('src',
																'/DataCommons/images/ajax-loader.gif'));
							},

							facetLinks : function(facet_field, facet_values) {
								var links = [];
								if (facet_values) {
									for ( var i = 0, l = facet_values.length; i < l; i++) {
										if (facet_values[i] !== undefined) {
											links
													.push($('<a href="#"></a>')
															.text(
																	facet_values[i])
															.click(
																	this
																			.facetHandler(
																					facet_field,
																					facet_values[i])));
										} else {
											links
													.push('no items found in current selection');
										}
									}
								}
								return links;
							},

							facetHandler : function(facet_field, facet_value) {
								var self = this;
								return function() {
									self.manager.store.remove('fq');
									self.manager.store
											.addByValue(
													'fq',
													facet_field
															+ ':'
															+ AjaxSolr.Parameter
																	.escapeValue(facet_value));
									self.doRequest();
									return false;
								};
							},

							afterRequest : function() {
								$(this.target).empty();
								for ( var i = 0, l = this.manager.response.response.docs.length; i < l; i++) {
									var doc = this.manager.response.response.docs[i];
									$(this.target).append(this.template(doc));

								}
							},

							template : function(doc) {
								var snippet = '';
								if (typeof doc.content === 'undefined') {
									snippet += 'Binary file. Extension: ' + doc.ext + ', Mime Type: ' + doc.mime_type;
								}
								else {
									if (doc.content.length > 0) {
										if (doc.content.length > 300) {
											snippet += doc.content.substring(0, 300);
											snippet += '<span style="display:none;">'
												+ doc.content.substring(300, 1000);
											snippet += '</span> <a href="#" class="more">more</a>';
										} else {
											snippet += doc.content;
										}
									} else {
										snippet += 'Binary file. Extension: ' + doc.ext + ', Mime Type: ' + doc.mime_type;
									}
								}
								
								var fileUrl = "../records/" + doc.id.replace("/", "/data/");
								var output = '<div><h4><a href=\'' + fileUrl + '\'>' + doc.name + '.' + doc.ext  + '</a></h4>';
								output += "<p class='text-grey'>" + snippet + '</p></div>';
								return output;
							},

							init : function() {
								$(document).on(
										'click',
										'a.more',
										function() {
											var $this = $(this), span = $this
													.parent().find('span');

											if (span.is(':visible')) {
												span.hide();
												$this.text('more');
											} else {
												span.show();
												$this.text('less');
											}

											return false;
										});
							}
						});

			})(jQuery);

		}));
