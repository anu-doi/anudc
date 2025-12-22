<#macro page title>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>${title?html}</title>
	<link href="//style.anu.edu.au/_anu/4/images/logos/anu.ico" rel="shortcut icon" type="image/x-icon"/>
	<link href="/DataCommons/static/css/sol.css" rel="stylesheet" type="text/css" media="screen" />
	<link href="/DataCommons/static/css/easy-autocomplete.min.css" rel="stylesheet" type="text/css" media="screen" />
<#-- <link href="/DataCommons/static/css/anu-bootstrap.css" rel="stylesheet" type="text/css" media="screen" />  -->
	<link rel="stylesheet" media="all" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
	<link href="/DataCommons/static/css/anu-bootstrap.css" rel="stylesheet" type="text/css" />
	<link href="/DataCommons/static/css/datacommons.css" rel="stylesheet" type="text/css" media="screen" />
	
	<script src="/DataCommons/static/js/jquery-3.7.1.min.js" type="text/javascript"></script>
	<script src="/DataCommons/static/js/jquery.validate.min.js" type="text/javascript"></script>
	<script src="/DataCommons/static/js/sol.js" type="text/javascript"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
	<script src="/DataCommons/static/js/datacommons.js" type="text/javascript"></script>

<#--	<script src="/DataCommons/static/js/bootstrap.min.js" type="text/javascript"></script> -->
</head>
<body>
	<div role="navigation">
		<a href="#main-content" class="visually-hidden focusable">
        	Skip to main content
      	</a>
    </div>
    <div class="anu-bnr-wrap-1240">
    	<div class="anu-wf-banner">
    	</div>
	<header aria-label="Header banner" class="anu-wf" data-title="Datacommons" data-subtitle="Library"
		data-domain="anulib.anu.edu.au" role="banner">
		<#if security??>
			<#if security.getUsername()??>
				<div>
					<div class="utility-menu">
						<ul class="utility-menu-list menu nav">
							<li class="nav-item"><a href="/DataCommons/rest/user" class="nav-link">${security.getUsername()}</a></li>
						</ul>
					</div>
				</div>
			</#if>
	</#if>
	</header>
	<div class="main-navigation">
		<div>
			<nav role="navigation" aria-labelledby="block-webstyle-main-menu-menu" id="block-webstyle-main-menu">
				<h2 class="visually-hidden" id="block-webstyle-main-menu-menu">Main navigation</h2>
				<div class='anu-wf-mobile-menu react-none d-block d-lg-none' data-showauthlinks="true">
					<ul>
						<li><a href="/DataCommons/rest/about">About</a></li>
						<li><a href="/DataCommons/rest/contribute">Contribute</a></li>
						<li><a href="/DataCommons/rest/upload/search">Data search</a></li>
						<li><a href="/DataCommons/rest/collreq">Data request</a></li>
						<li><a href="/DataCommons/rest/contact">Contact</a></li>
						<li><a href="/DataCommons/login-select">Login</a></li>
					</ul>
				</div>
				<div class='anu-wf-mega-menu d-none d-lg-block'>
					<div class="anu-wf-mega-menu-item"><a href="/DataCommons/rest/about">About</a></div>
					<div class="anu-wf-mega-menu-item"><a href="/DataCommons/rest/contribute">Contribute</a></div>
					<div class="anu-wf-mega-menu-item"><a href="/DataCommons/rest/upload/search">Data search</a></div>
					<div class="anu-wf-mega-menu-item"><a href="/DataCommons/rest/collreq">Data request</a></div>
					<div class="anu-wf-mega-menu-item"><a href="/DataCommons/rest/contact">Contact</a></div>
					<#if security??>
						<#if security.getUsername()??>
							<div class="anu-wf-mega-menu-item"><a href="/DataCommons/logout">Logout</a></div>
							<div class="anu-wf-mega-menu-item"><a href="/DataCommons/rest/admin">Administration</a></div>
						<#else>
							<div class="anu-wf-mega-menu-item"><a href="/DataCommons/login-select">Login</a></div>
						</#if>
					<#else>
						<div class="anu-wf-mega-menu-item"><a href="/DataCommons/login-select">Login</a></div>
					</#if>
				</div>
			</nav>
		</div>
	</div>
<main id="main-content" role="main">
<div id="body" class="container pt-2">
<h1>${title?html}</h1>
<#nested/>
</div>
</main>
	<footer class="anu-wf-footer" aria-label="Footer banner" role="contentinfo"></footer>
</div>
<script src="https://webstyle.anu.edu.au/widgets/bundle.js"></script>
</body>
</html>
</#macro>