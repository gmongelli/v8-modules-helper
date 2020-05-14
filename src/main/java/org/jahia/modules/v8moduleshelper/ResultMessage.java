package org.jahia.modules.v8moduleshelper;

import java.io.Serializable;

public class ResultMessage implements Serializable {

    String moduleName;
    String moduleVersion;
    boolean jahiaGroup;
    String nodeTypesMixin;
    String serverSettings;
    String siteSettings;
    String nodeTypesDate;
    String contributeMode;
    boolean hasSpringBean;

    private static final long serialVersionUID = -6552128415414065542L;

    /**     
     * Constructor for ResultMessage
     */
    public ResultMessage(String moduleName,
                         String moduleVersion,
                         boolean jahiaGroup,
                         String nodeTypesMixin,
                         String serverSettings,
                         String siteSettings,
                         String nodeTypesDate,
                         String contributeMode,
                         boolean hasSpringBean) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.jahiaGroup = jahiaGroup;
        this.nodeTypesMixin = nodeTypesMixin;
        this.serverSettings = serverSettings;
        this.siteSettings = siteSettings;
        this.nodeTypesDate = nodeTypesDate;
        this.contributeMode = contributeMode;
        this.hasSpringBean = hasSpringBean;
    }
    public String getNodeTypesDate() { return nodeTypesDate; }

    public void setNodeTypesDate(String nodeTypesDate) { this.nodeTypesDate = nodeTypesDate; }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public String getNodeTypes() {
        return nodeTypesMixin;
    }

    public void setNodeTypes(String nodeTypes) {
        this.nodeTypesMixin = nodeTypes;
    }

    public String getServerSettings() {
        return serverSettings;
    }

    public void setServerSettings(String serverSettings) {
        this.serverSettings = serverSettings;
    }

    public String getSiteSettings() {
        return siteSettings;
    }

    public void setSiteSettings(String siteSettings) {
        this.siteSettings = siteSettings;
    }

    public String getContributeMode() { return contributeMode; }

    public void setContributeMode(String contributeMode) { this.contributeMode = contributeMode; }

    public boolean isHasSpringBean() {
        return hasSpringBean;
    }

    public void setHasSpringBean(boolean hasSpringBean) {
        this.hasSpringBean = hasSpringBean;
    }

    public boolean isJahiaGroup() { return jahiaGroup; }

    public void setJahiaGroup(boolean jahiaGroup) { this.jahiaGroup = jahiaGroup; }
}
