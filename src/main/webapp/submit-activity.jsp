<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Submit Activity</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h2>Submit Activity</h2>
        
        <form method="post" action="submit-activity">
            <input type="hidden" name="activityId" value="${param.activityId}">
            <input type="hidden" name="courseId" value="${param.courseId}">
            
            <label>Your Answer / Content:</label>
            <textarea name="content" rows="10" cols="50" required></textarea><br><br>
            
            <button type="submit">Submit Assignment</button>
        </form>
        
        <br>
        <a href="activities?courseId=${param.courseId}">Back to Activities</a>
    </div>
</body>
</html>
