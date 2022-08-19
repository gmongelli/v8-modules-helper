package org.jahia.modules.v8moduleshelper;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ResultMessage implements Serializable {

    String moduleName;
    String moduleVersion;
    Map<String, String> data;

    private static final long serialVersionUID = -6552128415414065542L;

    public ResultMessage(String moduleName, String moduleVersion) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        data = new LinkedHashMap<>();
    }

    public ResultMessage trackData(String label, String value) {
        data.put(label, value);
        return this;
    }

    public ResultMessage trackData(String label, boolean value) {
        return trackData(label, String.valueOf(value));
    }

    public ResultMessage trackData(String label, Collection<String> value) {
        return trackData(label, StringUtils.join(value, ";"));
    }

    public Set<String> getDataKeys() {
        return new LinkedHashSet<>(data.keySet());
    }

    public String getData(String key) {
        return data.get(key);
    }

    public Collection<String> getAllData() {
        return new ArrayList<>(data.values());
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
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
