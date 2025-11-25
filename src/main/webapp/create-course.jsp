<%@ page contentType="text/html;charset=UTF-8" %>
    <html>

    <head>
        <title>Create Course</title>
        <link rel="stylesheet" href="css/style.css">
    </head>

    <body>
        <div class="container">
            <h2>Create New Course</h2>

            <form method="post" action="courses">
                Title: <input type="text" name="title" required><br><br>

                Description:<br>
                <textarea name="description" rows="5" cols="50"></textarea><br><br>

                <button type="submit">Create Course</button>
            </form>

            <br>
            <a href="courses">Back to Dashboard</a>
        </div>
    </body>

    </html>