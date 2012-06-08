/**
 * removeElement
 * 
 * Australian National University Data Commons
 * 
 * Removes an element and all its child elements.
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
 * </pre>
 * 
 * @param e
 *            Element to be deleted.
 */
function removeElement(e)
{
	jQuery(e).remove();
}

/**
 * Australian National University Data Commons
 * 
 * Clones the first &lt;p&gt; element and clears the value of its input element with type="text".
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
 * </pre>
 * 
 * @param e
 *            Element within there's a &lt;p&gt; element to clone.
 */
function cloneUrlFields(e)
{
	var cloned = jQuery(e).children("p:first").clone();
	cloned.find("input[type='text']").attr("value", "");
	cloned.find("input[type='button']").removeAttr("hidden");
	cloned.appendTo(jQuery(e));
}
