<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <html>

        <head>
            <title>Dashboard - DLAM</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>
            <div class="container">
                <jsp:include page="header.jsp" />

                <h3 class="inline">Available Courses</h3>

                <c:if test="${sessionScope.user.role == 'INSTRUCTOR'}">
                    <a href="create-course">Create New Course</a>
                    <br><br>
                </c:if>

                <c:if test="${empty courses}">
                    <p>No courses available.</p>
                </c:if>

                <ul>
                    <c:forEach var="c" items="${courses}">
                        <li>
                            <strong>${c.title}</strong> - ${c.description}
                            <br>
                            <a href="activities?courseId=${c.id}">View Activities</a>
                        </li>
                        <br>
                    </c:forEach>
                </ul>
            </div>
        </body>

        </html>