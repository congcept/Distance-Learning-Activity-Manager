<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <html>

        <head>
            <title>Course Details</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>
            <div class="container">

                <h2>Course ID: ${courseId}</h2>

                <c:if test="${sessionScope.user.role == 'INSTRUCTOR'}">
                    <a href="create-activity?courseId=${courseId}">Add Activity</a>
                </c:if>

                <h3>Activities</h3>

                <c:if test="${empty activities}">
                    <p>No activities yet.</p>
                </c:if>

                <c:forEach var="a" items="${activities}">
                    <div class="activity-card">
                        <div class="activity-title"><strong>${a.title}</strong></div>
                        <div class="activity-date">Due: ${a.dueDate}</div>
                        ${a.description}

                        <c:if test="${sessionScope.user.role == 'STUDENT'}">
                            <br><br>
                            <a href="submit-activity?activityId=${a.id}&courseId=${courseId}">Submit Assignment</a>
                        </c:if>
                        <c:if test="${sessionScope.user.role == 'INSTRUCTOR'}">
                            <br><br>
                            <a href="view-submissions?activityId=${a.id}&courseId=${courseId}">View Submissions</a>
                        </c:if>
                    </div>
                </c:forEach>

                <br>
                <a href="courses">Back to Dashboard</a>
            </div>
        </body>

        </html>