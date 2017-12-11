package com.bt.nextgen.service;

import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.ObjectFactory;

/**
 * Created with IntelliJ IDEA.
 * User: l053474
 * Date: 16/08/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class SafiObjectFactory {
    private final static ObjectFactory maintainDeviceObjectFactory = new ObjectFactory();

    private final static au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ObjectFactory deviceCommonObjectFactory = new au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ObjectFactory();

    private final static au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory identifiesObjectFactory=new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();

    private final static  au.com.westpac.gn.utility.xsd.esbheader.v3.ObjectFactory esbHeaderObjectFactory= new au.com.westpac.gn.utility.xsd.esbheader.v3.ObjectFactory();

    private final static au.com.westpac.gn.utility.xsd.esbheader.v3.ObjectFactory esbHeader=new au.com.westpac.gn.utility.xsd.esbheader.v3.ObjectFactory();

    public static au.com.westpac.gn.utility.xsd.esbheader.v3.ObjectFactory getEsbHeader() {
        return esbHeader;
    }

    public static au.com.westpac.gn.utility.xsd.esbheader.v3.ObjectFactory getEsbHeaderObjectFactory() {
        return esbHeaderObjectFactory;
    }

    public static ObjectFactory getMaintainDeviceObjectFactory() {
        return maintainDeviceObjectFactory;
    }

    public static au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ObjectFactory getDeviceCommonObjectFactory() {
        return deviceCommonObjectFactory;
    }

    public static au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory getIdentifiesObjectFactory() {
        return identifiesObjectFactory;
    }
}
