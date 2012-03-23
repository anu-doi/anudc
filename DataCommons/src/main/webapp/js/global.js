/**
 * DisplayResource
 * 
 * Australian National University Data Comons
 * 
 * Global javascript file to be included in all HTML documents. Contains global constants and utility methods. 
 * 
 * Version	Date		Developer			Description
 * 0.1		08/03/2012	Rahul Khanna		Initial
 * 0.2		20/03/2012	Genevieve Turner	Added functions for adding/removing rows
 * 
 */

/**
 * Takes an integer as a parameter and formats the integer with commas as thousand separators. E.g. 123456789 returns 123,456,789.
 * 
 * @param nStr
 *            A number as string. E.g. "123456"
 * @returns A formatted integer as string. E.g. "123,456".
 */
function groupDigits(nStr)
{
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1))
	{
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}

/**
 * cloneElement
 * 
 * Clones the given element
 * 
 * Version	Date		Developer			Description
 * 0.2		20/03/2012	Genevieve Turner	Added functions for adding/removing rows
 * 
 * @param elementToClone The elemnt to take a copy of
 */
function cloneElement(elementToClone)
{
	var newElement = elementToClone;
	// Verify the field is an element
	if (newElement.nodeType != 1) {
		newElement = getNextSiblingElement(elementToClone.parentNode.childNodes[0]);
	}
	console.log(newElement.nodeType);
	console.log(newElement);
	cloneNode = newElement.cloneNode(true); //.find("input:text").value("")
	elementToClone.parentNode.appendChild(cloneNode);
}

/**
 * 
 * Version	Date		Developer			Description
 * 0.2		20/03/2012	Genevieve Turner	Added functions for adding/removing rows
 * 
 * @param elementToRemove The element to remove
 */
function removeElement(elementToRemove, minRows)
{
	//TODO remove stuff about min rows
	var minRows = 1;
	var firstSiblingElement = elementToRemove.parentNode.childNodes[0];
	
	// Verify that the first child node is of element type
	if (firstSiblingElement.nodeType != 1) {
		firstSiblingElement = getNextSiblingElement(firstSiblingElement);
	}
	
	// If the row is a table with a table header we want to account for the table header row
	if (firstSiblingElement.getElementsByTagName('th').length > 0) {
		minRows = 2;
	}
	
	if (elementToRemove.parentNode.rows.length > minRows) {
		elementToRemove.parentNode.removeChild(elementToRemove);
	}
}

/**
 * getNextSiblingElement
 * 
 * Returns the next sibling that is an element
 * 
 * Version	Date		Developer			Description
 * 0.2		20/03/2012	Genevieve Turner	Added functions for adding/removing rows
 * 
 * @param curNode The node to get the next sibling of
 * @returns The next node that is an element
 */
function getNextSiblingElement(curNode)
{
	var nextSiblingElement = curNode.nextSibling;
	
	while (nextSiblingElement != null && nextSiblingElement.nodeType != 1)
	{
		nextSiblingElement = nextSiblingElement.nextSibling;
	}
	
	return nextSiblingElement;
}

/**
 * getNextTableRow
 * 
 * Returns the next table node
 * 
 * Version	Date		Developer			Description
 * 0.2		20/03/2012	Genevieve Turner	Added functions for adding/removing rows
 * 
 * @param curNode The node to get the next table node of
 * @returns The next node
 */
function getNextTableRow(curNode)
{
	var nextRowElement = curNode.nextSibling.nextSibling.childNodes[1].childNodes[1];
	
	return nextRowElement;
}
