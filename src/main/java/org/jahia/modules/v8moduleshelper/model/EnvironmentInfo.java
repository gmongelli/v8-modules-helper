package org.jahia.modules.v8moduleshelper.model;

import java.io.Serializable;

/**    
 * Serialized class with information about jahia environments
 */
public class EnvironmentInfo implements Serializable {
    private static final long serialVersionUID = 29383204L;

    private boolean srcRemoveStore = true;
    private boolean srcStartedOnly = true;
    private boolean srcRemoveJahia = true;
    private boolean srcAddSystemModules = true;

    public boolean isSrcRemoveStore() {
        return srcRemoveStore;
    }

    public void setSrcRemoveStore(boolean srcRemoveStore) {
        this.srcRemoveStore = srcRemoveStore;
    }

    public boolean isSrcStartedOnly() {
        return srcStartedOnly;
    }

    public void setSrcStartedOnly(boolean srcStartedOnly) {
        this.srcStartedOnly = srcStartedOnly;
    }

    public boolean isSrcRemoveJahia() {
        return srcRemoveJahia;
    }

    public void setSrcRemoveJahia(boolean srcRemoveJahia) {
        this.srcRemoveJahia = srcRemoveJahia;
    }

    public boolean isSrcAddSystemModules() { return srcAddSystemModules; }

    public void setSrcAddSystemModules(boolean srcAddSystemModules) { this.srcAddSystemModules = srcAddSystemModules; }

}
