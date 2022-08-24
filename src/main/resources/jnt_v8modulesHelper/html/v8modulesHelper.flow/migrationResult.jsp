<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>

<script type="text/javascript">
    function downloadCSV(csv, filename) {
        const csvFile = new Blob([csv], {type: "text/csv"});
        const downloadLink = document.createElement("a");
        downloadLink.download = filename;
        downloadLink.href = window.URL.createObjectURL(csvFile);
        downloadLink.style.display = "none";
        document.body.appendChild(downloadLink);
        downloadLink.click();
    }

    function exportTableToCSV(filename) {
        const csv = [];
        const rows = document.querySelectorAll("table tr");

        for (let i = 0; i < rows.length; i++) {
            const row = [], cols = rows[i].querySelectorAll("td, th");

            for (let j = 0; j < cols.length; j++) {
                const span = cols[j].querySelector("span")
                const text = span === null ? cols[j].innerText : span.innerText;
                row.push("\"" + text.replace("\"", "\"\"") + "\"");
            }

            csv.push(row.join(","));
        }

        downloadCSV(csv.join("\n"), filename);
    }
</script>

<c:set var="doubleQuote">"</c:set>
<c:set var="singleQuote">'</c:set>
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
                onclick="exportTableToCSV('migration-report.csv')">Export Report to CSV
        </button>
    </form:form>

    <c:set var="redirectUrl" value="${renderContext.mainResource.node.path}.html" scope="session"/>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <%--@elvariable id="migrationReport" type="org.jahia.modules.v8moduleshelper.Report"--%>
            <c:forEach items="${migrationReport.entries}" var="label">
                <th>${label}</th>
            </c:forEach>
        </tr>
        </thead>
        <c:forEach items="${migrationReport.moduleReports}" var="module">
            <%--@elvariable id="module" type="org.jahia.modules.v8moduleshelper.ModuleReport"--%>
            <tr>
                <td>${module.moduleName}/${module.moduleVersion}</td>
                <c:forEach items="${migrationReport.entries}" var="col">
                    <td><span title="${fn:replace(module.descriptions[col], doubleQuote, singleQuote)}">${module.data[col]}</span></td>
                </c:forEach>
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

<div class="box-1">
    <h2>From 8.0.0.0 to 8.0.1.0</h2>
    <fmt:message key="link.upgrade_8000_8010" var="upgrade_8000_8010"/>
    <p><a href="${upgrade_8000_8010}">Upgrade Instructions</a></p>
</div>