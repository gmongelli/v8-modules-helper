<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>

<c:if test="${environmentInfo.srcStartedOnly == 'true'
            || environmentInfo.srcRemoveStore == 'true'
            || environmentInfo.srcRemoveJahia == 'true'}">
    <div class="box-1">
        <h1>Selected Options</h1>

        <c:if test="${environmentInfo.srcStartedOnly == 'true'}">
            <span> <fmt:message
                    key="lbl.srcStartedOnly"></fmt:message></span>
        </c:if>
        <c:if test="${environmentInfo.srcRemoveStore == 'true'}">
            <br><span> <fmt:message
                    key="lbl.srcRemoveStore"></fmt:message></span>
        </c:if>
        <c:if test="${environmentInfo.srcRemoveJahia == 'true'}">
            <br><span> <fmt:message
                    key="lbl.srcRemoveJahia"></fmt:message></span>
        </c:if>
    </div>
</c:if>

<div class="box-1">

    <h1>Modules Report</h1>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Group ID</th>
            <th>jmix:cmContentTreeDisplayable</th>
            <th>serverSettings</th>
            <th>siteSettings</th>
            <th>Date format</th>
            <th>Spring</th>
        </tr>
        </thead>
        <c:forEach items="${migrationReport}" var="module">
            <tr>
                <td>${module.moduleName}/${module.moduleVersion}</td>
                <td>${module.moduleGroupId}</td>
                <td>${module.nodeTypes}</td>
                <td>${module.serverSettings}</td>
                <td>${module.siteSettings}</td>
                <td>${module.nodeTypesDate}</td>
                <td>${module.hasSpringBean}</td>
            </tr>
        </c:forEach>
    </table>

    <form:form modelAttribute="environmentInfo" class="form-horizontal" method="post">

        <button id="previous" class="btn btn-primary" type="submit" name="_eventId_previous">
            Back
        </button>
    </form:form>

    <c:set var="redirectUrl" value="${renderContext.mainResource.node.path}.html" scope="session" />

</div>


<div class="box-1">
    <h2>Functional Administrators</h2>
    <fmt:message key="link.functionalAdmin" var="linkFunctionalAdmin" />
    <p><a href="${linkFunctionalAdmin}">Migration Guide</a></p>
</div>

<div class="box-1">
    <h2>System Administrators</h2>
    <fmt:message key="link.systemAdmin" var="linkSystemAdmin" />
    <p><a href="${linkSystemAdmin}">Migration Guide</a></p>
</div>

<div class="box-1">
    <h2>Developers</h2>
    <fmt:message key="link.developers" var="linkDevelopers" />
    <p><a href="${linkDevelopers}">Migration Guide</a></p>
</div>

<div class="box-1">
    <h2>Known Issues</h2>
    <fmt:message key="link.knownIssues" var="linkKnownIssues" />
    <p><a href="${linkKnownIssues}">Documentation</a></p>
</div>
