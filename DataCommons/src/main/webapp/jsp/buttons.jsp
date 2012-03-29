<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:content layout="narrow">
<p>Testing side bar</p>
<p><input type="button" id="editButton" name="editButton" value="Edit" onclick="window.location='display/edit?item=${param.item}&amp;tmplt=${param.tmplt}&amp;layout=${param.layout}'" /></p>
<p><input type="button" id="itemLinkButton" name="itemLinkButton" value="Link to Item" /></p>
</anu:content>