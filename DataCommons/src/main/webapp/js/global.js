/**
 * Global javascript file to be included in all HTML documents. Contains global constants and utility methods. 
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
