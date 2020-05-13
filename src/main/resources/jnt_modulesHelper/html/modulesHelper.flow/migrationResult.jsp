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
            <th>Version</th>
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
                <td>${module.moduleName}</td>
                <td>${module.moduleVersion}</td>
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
    <h2>Maven - Parent version</h2>
    <p>Modules should change their parent version to Jahia 8.0 and recompile them using JDK8 as target version.
        Example:<br><br>
        <strong>&lt;parent&gt;</strong>
        <br>&nbsp;&nbsp;<strong>&lt;groupId&gt;your.group.id&lt;/groupId&gt;</strong>
        <br>&nbsp;&nbsp;<strong>&lt;artifactId&gt;your.artifact.id&lt;/artifactId&gt;</strong>
        <br>&nbsp;&nbsp;<strong>&lt;version&gt;8.0.0.0-SNAPSHOT&lt;/version&gt;</strong>
        <br><strong>&lt;/parent&gt;</strong>
    </p>
</div>

<div class="box-1">
    <h2>Maven - Group ID</h2>
    <p>The groupID <strong>org.jahia.modules</strong> is now restricted to Jahia proprietary modules. If you are using it please
        replace by a distinct group ID.</p>
</div>

<div class="box-1">
    <h2>Spring</h2>
    <p>Jahia is in process of removing Spring from our own modules. We are exposing Spring 3.2 for compatibility and
        Spring 5.2 through Karaf feature, but this version is not used by Jahia itself.<br>
        If your modules still needs to use Spring 3.2, which is provided by Jahia core, then please enable the Bundle
        Activator in your pom.xml:<br><br>
        <strong>&lt;require-capability&gt;osgi.extender;filter:=&quot;(osgi.extender=org.jahia.bundles.blueprint.extender.config)&quot;&lt;/require-capability&gt;</strong>
    </p>
</div>

<div class="box-1">
    <h2>Site Settings and Server Settings</h2>
    <p>Jahia 8 changed the navigation for these two options so now you must explicitly declare the new locations:
        <br><br></p>
    <li><strong>Administration -> Server accordion</strong></li>
    <p>The location for your Server Settings. The target must start with &quot;administration-server&quot;.</p>
    <li><strong>Administration -> Site accordion</strong></li>
    <p>The location for your Site Settings that relate to administration functionality. Users require the Site
        administration access permission to see these panels. The target must start with &quot;administration-
        sites&quot;</p>
    <li><strong>jContent -> Additional accordion</strong></li>
    <p>The location for your Site Settings that fill functional purposes, for example, that are used by editors. The
        target must start with &quot;jcontent&quot;.</p>
    <li><strong>Dashboard -> My workspace</strong></li>
    <p>The location for the User Dashboard panels, which are settings panels that belong to a user. The target must
        start with &quot;dashboard&quot;.</p>
</div>

<div class="box-1">
    <h2>Mixin jmix:cmContentTreeDisplayable</h2>
    <p>If you were using the jmix:cmContentTreeDisplayable mixin to display your content type in the Content and Media
        Manager tree as a content folder, then you need to now use <strong>jmix:visibleInContentTree</strong>.<br>
        This new mixin enables you to display such content in content trees without adding a dependency to jContent
        in your module.
    </p>
</div>

<div class="box-1">
    <h2>Redirections to back office interfaces</h2>
    <p>Our new Jahia interface uses different URL patterns. We have set the following redirections for compatibility reasons:<br>
        <li>/cms/edit/ <strong>-></strong> /jahia/page-composer/</li>
        <li>/cms/contribute/ <strong>-></strong> /jahia/page-composer/</li>
        <li><strong>...</strong></li>
        <br>If you are using these URLs in your custom modules it is advised to update these URLs though this change is <strong>not mandatory.</strong>
    </p>
</div>

<div class="box-1">
    <h2>Areas do not add &lt;div class="clear"&gt;&lt;/div&gt; anymore</h2>
    <p> Jahia 7 adds <strong>&lt;div class="clear"&gt;&lt;/div&gt;</strong> in areas and lists to handle floats in generated HTML.
        If you want to preserve this behavior you must use <strong>&lt;template:param name="omitFormatting" value="false"/&gt;</strong>
    </p>
</div>

<div class="box-1">
    <h2>Contribute mode settings</h2>
    <p> If you were using “Contribute mode settings” in your templates, then these ones will not be applied in Page
        Composer and jContent. You need to define these restrictions in the <strong>list restriction</strong> section when using the studio.
    </p>
</div>

<div class="box-1">
    <h2>Content Editor</h2>
    <li><strong>Template mixin</strong></li>
        <p>Template mixins (the ability to display a field depending on the value of a mixin or another field) are not supported
    in Content Editor 2.0. However, they will be supported in version 2.1 which will be released soon after v8. In the meantime,
            it is possible to access such fields by using the legacy UI, available under the Advanced options tab in Content Editor.</p>
    <li><strong>Custom Java validator</strong></li>
    <p>Mandatory fields and regular expression validation are supported by Content Editor. However, custom Java validators are not.
    If you implemented such custom validators, please reach out to Jahia support.</p>
    <li><strong>Cache mixin</strong></li>
    <p>Cache mixin (originally in the Options tab) is now hidden by default to make the user experience simpler for editors.
    If you need to access the mixin, the legacy Options tab is available under Advanced options tab in Content Editor.</p>
    <li><strong>Skinnable mixin</strong></li>
    <p>Skins mixin (originally in the Layout tab) is now hidden by default to make the user experience simpler for editors.</p>
    <li><strong>Date format in the form</strong></li>
    <p>Date format in CND files is not supported anymore. The date format in the UI depends exclusively on the language of
    the user in Jahia. If the editor uses Jahia in English, it will be mm/dd/yyyy, if the editor uses Jahia in French it
    will be dd/mm/yyyyy.</p>
    <li><strong>Images in choicelist</strong></li>
    <p>Content Editor cannot display images in <strong>choicelists</strong> (dropdowns that display in Content Editor).  If this is a
    significant issue for your organization, please reach out to Jahia support.</p>
    </p>
</div>