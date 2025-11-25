<%@ page contentType="text/html;charset=UTF-8" %>
    <html>

    <head>
        <title>Create Activity</title>
        <link rel="stylesheet" href="css/style.css">
    </head>

    <body>
        <div class="container">
            <jsp:include page="header.jsp">
                <jsp:param name="backLink" value="activities?courseId=${param.courseId}" />
            </jsp:include>
            <h2>Create Activity</h2>

            <form method="post" action="create-activity">
                <input type="hidden" name="courseId" value="${courseId}">

                Title: <input type="text" name="title" required><br><br>
                Description:<br>
                <textarea name="description" rows="5" cols="50"></textarea><br><br>

                Due date: <input type="date" name="dueDate"><br><br>

                <button type="submit">Create</button>
            </form>
        </div>
    </body>

    </html>