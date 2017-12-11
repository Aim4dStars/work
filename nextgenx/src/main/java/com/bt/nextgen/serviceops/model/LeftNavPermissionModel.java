package com.bt.nextgen.serviceops.model;

/**
 * Created by l069679 on 7/06/2017.
 */
public class LeftNavPermissionModel {
    private boolean docLibrary;
    private boolean gcmHome;

    public boolean isDocLibrary() {
        return docLibrary;
    }

    public void setDocLibrary(boolean docLibrary) {
        this.docLibrary = docLibrary;
    }

    public boolean isGcmHome() {
        return gcmHome;
    }

    public void setGcmHome(boolean gcmHome) {
        this.gcmHome = gcmHome;
    }
}
