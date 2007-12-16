<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %><?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<c:set var="sql" value =""/>

<c:choose>
<c:when test="${empty sessionScope['username']}" >
  <users/>
</c:when>

<c:when test="${empty sessionScope['isadmin']}" >
  <users/>
</c:when>

<c:when test="${sessionScope['isadmin']=='true'}" >
	<c:set var="sql" value="SELECT user_name,email,registration_status,registration_date,title,firstname,lastname,address,country,webpage,affiliation from users order by user_name" />
</c:when>
<c:otherwise>
	<c:set var="sql" value="SELECT user_name,email,registration_status,registration_date,title,firstname,lastname,address,country,webpage,affiliation from users where user_name= \"${sessionScope['username']}\" order by user_name" />
</c:otherwise>
</c:choose>
<users>
	<c:if test="${!empty sql}">
		<c:catch var="error">
	  		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				${sql}
			</sql:query>

			<c:forEach var="row" items="${rs.rows}">
					<user user_name='${row.user_name}'
						email='${row.email}'
						registration_status='${row.registration_status}'
						registration_date='${row.registration_date}'
						title='${row.title}'
						firstname='${row.firstname}'
						lastname='${row.lastname}'
						address='${row.address}'
						country='${row.country}'
						affiliation='${row.affiliation}'
						webpage='${row.webpage}'


					  <sql:query var="rs_user" dataSource="jdbc/qmrf_documents">
								SELECT user_name,role_name from tomcat_users.user_roles where user_name=? and role_name regexp '^qmrf_';
								<sql:param value="${row.user_name}"/>
						</sql:query>
						<c:forEach var="rs_row" items="${rs_user.rows}">
								role='${rs_row.role_name}'
						</c:forEach>
						/>
		  	</c:forEach>


	  	</c:catch>
	  	<c:if test="${!empty error}">
	  		${error}
	  	</c:if>
	  </c:if>

</users>