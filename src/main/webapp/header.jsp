<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <div class="header">
            <div class="left-section">
                <c:if test="${not empty param.backLink}">
                    <a href="${param.backLink}" class="back-btn">Back</a>
                </c:if>
                <div class="user-info">
                    <c:if test="${not empty sessionScope.user}">
                        <span>Welcome, <strong>${sessionScope.user.fullName}</strong> (${sessionScope.user.role})</span>
                    </c:if>
                </div>
            </div>
            <div class="actions">
                <c:if test="${not empty sessionScope.user}">
                    <a href="logout" class="logout-btn">Logout</a>
                </c:if>
            </div>
        </div>