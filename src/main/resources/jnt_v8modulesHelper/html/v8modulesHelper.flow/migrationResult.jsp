<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>

<script type="text/javascript">
    function downloadCSV(csv, filename) {
        var csvFile;
        var downloadLink;

        csvFile = new Blob([csv], {type: "text/csv"});
        downloadLink = document.createElement("a");
        downloadLink.download = filename;
        downloadLink.href = window.URL.createObjectURL(csvFile);
        downloadLink.style.display = "none";
        document.body.appendChild(downloadLink);
        downloadLink.click();
    }

    function exportTableToCSV(filename) {
        var csv = [];
        var rows = document.querySelectorAll("table tr");

        for (var i = 0; i < rows.length; i++) {
            var row = [], cols = rows[i].querySelectorAll("td, th");

            for (var j = 0; j < cols.length; j++)
                row.push(cols[j].innerText);

            csv.push(row.join(","));
        }

        downloadCSV(csv.join("\n"), filename);
    }
</script>

<c:if test="${environmentInfo.srcStartedOnly == 'true'
            || environmentInfo.srcRemoveStore == 'true'
            || environmentInfo.srcAddSystemModules == 'true'
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
        <c:if test="${environmentInfo.srcAddSystemModules == 'true'}">
            <br><span> <fmt:message
                key="lbl.srcAddSystemModules"></fmt:message></span>
        </c:if>
    </div>
</c:if>

<div class="box-1">

    <h1>Modules Report</h1>

    <form:form modelAttribute="environmentInfo" class="form-horizontal" method="post">

        <button id="previous" class="btn btn-primary" type="submit" name="_eventId_previous">
            Back
        </button>
        <button id="export" class="btn btn-primary" type="submit" name="_eventId_export"
                onclick="exportTableToCSV('members.csv')">Export Report to CSV
        </button>
    </form:form>

    <c:set var="redirectUrl" value="${renderContext.mainResource.node.path}.html" scope="session"/>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>org.jahia.modules</th>
            <th>jmix:cmContentTreeDisplayable</th>
            <th>serverSettings</th>
            <th>siteSettings</th>
            <th>contributeMode</th>
            <th>DateFormat</th>
            <th>Spring</th>
            <th>Actions</th>
        </tr>
        </thead>
        <c:forEach items="${migrationReport}" var="module">
            <tr>
                <td>${module.moduleName}/${module.moduleVersion}</td>
                <td>${module.jahiaGroup}</td>
                <td>${module.nodeTypes}</td>
                <td>${module.serverSettings}</td>
                <td>${module.siteSettings}</td>
                <td>${module.contributeMode}</td>
                <td>${module.nodeTypesDate}</td>
                <td>${module.hasSpringBean}</td>
                <td>${module.customActions}</td>
            </tr>
        </c:forEach>
    </table>

</div>


<div class="box-1">
    <h2>Functional Administrators</h2>
    <fmt:message key="link.functionalAdmin" var="linkFunctionalAdmin"/>
    <p><a href="${linkFunctionalAdmin}">Migration Guide</a></p>
</div>

<div class="box-1">
    <h2>System Administrators</h2>
    <fmt:message key="link.systemAdmin" var="linkSystemAdmin"/>
    <p><a href="${linkSystemAdmin}">Migration Guide</a></p>
</div>

<div class="box-1">
    <h2>Developers</h2>
    <fmt:message key="link.developers" var="linkDevelopers"/>
    <p><a href="${linkDevelopers}">Migration Guide</a></p>
</div>

<div class="box-1">
    <h2>Known Issues</h2>
    <fmt:message key="link.knownIssues" var="linkKnownIssues"/>
    <p><a href="${linkKnownIssues}">Documentation</a></p>
</div>
