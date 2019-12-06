<form name="frmBasicSearch" action="/DataCommons/rest/search" method="get">
	<p>
		<label for="basicSearchTerms">Search</label>
		<input class="text" type="text" name="q" id="idBasicSearchTerms" size="30" value=""/>
		<#-- To do check if logged in --#>
			<select name="filter">
				<option value="all" <c:if test="${param.filter == 'all'}">selected="selected" </c:if>>All</option>
				<option value="published" <c:if test="${param.filter == 'published'}">selected="selected" </c:if>>Published</option>
				<option value="team" <c:if test="${param.filter == 'team'}">selected="selected" </c:if>>Team</option>
			</select>
		<input type="hidden" name="limit" value="<c:out value='10' />" />
		<input type="submit" value="Search" />
	</p>
</form>