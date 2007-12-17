<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<c:set var="thispage" value='admin_review.jsp'/>


<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin'] eq 'false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>

<c:if test="${empty param.id}" >
  <c:redirect url="/admin.jsp"/>
</c:if>

<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>

  </head>
  <body>


<jsp:include page="menu.jsp" flush="true"/>

<jsp:include page="menuadmin.jsp" flush="true">
    <jsp:param name="highlighted" value="review"/>
</jsp:include>


<c:set var="xslt_url" value="/WEB-INF/xslt/qmrf_adminedit.xsl"/>
<c:set var="title" value="Reviewing"/>
<c:set var="submit_caption" value="Update"/>

<c:if test="${param.status eq 'published'}">
		<c:set var="xslt_url" value="/WEB-INF/xslt/qmrf_adminpublish.xsl"/>
		<c:set var="title" value="Publishing"/>
		<c:set var="submit_caption" value="Publish"/>
</c:if>
<c:if test="${param.status eq 'returned for revision'}">
		<c:set var="submit_caption" value="Return for revision"/>
</c:if>

<c:set var="updateXML" value="false"/>
<c:if test="${!empty param.QMRF_number}">
	<c:set var="updateXML" value="true"/>
</c:if>
<c:if test="${!empty param.date_publication}">
	<c:set var="updateXML" value="true"/>
</c:if>
<c:if test="${!empty param.keywords}">
	<c:set var="updateXML" value="true"/>
</c:if>
<c:if test="${!empty param.summary_comments}">
	<c:set var="updateXML" value="true"/>
</c:if>
<c:if test="${updateXML eq true}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select xml from documents where idqmrf=?
			<sql:param value="${param.id}"/>
		</sql:query>

		<c:forEach var="row" items="${rs.rows}">
			<catch var="error">
				<c:import var="qmrfToHtml" url="/WEB-INF/xslt/update_keywords_comments.xsl"/>
				<c:set var="docnew" scope="page">
					<x:transform xml="${fn:trim(row.xml)}" xslt="${qmrfToHtml}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
							<x:param name="keywords" value="${param.keywords}"/>
							<x:param name="summary_comments" value="${param.summary_comments}"/>
					</x:transform>
				</c:set>
			</catch>
			<c:if test="${!empty error}">
				<div class="error">
					${error}
				</div>
			</c:if>
		</c:forEach>

		<c:choose>
			<c:when test="${param.status eq 'under review'}">
				<c:catch var="error_update">
					<sql:update var="updateCount" dataSource="jdbc/qmrf_documents">
						 update documents set qmrf_number=null,xml=?,updated=now(),status='under review',reviewer=? where idqmrf=?
						<sql:param value="${docnew}"/>
						<sql:param value="${sessionScope['username']}"/>
						<sql:param value="${param.id}"/>
					</sql:update>
				</c:catch>
				<c:if test="${!empty error_update}">
					<div class="error">${error_update}</div>
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="error">Status ${param.status} different than expected!</div>
			</c:otherwise>
		</c:choose>

		<c:choose>
		<c:when test="${!empty error_update}">
			<div class="error">Error updating the document 	<br> ${error_update}</div>
		</c:when>
		<c:otherwise/>
		</c:choose>

</c:if>



<c:set var="report">
	select idqmrf,qmrf_number,user_name,updated,status from documents where idqmrf = ${param.id} and (status = 'submitted' || status = 'under review')
</c:set>

<jsp:include page="records.jsp" flush="true">
	<jsp:param name="sql" value="${report}"/>
	<jsp:param name="qmrf_number" value="QMRF#"/>
    <jsp:param name="user_name" value="Author"/>
    <jsp:param name="updated" value="Last updated"/>
	<jsp:param name="status" value="Status"/>
	<jsp:param name="actions" value="admin"/>
</jsp:include>



<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select idqmrf,qmrf_number,xml from documents where idqmrf=? and (status = 'submitted' || status = 'under review')
	<sql:param value="${param.id}"/>
</sql:query>

<c:if test="${rs.rowCount>0}">
	<table width="100%" border="0">
		<jsp:include page="list_attachments.jsp" flush="true">
			<jsp:param name="id" value="${param.id}"/>
		</jsp:include>
	</table>

	<form method="POST" name="qmrfform" action='<%= response.encodeURL("admin_review.jsp") %>'>

		<c:forEach var="row" items="${rs.rows}">
			<c:set var="doc" value="${fn:trim(row.xml)}"/>
			<input type="hidden" name="id" value="${row.idqmrf}">
			<input type="hidden" name="status" value="${param.status}">



				<c:catch var="error">
					<c:import var="xsl" url="${xslt_url}"/>
					<x:transform xml="${doc}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd" />
				</c:catch>

				<c:if test="${!empty error}">
					<div class="error">ERROR ${error}</div>
				</c:if>



		</c:forEach>
	</form>
</c:if>
</body>
</html>
