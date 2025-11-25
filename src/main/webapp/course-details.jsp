<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <html>

        <head>
            <title>Course Details</title>
        </head>

        <body>

            <h2>Course ID: ${courseId}</h2>

            <c:if test="${sessionScope.user.role == 'INSTRUCTOR'}">
                <a href="create-activity?courseId=${courseId}">Add Activity</a>
            </c:if>

            <h3>Activities</h3>

            <c:if test="${empty activities}">
                <p>No activities yet.</p>
            </c:if>

            <c:forEach var="a" items="${activities}">
                <div style="border: 1px solid #ccc; padding: 10px; margin: 8px 0;">
                    <strong>${a.title}</strong> <br>
                    Due: ${a.dueDate} <br><br>
                    ${a.description}
                </div>
            </c:forEach>

            <br>
            <a href="courses">Back to Dashboard</a>

        </body>

        </html>