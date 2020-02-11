<#macro page title>
<!DOCTYPE html>
<html>
<head>
	<title>${title?html}</title>
	<link href="//style.anu.edu.au/_anu/4/images/logos/anu.ico" rel="shortcut icon" type="image/x-icon"/>
	<link href="/DataCommons/static/css/sol.css" rel="stylesheet" type="text/css" media="screen" />
	<link href="/DataCommons/static/css/easy-autocomplete.min.css" rel="stylesheet" type="text/css" media="screen" />
	<link href="/DataCommons/static/css/anu-bootstrap.css" rel="stylesheet" type="text/css" media="screen" />
	<link href="/DataCommons/static/css/datacommons.css" rel="stylesheet" type="text/css" media="screen" />
	
	<script src="/DataCommons/static/js/jquery-3.3.1.min.js" type="text/javascript"></script>
	<script src="/DataCommons/static/js/jquery.validate.min.js" type="text/javascript"></script>
	<script src="/DataCommons/static/js/sol.js" type="text/javascript"></script>
	<script src="/DataCommons/static/js/bootstrap.min.js" type="text/javascript"></script>
	<script src="/DataCommons/static/js/datacommons.js" type="text/javascript"></script>
</head>
<body>
<header class="bg-dark text-light clearfix">
	<div id="bnr-wrap" class="container">
		<div id="bnr-left">
		<div class="float-left">
			<a class="" href="http://www.anu.edu.au/">
				<img class="text-white" src="//style.anu.edu.au/_anu/4/images/logos/2x_anu_logo_small.png" alt="The Australian National University" width="150px"/>
			</a>
		</div>
		</div>
		<div id="bnr-mid" class="float-left">
			<div class="float-left">
				<img class="" src="//style.anu.edu.au/_anu/4/images/logos/pipe_logo_small.png" alt="" width="66" height="51"/>
			</div>
			<div id="bnr-h-lines" class="text-light float-left">
				<div class="bnr-line-1 bnr-2line">
				<a href="/">Data Commons</a>
				</div>
				<div class="bnr-line-2">
				<a href="//anulib.anu.edu.au">Library</a>
				</div>
			</div>
		</div>
		<div id="bnr-right" class="float-right">
			<form id="searchForm" role="search" method="get" action="//find.anu.edu.au/search">
				<div class="input-group">
					<input aria-label="Search query" id="qt" class="form-control" type="search" placeholder="Search ANU web, staff &maps" name="q"/>
					<div class="srch-divide">
						<div class="srch-divide2"></div>
					</div>
					<button id="search1" class="btn-go" name="search1" value="Go"><img src="//style.anu.edu.au/_anu/4/images/buttons/search-black.png" alt="Search"/></button>
				</div>
			</form>
		</div>
	<#-- To Do -->
	<#if security??>
		<#if security.getUsername()??>
		<div id="bnr-low" class="float-right mb-3">
			<nav class="navbar navbar-expand navbar-dark">
			<ul class="navbar-nav">
				<li class="nav-item"><a href="/DataCommons/rest/user" class="nav-link">${security.getUsername()}</a></li>
			</ul>
			</nav>
		</div>
		</#if>
	</div>
	</#if>
</header>
<nav class="navbar navbar-expand-sm navbar-dark bg-black">
	<div class="container">
	<div class="anu-width">
		<div class="collapse navbar-collapse">
			<ul class="navbar-nav mr-auto">
				<li><a class="nav-link nav-brand" href="/DataCommons"><image src="//style.anu.edu.au/_anu/4/images/buttons/home-white-over.png" alt="Home"/></a></li>
				<li class="nav-item"><a class="nav-link text-light" href="https://openresearch.anu.edu.au/about-open-research-anu">About</a></li>
				<li class="nav-item"><a class="nav-link text-light" href="/DataCommons/rest/contribute">Contribute</a></li>
				<li class="nav-item"><a class="nav-link text-light" href="/DataCommons/rest/upload/search">Data search</a></li>
				<li class="nav-item"><a class="nav-link text-light" href="/DataCommons/rest/collreq">Data request</a></li>
				<li class="nav-item"><a class="nav-link text-light" href="https://openresearch.anu.edu.au/contact">Contact</a></li>
				<#if security??>
					<#if security.getUsername()??>
				<li class="nav-item"><a class="nav-link text-light" href="/DataCommons/logout">Logout</a></li>
				<li class="nav-item"><a class="nav-link text-light" href="/DataCommons/rest/admin">Administration</a></li>
					<#else>
				<li class="nav-item"><a class="nav-link text-light" href="/DataCommons/login-select">Login</a></li>
					</#if>
				<#else>
				<li class="nav-item"><a class="nav-link text-light" href="/DataCommons/login-select">Login</a></li>
				</#if>
			</ul>
		</div>
	</div>
	</div>
</nav>
<main id="body-wrap" role="main">
<div id="body" class="container pt-2">
<h1>${title?html}</h1>
<#nested/>
</div>
</main>
<footer id="footer-wrap" class="bg-dark text-light">
	<div class="container bg-dark">
	<div id="anu-footer" class="bg-dark anu-width pt-2">
		<div id="anu-detail">
			<ul class="bg-dark text-light list-unstyled">
				<li class=""><a class="text-light" href="http://www.anu.edu.au/contact">Contact ANU</a></li>
                <li class=""><a class="text-light" href="http://www.anu.edu.au/copyright">Copyright</a></li>
                <li class=""><a class="text-light" href="http://www.anu.edu.au/disclaimer">Disclaimer</a></li>
				<li class=""><a class="text-light" href="http://www.anu.edu.au/privacy">Privacy</a></li>
				<li class=""><a class="text-light" href="http://www.anu.edu.au/freedom-of-information">Freedom of Information</a></li>
			</ul>
		</div>
		<div id="anu-address">
			<p>+61 2 6125 5111<br/>
			The Australian National University, Canberra<br/>
			CRICOS Provider : 00120C<br/>
			<span class="NotAPhoneNumber">ABN : 52 234 063 906</span></p>
		</div>
		<div id="anu-groups">
			<div class="anu-ftr-go8 px-2"><a href="http://www.anu.edu.au/about/partnerships/group-of-eight"><img class="text-white" src="http://style.anu.edu.au/_anu/4/images/logos/2x_GroupOf8.png" alt="Group of Eight Member" /></a></div>
			<div class="anu-ftr-iaru px-2"><a href="http://www.anu.edu.au/about/partnerships/international-alliance-of-research-universities"><img class="text-white" src="http://style.anu.edu.au/_anu/4/images/logos/2x_iaru.png" alt="IARU" /></a></div>
			<div class="anu-ftr-apru px-2"><a href="http://www.anu.edu.au/about/partnerships/association-of-pacific-rim-universities"><img class="text-white" src="http://style.anu.edu.au/_anu/4/images/logos/2x_apru.png" alt="APRU" /></a></div>
			<div class="anu-ftr-edx px-2"><a href="http://www.anu.edu.au/about/partnerships/edx"><img class="text-white" src="http://style.anu.edu.au/_anu/4/images/logos/2x_edx.png" alt="edX" /></a></div>
		</div>
	</div>
	</div>
</footer>
</body>
</html>
</#macro>