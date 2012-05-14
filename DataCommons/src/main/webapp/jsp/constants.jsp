<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<!-- Link to CAS Server. -->
<c:set var="casServer" value="https://login-test.anu.edu.au" scope="request" />

<!-- Number of items per page in search results. -->
<c:set var="searchItemsPerPage" value="10" scope="request" />