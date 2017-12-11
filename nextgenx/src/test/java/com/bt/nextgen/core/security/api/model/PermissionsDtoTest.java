package com.bt.nextgen.core.security.api.model;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the various methods and JSON structure of the PermissionsDto.
 * @author M013938
 */
public class PermissionsDtoTest
{

    private PermissionsDto permissions;

    private ObjectMapper mapper;

    @Before
    public void initPermissionsAndMapper()
    {
        mapper = new ObjectMapper();
        permissions = new PermissionsDto(false);
        permissions.setPermission("cash", true);
        permissions.setPermission("fees", true);
        permissions.setPermission("fees.schedule", false);
        permissions.setPermission("fees.invoice.submit", false);
        permissions.setPermission("fees.invoice.delete", false);
        permissions.setPermissionMessage("account.summary.bar.view", "This account has been blocked.  For more information, contact Panorama Support on 1300 784 207");
    }

    @Test(expected = IllegalArgumentException.class)
    public void permissionNodesCannotBeNamedDefault() throws Exception
    {
        permissions.setPermission("cash.default.advance", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void permissionNodesCannotHaveEmptyNames() throws Exception
    {
        permissions.setPermission("cash..advance", true);
    }

    @Test
    public void permissionDtoIsSerializable() throws Exception
    {
        assertEquals(writeAndRead(permissions), permissions);
    }

    @Test
    public void permissionsAreEqualRegardlessOfSetOrder()
    {
        PermissionsDto duplicate = new PermissionsDto(false);
        duplicate.setPermission("fees.invoice.submit", false);
        duplicate.setPermission("fees.invoice.delete", false);
        duplicate.setPermission("fees.schedule", false);
        duplicate.setPermissionMessage("account.summary.bar.view", "This account has been blocked.  For more information, contact Panorama Support on 1300 784 207");
        duplicate.setPermission("fees", true);
        duplicate.setPermission("cash", true);
        assertEquals(duplicate, permissions);
        assertEquals(duplicate.hashCode(), permissions.hashCode());
    }

    @Test
    public void defaultConstructorCreatesEmptyPermissionsObject() throws Exception
    {
        permissions = new PermissionsDto();
        String expected = "{'permission':{},'type':'Permissions'}";
        expected = expected.replace('\'', '"');
        assertEquals(expected, mapper.writeValueAsString(permissions));
    }

    @Test
    public void hasPermission()
    {
        assertPermitted("cash.transact.bpay", "fees.invoice");
        assertForbidden(null, "admin.users.add", "fees.invoice.delete", "fees.invoice.submit");
        assertMessage("account.summary.bar.view", "This account has been blocked.  For more information, contact Panorama Support on 1300 784 207");
    }

    @Test
    public void hasPermissionWithFallback()
    {
        PermissionsDto override = new PermissionsDto();
        override.setPermission("account.view", true);
        override.setPermission("account.link", true);
        assertTrue(override.hasPermission("account.view.owners", permissions));
        assertFalse(override.hasPermission("account.transact", permissions));
        assertTrue(override.hasPermission("cash.transfer", permissions));
    }

    @Test
    public void prunePermission()
    {
        assertPermitted("fees.invoice");
        assertForbidden("fees.invoice.submit", "fees.schedule");
        permissions.prunePermission("fees.invoice");
        assertPermitted("fees.invoice", "fees.invoice.submit");
        assertForbidden("fees.schedule");
    }

    @Test
    public void toJson() throws Exception
    {
        assertTrue(mapper.canSerialize(PermissionsDto.class));
        String expected = "{'permission':{'default':false,'account':{'summary':{'bar':{'view':'This account has been blocked.  For more information, contact Panorama Support on 1300 784 207'}}},'cash':true,'fees':{'default':true,'invoice':{'delete':false,'submit':false},'schedule':false}},'type':'Permissions'}";
        expected = expected.replace('\'', '"');
        assertEquals(expected, mapper.writeValueAsString(permissions));
    }

    private void assertPermitted(String... paths)
    {
        for (String path : paths)
        {
            assertTrue("Expecting to have permission: " + path, permissions.hasPermission(path));
        }
    }

    private void assertMessage(String path, String message)
    {
        assertEquals(permissions.hasPermissionMessage(path), message);
    }

    private void assertForbidden(String... paths)
    {
        for (String path : paths)
        {
            assertFalse("Expecting to not have permission: " + path, permissions.hasPermission(path));
        }
    }

    private static UserProfile userInfo(final JobRole role, List<FunctionalRole> functions)
    {
        UserInformationImpl info = new UserInformationImpl();
        info.setFunctionalRoles(functions);

        JobProfile jobProfile = new JobProfile()
        {

            @Override public JobRole getJobRole()
            {
                return role;
            }

            @Override public String getPersonJobId()
            {
                return null;
            }

            @Override public JobKey getJob()
            {
                return null;
            }

            @Override public String getProfileId()
            {
                return null;
            }

            @Override public DateTime getCloseDate(){
                return null;
            }

            @Override
            public UserExperience getUserExperience() {
                return null;
            }
        };

        return new UserProfileAdapterImpl(info, jobProfile);
    }

    private static UserProfile userInfo(JobRole role, FunctionalRole... functions)
    {
        return userInfo(role, asList(functions));
    }

    private static UserProfile userInfo(FunctionalRole... functions)
    {
        return userInfo(JobRole.OTHER, functions);
    }

    @SuppressWarnings("unchecked")
    public static <T> T writeAndRead(T instance) throws IOException, ClassNotFoundException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutput oo = new ObjectOutputStream(baos))
        {
            oo.writeObject(instance);
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (ObjectInput oi = new ObjectInputStream(bais))
        {
            return (T)oi.readObject();
        }
    }
}
