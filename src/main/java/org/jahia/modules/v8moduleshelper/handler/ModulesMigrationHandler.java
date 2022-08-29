package org.jahia.modules.v8moduleshelper.handler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jahia.api.Constants;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.v8moduleshelper.Report;
import org.jahia.modules.v8moduleshelper.model.EnvironmentInfo;
import org.jahia.modules.v8moduleshelper.ModuleReport;
import org.jahia.osgi.BundleResource;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRFileNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.ExtendedPropertyDefinition;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.notification.HttpClientService;
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.context.support.AbstractApplicationContext;
import org.jahia.bin.Action;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import static api.Constants.TITLE_BR;

/**
 *
 * Class responsible to run the report and export results to webflow
 */
public class ModulesMigrationHandler {

    private static final String JAHIA_STORE_URL =
            "https://store.jahia.com/en/sites/private-app-store/contents/modules-repository.moduleList.json";

    private static final String SITE_SELECT =
            "SELECT * FROM [jnt:template] WHERE [j:view] = 'siteSettings' AND";
    private static final String SERVER_SELECT =
            "SELECT * FROM [jnt:template] WHERE [j:view] = 'serverSettings' AND";
    private static final String CONTRIBUTE_MODE_SELECT =
            "SELECT * FROM [jmix:contributeMode] WHERE";
    private static final String CONTENT_TEMPLATES_SELECT = "select * from [jnt:contentTemplate] where ";

    private static final Logger logger = LoggerFactory.getLogger(ModulesMigrationHandler.class);
    private static final String STORE_MODULES_LIST = "storeModulesList";
    private static final String STORE_MODULES_LIST_AVAILABLE = "storeModulesListAvailable";
    private static final String STORE_MODULES_LIST_ERROR_MESSAGE = "Cannot load information from Jahia Store. Please consider including Jahia modules in the report";
    private static final String JCR_FOLDER = "v8-migration";
    private static final String SYSTEMSITE_FILES_PATH = "/sites/systemsite/files";
    private static final String MODULES_LIST_FILENAME = "modules-repository.moduleList.json";
    private static final String MODULES_LIST_FILE_PATH = SYSTEMSITE_FILES_PATH + "/" + JCR_FOLDER + "/" + MODULES_LIST_FILENAME;
    private static final String DESC_GRP_ID_ERROR = "The group ID must be changed" + TITLE_BR + "(unless the module is developed by Jahia)";
    private static final String DESC_CM_CONTENT_TREE_DISPLAYABLE = "If you were using the jmix:cmContentTreeDisplayable mixin to display your content type in Content and Media Manager tree as a content folder, then you need to use jmix:visibleInContentTree instead";
    private static final String DESC_TYPES_WITH_CTNT_TPLT = "Add jmix:mainResource to the content types that can be displayed in full page";
    private static final String DESC_HAS_SERVER_SETTINGS = "Navigation updates in Jahia 8 change where Site Settings and Server Settings can display. Now you must explicitly declare the location of your custom Site Settings, Server Settings, and User Dashboard panels";
    private static final String DESC_HAS_SITE_SETTINGS = DESC_HAS_SERVER_SETTINGS;
    private static final String DESC_CONTRIBUTE_MODE_CONF = "The contribute mode has been removed, and related configuration has to be reviewed";
    private static final String DESC_USES_DATE_FMT = "Date format in CND files is not supported anymore";
    private static final String DESC_HAS_SPRING_BEANS = "The module declares some Spring beans, and need to be configured to have a Spring context";
    private static final String DESC_HAS_SPRING_ACTIONS = "The module registers some custom actions as Spring beans, they should be rewritten to be registered as some OSGi services";
    private static final String DESC_HAS_EMPTY_SPRING_FILES = "The module embeds some empty Spring files";
    private static final String DATE_FORMAT_OPTION_NAME = "format";

    private final Report report = new Report();
    private StringBuilder errorMessage = new StringBuilder();
    private HttpClientService httpClientService;
    private List<String> jahiaStoreModules = new ArrayList<>();
    private Map<String, Action> actionsMap;

    private static boolean removeStore = false;
    private static boolean removeJahiaGit = false;
    private static boolean onlyStarted = false;
    private static boolean addSystem = false;

    private void initClient() {

    	if (httpClientService != null ) {
    		return;
    	}
        HttpClientParams params = new HttpClientParams();
        params.setAuthenticationPreemptive(true);
        params.setCookiePolicy("ignoreCookies");

        HttpConnectionManagerParams cmParams = new HttpConnectionManagerParams();
        cmParams.setConnectionTimeout(15000);
        cmParams.setSoTimeout(60000);

        MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
        httpConnectionManager.setParams(cmParams);

        httpClientService = new HttpClientService();
        httpClientService.setHttpClient(new HttpClient(params, httpConnectionManager));
    }

    /**
     * Set the error message to be shown in UI
     *
     * @param message Error message
     */
    public void setErrorMessage(String message) {
        this.errorMessage.append("</br>" + message);
    }

    /**
     * Load modules list from Jahia store
     */
    private void loadStoreJahiaModules(ExternalContext context) {

        initClient();

        final JSONArray modulesRoot = (JSONArray) context.getGlobalSessionMap().get(STORE_MODULES_LIST);
        if (modulesRoot == null) {
            setErrorMessage(STORE_MODULES_LIST_ERROR_MESSAGE);
            return;
        }

        try {
            final JSONArray moduleList = modulesRoot.getJSONObject(0).getJSONArray("modules");
            for (int i = 0; i < moduleList.length(); i++) {
                final JSONObject moduleObject = moduleList.getJSONObject(i);
                final String moduleName = moduleObject.getString("name");
                final String groupId = moduleObject.getString("groupId");

                if (StringUtils.equalsIgnoreCase(groupId, "org.jahia.modules")) {
                    jahiaStoreModules.add(moduleName.toLowerCase());
                }
            }
        } catch (JSONException e) {
            setErrorMessage(STORE_MODULES_LIST_ERROR_MESSAGE);
            logger.error("Error while parsing the information from the Jahia Store", e);
        }
    }

    /**
     * Collects data from JCR
     *
     * @param querySelect JCR Query
     * @param moduleName Module name
     * @param moduleVersion Module Version
     * @return Modules list
     */
    private List<String> getModuleResourcesByQuery(String querySelect, String moduleName, String moduleVersion) {
        return getModuleResourcesByQuery(querySelect, moduleName, moduleVersion, JCRNodeWrapper::getPath);
    }

    private List<String> getModuleResourcesByQuery(String querySelect, String moduleName, String moduleVersion, Function<JCRNodeWrapper,String> transformer) {
        final List<String> modulesPathList = new ArrayList<String>();

        final String modulePath = String.format(" ISDESCENDANTNODE ('/modules/%s/%s/templates/')",
                moduleName, moduleVersion.replace(".SNAPSHOT", "-SNAPSHOT"));

        try {
            final JCRSessionWrapper session = JCRSessionFactory.getInstance()
                    .getCurrentSystemSession(Constants.EDIT_WORKSPACE, null, null);

            final NodeIterator iterator = session.getWorkspace().getQueryManager()
                    .createQuery(querySelect + modulePath, Query.JCR_SQL2).execute().getNodes();
            while (iterator.hasNext()) {
                final JCRNodeWrapper node = (JCRNodeWrapper) iterator.nextNode();
                modulesPathList.add(transformer.apply(node));
            }

        } catch (RepositoryException e) {
            logger.error(String.format("Cannot get JCR information from module %s/%s",
                    moduleName, moduleVersion), e);
        }

        return modulesPathList;
    }

    private List<String> getNodeTypesWithMixin(String module, String mixin) {
        return StreamSupport.stream(NodeTypeRegistry.getInstance().getNodeTypes(module).spliterator(), false)
                .filter(nt -> nt.isNodeType(mixin))
                .map(ExtendedNodeType::getName)
                .filter(n -> !Objects.equals(n, mixin))
                .collect(Collectors.toList());
    }

    /**
     * Return a list of nodeTypes having a property with date format
     *
     * @param nodeTypeIterator Node type iterator for a specific module
     * @return Modules list
     */
    private Set<String> getNodeTypesDateFormat(NodeTypeRegistry.JahiaNodeTypeIterator nodeTypeIterator) {
        final Set<String> properties = new TreeSet<>();

        for (ExtendedNodeType moduleNodeType : nodeTypeIterator) {
            final String nodeTypeName = moduleNodeType.getName();
            final ExtendedPropertyDefinition[] allPropertyDefinitions = moduleNodeType.getPropertyDefinitions();

            for (ExtendedPropertyDefinition propertyDefinition : allPropertyDefinitions) {
                final String formatValue = propertyDefinition.getSelectorOptions().get(DATE_FORMAT_OPTION_NAME);

                if (formatValue != null) {
                    properties.add(String.format("%s - %s", nodeTypeName, propertyDefinition.getName()));
                }
            }
        }

        return properties;
    }

    /**
     * Collects actions from Package
     *
     * @param aPackage
     * @return Modules list
     */
    private List<String> getModuleActions(JahiaTemplatesPackage aPackage) {
        List<String> actionsList = new ArrayList<String>();

        AbstractApplicationContext context = aPackage.getContext();

        if (context != null) {

            String[] beanNames = context.getBeanDefinitionNames();

            for (String beanName : beanNames) {
                for (String actionName : this.actionsMap.keySet()) {
                    if (beanName.toLowerCase().contains(actionName.toLowerCase()) && actionsList.contains(actionName) == false) {
                        actionsList.add(actionName);
                    }
                }
            }
        }

        return actionsList;
    }

    private boolean isSpringContext(JahiaTemplatesPackage module) {
        final AbstractApplicationContext springContext = module.getContext();
        if (springContext == null) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("No Spring context for %s", module));
            return false;
        }

        if (springContext.getDisplayName() != null) {
            String[] beanDefinitionNames = springContext.getBeanDefinitionNames();

            for (String beanDefinitionName : beanDefinitionNames) {
                if (beanDefinitionName.contains("springframework")) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<String> getEmptySpringFiles(JahiaTemplatesPackage module) {
        final Bundle bundle = module.getBundle();
        final Enumeration<URL> entries = bundle.findEntries("META-INF/spring", "*.xml", false);
        if (entries == null || !entries.hasMoreElements()) return Collections.emptyList();
        return Collections.list(entries).stream()
                .map(url -> new BundleResource(url, bundle))
                .filter(bundleResource -> {
                    InputStream inputStream = null;
                    try {
                        inputStream = bundleResource.getInputStream();
                        final SAXBuilder saxBuilder = new SAXBuilder();
                        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                        final Document document = saxBuilder.build(inputStream);
                        if (!document.hasRootElement()) return true;
                        final Element rootElement = document.getRootElement();
                        if (!StringUtils.equalsIgnoreCase(rootElement.getName(), "beans")) return true;
                        return rootElement.getChildren().size() == 0;
                    } catch (IOException | JDOMException e) {
                        logger.error("", e);
                        return false;
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                    }
                })
                .map(bundleResource -> {
                    try {
                        return bundleResource.getURL().getPath();
                    } catch (IOException e) {
                        logger.error("", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Fill report for package that matches filter
     *
     * @param aPackage Module to be analyzed
     */
    private void fillReport(JahiaTemplatesPackage aPackage) {
        final String moduleName = aPackage.getId();
        final String moduleVersion = aPackage.getVersion().toString();
        final String moduleGroupId = aPackage.getGroupId();
        final String modulescmURI = aPackage.getScmURI();

        if (jahiaStoreModules.contains(moduleName.toLowerCase())) {
            return;
        }

        if (removeJahiaGit && modulescmURI.toLowerCase().contains("scm:git:git@github.com:jahia/")) {
            return;
        }

        final boolean usesJahiaGroupID = moduleGroupId.equalsIgnoreCase("org.jahia.modules");
        final boolean hasSpringBean = isSpringContext(aPackage);
        final List<String> nodeTypesWithcmContentTreeDisplayable = getNodeTypesWithMixin(moduleName, "jmix:cmContentTreeDisplayable");
        final Set<String> nodeTypesWithDate = getNodeTypesDateFormat(NodeTypeRegistry.getInstance().getNodeTypes(moduleName));
        final List<String> siteSettingsPaths = getModuleResourcesByQuery(SITE_SELECT, moduleName, moduleVersion);
        final List<String> serverSettingsPaths = getModuleResourcesByQuery(SERVER_SELECT, moduleName, moduleVersion);
        final List<String> contributeModePaths = getModuleResourcesByQuery(CONTRIBUTE_MODE_SELECT, moduleName, moduleVersion);
        final List<String> contentTemplates = getModuleResourcesByQuery(CONTENT_TEMPLATES_SELECT, moduleName, moduleVersion,
                node -> String.format("%s : {%s}", node.getName(), node.getPropertyAsString("j:applyOn")));
        final List<String> customActions = getModuleActions(aPackage);
        final List<String> emptySpringFiles = getEmptySpringFiles(aPackage);

        final ModuleReport moduleReport = new ModuleReport(moduleName, moduleVersion)
                .trackData("org.jahia.modules", usesJahiaGroupID, DESC_GRP_ID_ERROR)
                .trackData("jmix:cmContentTreeDisplayable", nodeTypesWithcmContentTreeDisplayable, DESC_CM_CONTENT_TREE_DISPLAYABLE)
                .trackData("Types with content template", contentTemplates, DESC_TYPES_WITH_CTNT_TPLT)
                .trackData("serverSettings", serverSettingsPaths, DESC_HAS_SERVER_SETTINGS)
                .trackData("siteSettings", siteSettingsPaths, DESC_HAS_SITE_SETTINGS)
                .trackData("contributeMode", contributeModePaths, DESC_CONTRIBUTE_MODE_CONF)
                .trackData("DateFormat", nodeTypesWithDate, DESC_USES_DATE_FMT)
                .trackData("Spring", hasSpringBean, DESC_HAS_SPRING_BEANS)
                .trackData("Spring Actions", customActions, DESC_HAS_SPRING_ACTIONS)
                .trackData("Empty Spring Files", emptySpringFiles, DESC_HAS_EMPTY_SPRING_FILES);

        logger.info(moduleReport.toString());
        this.report.add(moduleReport);

    }


    /**
     *
     * Execute the migration
     *
     * @param environmentInfo Object containing environment information read from frontend
     * @param context         Page context
     * @return true if OK; otherwise false
     */
    public Boolean execute(final EnvironmentInfo environmentInfo,
                           RequestContext context) throws RepositoryException {

        logger.info("Starting modules report");

        this.removeStore = environmentInfo.isSrcRemoveStore();
        this.removeJahiaGit = environmentInfo.isSrcRemoveJahia();
        this.onlyStarted = environmentInfo.isSrcStartedOnly();
        this.addSystem = environmentInfo.isSrcAddSystemModules();

        this.actionsMap = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getActions();
        this.actionsMap.remove("default");
        this.actionsMap.remove("webflow");

        report.clear();
        jahiaStoreModules.clear();

        if (removeStore == true) {
            loadStoreJahiaModules(context.getExternalContext());
        }

        JahiaTemplateManagerService templateManager = ServicesRegistry.getInstance().getJahiaTemplateManagerService();

        List<JahiaTemplatesPackage> packagesList = (onlyStarted) ? templateManager.getAvailableTemplatePackages() :
                new ArrayList<JahiaTemplatesPackage>(templateManager.getRegisteredBundles().values());

        for (JahiaTemplatesPackage module : packagesList) {
            if (module.getModuleType().equals("module")
                    || module.getModuleType().equals("templatesSet")
                    || (module.getModuleType().equals("system") && addSystem)) {
                fillReport(module);
            }
        }

        if (this.errorMessage.length() > 0) {
            context.getMessageContext().addMessage(new MessageBuilder().error()
                    .defaultText("An error encountered: " + this.errorMessage).build());
            return false;
        } else {
            context.getFlowScope().put("migrationReport", this.report);
        }

        logger.info("Finishing modules report");

        return true;
    }

    // TODO remove & clean "connectionToStore"
    public void init(ExternalContext context) {
        final JSONArray modulesList = downloadModulesList();
        if (modulesList != null) {
            context.getGlobalSessionMap().put("connectionToStore", Boolean.TRUE);
            context.getGlobalSessionMap().put(STORE_MODULES_LIST_AVAILABLE, Boolean.TRUE);
            context.getGlobalSessionMap().put(STORE_MODULES_LIST, modulesList);
        } else {
            context.getGlobalSessionMap().put("connectionToStore", Boolean.FALSE);
            context.getGlobalSessionMap().put(STORE_MODULES_LIST_AVAILABLE, Boolean.FALSE);
            context.getGlobalSessionMap().remove(STORE_MODULES_LIST);
        }
    }

    private JSONArray downloadModulesList () {
        logger.debug("Downloading the modules list from the store");
        initClient();
        final Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        final String json = httpClientService.executeGet(JAHIA_STORE_URL.concat(System.getProperties().contains("v8migrationhelpertest") ? "dummmy" : ""), headers);
        if (StringUtils.isNotBlank(json)) {
            try {
                final JSONArray modulesList = new JSONArray(json);
                writeModulesListInTheJCR(modulesList);
                return modulesList;
            } catch (JSONException e) {
                logger.error("", e);
            }
        }

        logger.info("Failed to download the modules list from the store, loading the list from the local data");
        final JSONArray modulesList = loadModulesListFromLocal();
        if (modulesList != null) {
            return modulesList;
        } else {
            logger.error("Failed to load the modules list, from the store or from the local data");
            return null;
        }
    }

    private JSONArray loadModulesListFromLocal() {
        final String json;
        try {
            json = JCRTemplate.getInstance().doExecuteWithSystemSessionAsUser(null, Constants.EDIT_WORKSPACE, null, new JCRCallback<String>() {
                @Override
                public String doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    if (!session.nodeExists(MODULES_LIST_FILE_PATH)) return null;
                    final JCRFileNode file = (JCRFileNode) session.getNode(MODULES_LIST_FILE_PATH);
                    return file.getFileContent().getText();
                }
            });
        } catch (RepositoryException e) {
            logger.error("", e);
            return null;
        }

        final String msg = "Failed to load the modules list from the local data";
        if (StringUtils.isBlank(json)) {
            logger.warn(msg);
        } else {
            try {
                return new JSONArray(json);
            } catch (JSONException e) {
                    logger.error(msg, e);
            }
        }

        return null;
    }

    private void writeModulesListInTheJCR(JSONArray modulesList) {
        try {
            JCRTemplate.getInstance().doExecuteWithSystemSessionAsUser(null, Constants.EDIT_WORKSPACE, null, new JCRCallback<Void>() {
                @Override
                public Void doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    final JCRNodeWrapper filesFolder = session.getNode(SYSTEMSITE_FILES_PATH);
                    final JCRNodeWrapper outputDir = filesFolder.hasNode(JCR_FOLDER) ?
                            filesFolder.getNode(JCR_FOLDER) :
                            filesFolder.addNode(JCR_FOLDER, Constants.JAHIANT_FOLDER);
                    if (!outputDir.isNodeType(Constants.JAHIANT_FOLDER)) {
                        logger.error(String.format("Impossible to write the folder %s of type %s", outputDir.getPath(), outputDir.getPrimaryNodeTypeName()));
                        return null;
                    }

                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try {
                        IOUtils.write(modulesList.toString(), out, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        logger.error("", e);
                        return null;
                    }
                    final byte[] bytes = out.toByteArray();

                    final JCRNodeWrapper reportNode = outputDir.uploadFile(MODULES_LIST_FILENAME, new ByteArrayInputStream(bytes), "application/json");
                    session.save();
                    final String reportPath = reportNode.getPath();
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Written the modules list in %s", reportPath));
                    }
                    return null;
                }
            });
        } catch (RepositoryException e) {
            logger.error("", e);
        }
    }

}
