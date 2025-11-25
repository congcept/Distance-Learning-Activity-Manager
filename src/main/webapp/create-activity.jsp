<%@ page contentType="text/html;charset=UTF-8" %>
    <html>

    <head>
        <title>Create Activity</title>
        <link rel="stylesheet" href="css/style.css">
    </head>

    <body>
        <div class="container">
            <h2>Create Activity</h2>

            <form method="post" action="create-activity">
                <input type="hidden" name="courseId" value="${courseId}">

                Title: <input type="text" name="title" required><br><br>
                Description:<br>
                <textarea name="description" rows="5" cols="50"></textarea><br><br>

                Due date: <input type="date" name="dueDate"><br><br>

                <button type="submit">Create</button>
            </form>

            <br>
            <a href="activities?courseId=${courseId}">Back to Course Details</a>
        </div>
    </body>

    </html>