function initHandlers() {
	jQuery("#btnReverify").on("click", reverify);
	jQuery("#btnFixIssues").on("click", recomplete);
}

function reverify() {
	location.reload();
}

function recomplete() {
	window.location = window.location.href.split("?")[0] + "?task=complete";
}