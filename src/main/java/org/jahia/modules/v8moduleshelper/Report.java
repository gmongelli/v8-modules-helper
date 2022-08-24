package org.jahia.modules.v8moduleshelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Report implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Report.class);

    private final Set<String> entries;
    private final List<ModuleReport> moduleReports;

    private static final long serialVersionUID = -6552128415714065542L;

    public Report() {
        entries = new LinkedHashSet<>();
        moduleReports = new ArrayList<>();
    }

    public void clear() {
        entries.clear();
        moduleReports.clear();
    }

    public void add(ModuleReport moduleReport) {
        moduleReports.add(moduleReport);
        for (String dataKey : moduleReport.getDataKeys()) {
            if (!entries.contains(dataKey)) entries.add(dataKey);
        }

    }

    public Set<String> getEntries() {
        return entries;
    }

    public List<ModuleReport> getModuleReports() {
        return moduleReports;
    }
}
