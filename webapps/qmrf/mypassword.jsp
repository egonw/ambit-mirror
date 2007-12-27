<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

	<c:choose>
	<c:when test="${empty sessionScope['username']}">
			<c:redirect url="protected.jsp"/>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'false'}">
		<c:if test="${(!empty param.user_name) and (sessionScope['username'] != param.user_name)}">
			<c:redirect url="protected.jsp"/>
		</c:if>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'true'}">
		<!-- OK, can change everything-->
	</c:when>
	<c:otherwise>
		<c:if test="${(!empty param.user_name) and (sessionScope['username'] != param.user_name)}">
			<c:redirect url="protected.jsp"/>
		</c:if>
	</c:otherwise>
</c:choose>

<html>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<head>
<meta name="description" content="(Q)MRF database">
<meta name="keywords" content="ambit,qsar,qmrf,structure search">
<meta name="robots"content="index,follow">
<META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
<meta name="copyright" content="Copyright 2007. Nina Jeliazkova nina@acad.bg">
<meta name="author" content="Nina Jeliazkova">
<meta name="language" content="English">
<meta name="revisit-after" content="7">
<link rel="SHORTCUT ICON" href="favicon.ico"/>
</head>
<title>(Q)SAR Model Reporting Format (QMRF) Inventory</title>
<body bgcolor="#ffffff">

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="profile"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>


<c:set var="success" value=""/>

<c:if test="${!empty param.user_name}">


<c:choose>
<c:when test="${fn:length(param.newpassword)<6}">
				<div class="error">
				Password should consists of at least 6 characters
			</div>
</c:when>
<c:when test="${!empty param.password && !empty param.newpassword && !empty param.confirm && (param.newpassword eq param.confirm)}">


				<sql:setDataSource dataSource="jdbc/tomcat_users"/>

				<c:catch var='transactionException2'>
						<sql:transaction>

								<sql:update var="rs1" >
										update users set user_pass=md5(?) where user_name=? and user_pass=md5(?)
										<sql:param value="${param.newpassword}"/>
										<sql:param value="${param.user_name}"/>
										<sql:param value="${param.password}"/>
								</sql:update>

					</sql:transaction>
			</c:catch>

		<c:choose>
		<c:when test='${not empty transactionException2}'>
						<div class="error">
									error ${exception}
						</div>
		</c:when>
		<c:otherwise>
					<div class="success">
						<c:set var="success" value="true"/>
				Password for user <b>${param.user_name}</b> changed successfully.<br/> Back to <a href="myprofile.jsp">My profile</a>

			</div>

		</c:otherwise>
		</c:choose>

</c:when>
<c:otherwise>
		<div class="error">
				Invalid passwords or passwords doesn't match.
	</div>
</c:otherwise>
</c:choose>

<c:if test="${empty success}">

	<form method="POST" name="password_form" action="mypassword.jsp">
		<h3>Change password for user name: <font color="red">${param.user_name}</font></h3>
	<table>
	<tr>
	<th>
		Old Password
	</th>
	<td>
		<input type="password" size="16" name="password"/>
	</td>
	</tr>

	<tr>
	<th>
		New Password
	</th>
	<td>
		<input type="password" size="16" name="newpassword"/>
	</td>
	</tr>

	<tr>
	<th>
		Confirm new password
	</th>
	<td>
		<input type="password" size="16" name="confirm"/>
	</td>
	</tr>

	<tr>
	<th>

	</th>
	<td>
		<input type="hidden" name="user_name" value="${param.user_name}"/>

		<input type="submit" name="Submit"/>
	</td>
	</tr>
	</table>
</form>
</c:if>
</c:if>
<!-- user_name -->





<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value="${isadmin}"/>
		</jsp:include>
	</p>
</div>
</body>
</html>
