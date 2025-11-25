<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <html>

        <head>
            <title>My Submissions</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>
            <div class="container">
                <jsp:include page="header.jsp">
                    <jsp:param name="backLink" value="activities?courseId=${param.courseId}" />
                </jsp:include>
                <h2>My Submissions for Activity ID: ${activityId}</h2>

                <c:if test="${empty submissions}">
                    <p>No submissions yet.</p>
                </c:if>

                <c:forEach var="s" items="${submissions}">
                    <div class="activity-card">
                        <p><strong>Submitted on:</strong> ${s.submissionDate}</p>

                        <label>Your Answer:</label>
                        <div style="background: #f9f9f9; padding: 10px; border: 1px solid #eee; margin-bottom: 15px;">
                            ${s.content}
                        </div>

                        <hr>

                        <h3>Instructor Feedback</h3>
                        <p><strong>Grade:</strong>
                            <c:choose>
                                <c:when test="${not empty s.grade}">
                                    <span class="success">${s.grade}</span>
                                </c:when>
                                <c:otherwise>
                                    <span style="color: #7f8c8d;">Not graded yet</span>
                                </c:otherwise>
                            </c:choose>
                        </p>

                        <p><strong>Feedback:</strong><br>
                            <c:out value="${s.feedback}" default="No feedback provided." />
                        </p>
                    </div>
                </c:forEach>
            </div>
        </body>

        </html>