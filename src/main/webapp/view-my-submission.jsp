<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <html>

        <head>
            <title>My Submission</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>
            <div class="container">
                <h2>My Submission</h2>

                <div class="activity-card">
                    <p><strong>Submitted on:</strong> ${submission.submissionDate}</p>

                    <label>Your Answer:</label>
                    <div style="background: #f9f9f9; padding: 10px; border: 1px solid #eee; margin-bottom: 15px;">
                        ${submission.content}
                    </div>

                    <hr>

                    <h3>Instructor Feedback</h3>
                    <p><strong>Grade:</strong>
                        <c:choose>
                            <c:when test="${not empty submission.grade}">
                                <span class="success">${submission.grade}</span>
                            </c:when>
                            <c:otherwise>
                                <span style="color: #7f8c8d;">Not graded yet</span>
                            </c:otherwise>
                        </c:choose>
                    </p>

                    <p><strong>Feedback:</strong><br>
                        <c:out value="${submission.feedback}" default="No feedback provided." />
                    </p>
                </div>

                <br>
                <a href="activities?courseId=${param.courseId}">Back to Activities</a>
            </div>
        </body>

        </html>