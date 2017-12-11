package com.bt.nextgen.api.version.model;

public class ModuleKey {
    private String moduleName;

    public ModuleKey(String moduleName) {
        super();
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
        return result;
    }

    @Override
    // disabling warnings on automatically generated code.
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ModuleKey other = (ModuleKey) obj;
        if (moduleName == null) {
            if (other.moduleName != null)
                return false;
        } else if (!moduleName.equals(other.moduleName))
            return false;
        return true;
    }

}
