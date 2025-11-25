<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <html>

        <head>
            <title>View Submissions</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>
            <div class="container">
                <h2>Submissions for Activity ID: ${activityId}</h2>

                <c:if test="${empty submissions}">
                    <p>No submissions yet.</p>
                </c:if>

                <c:forEach var="s" items="${submissions}">
                    <div class="activity-card">
                        <p><strong>Student ID:</strong> ${s.studentId}</p>
                        <p><strong>Submitted on:</strong> ${s.submissionDate}</p>
                        <p><strong>Content:</strong><br> ${s.content}</p>

                        <hr>

                        <form action="grade-submission" method="post">
                            <input type="hidden" name="submissionId" value="${s.id}">
                            <input type="hidden" name="activityId" value="${activityId}">
                            <input type="hidden" name="courseId" value="${courseId}">

                            <label>Grade:</label>
                            <input type="text" name="grade" value="${s.grade}" placeholder="e.g. A, 90/100" required>

                            <label>Feedback:</label>
                            <textarea name="feedback" rows="3">${s.feedback}</textarea>

                            <button type="submit">Update Grade</button>
                        </form>
                    </div>
                </c:forEach>

                <br>
                <a href="activities?courseId=${courseId}">Back to Course Activities</a>
            </div>
        </body>

        </html>