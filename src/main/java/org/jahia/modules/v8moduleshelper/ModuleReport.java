package org.jahia.modules.v8moduleshelper;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ModuleReport implements Serializable {

    String moduleName;
    String moduleVersion;
    Map<String, String> data, descriptions;

    private static final long serialVersionUID = -6552128415414065542L;

    public ModuleReport(String moduleName, String moduleVersion) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        data = new LinkedHashMap<>();
        descriptions = new HashMap<>();
    }

    public ModuleReport trackData(String label, String value, String description) {
        data.put(label, value);
        descriptions.put(label, StringUtils.defaultIfBlank(description, StringUtils.EMPTY));
        return this;
    }

    public ModuleReport trackData(String label, boolean value, String description) {
        return trackData(label, String.valueOf(value), description);
    }

    public ModuleReport trackData(String label, Collection<String> value, String description) {
        return trackData(label, StringUtils.join(value, ";"), description);
    }

    public Set<String> getDataKeys() {
        return new LinkedHashSet<>(data.keySet());
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
        }
        return sb.toString();
    }
}
