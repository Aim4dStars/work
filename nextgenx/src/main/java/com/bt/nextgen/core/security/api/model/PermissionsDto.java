package com.bt.nextgen.core.security.api.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.io.Serializable;

import static org.springframework.util.ObjectUtils.nullSafeEquals;
import static org.springframework.util.ObjectUtils.nullSafeHashCode;

/**
 * Set of hierarchical-structured permission flags used by the UI for disabling
 * and hiding various screen components.
 * @author M013938
 */
public class PermissionsDto extends BaseDto implements JsonSerializable, Serializable, KeyedDto<PermissionAccountKey>
{
    // =======================================================================
    // Class constants
    // =======================================================================

    /**
     * The root permission attribute name.
     */
    public static final String ROOT_NAME = "permission";

    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    //    private static final String blockedMessage = "This account currently has a restriction in place. For more information contact Panorama Support on xxx xxx xxx.";

    // =======================================================================
    // Instance members
    // =======================================================================

    /**
     * The root permission node, which always has the name of
     * {@link #ROOT_NAME permission}.
     */
    private PermissionNode root;

    // =======================================================================
    // Constructor
    // =======================================================================

    /**
     * Construct permissions DTO with specific value for default (root) flag.
     *
     * @param rootFlag root access flag, usually false.
     */
    public PermissionsDto(boolean rootFlag)
    {
        this.root = new PermissionNode(ROOT_NAME, rootFlag);
    }

    /**
     * Construct permissions DTO with null default access.
     * Used for account-specific override permissions that don't want a default
     * root flag.
     */
    public PermissionsDto()
    {
        this.root = new PermissionNode(ROOT_NAME);
    }

    // =======================================================================
    // Flag inquiry and manipulator methods
    // =======================================================================

    /**
     * Set the flag for a specific permission path. Will create the necessary
     * child permission nodes to satisfy the provided path.
     *
     * @param path full path of the flag.
     * @param flag the boolean value to set.
     */
    public void setPermission(String path, boolean flag)
    {
        root.setPermission(path, flag);
    }

    /**
     * Set the message for a specific permission path. Will create the necessary
     * child permission nodes to satisfy the provided path.
     *
     * @param path    full path of the flag.
     * @param message the string value to set, used for sending block code messages for an account.
     */
    public void setPermissionMessage(String path, String message)
    {
        root.setPermissionMessage(path, message);
    }

    /**
     * Completely remove the permission node and all child nodes at the
     * specified path.
     *
     * @param path the path to be pruned.
     */
    public void prunePermission(String path)
    {
        root.prunePermission(path);
    }

    /**
     * Determine the permission flag for the provided path. If this path
     * has not been explicitly set with a flag, will default to nearest-set
     * flag upwards in the path.
     *
     * @param path the path to enquire.
     * @return the flag for this path, or its closest parent.
     */
    public boolean hasPermission(String path)
    {
        return root.hasPermission(path);
    }

    /**
     * Determine the permission message for the provided path. If this path
     * has not been explicitly set with a message, will default to null.
     *
     * @param path the path to enquire.
     * @return the message for this path, or null.
     */
    public String hasPermissionMessage(String path)
    {
        return root.hasPermissionMessage(path);
    }

    /**
     * Determine the permission flag for the provided path. If this path
     * has not been explicitly set with a flag, will default to nearest-set
     * flag upwards in the path.
     *
     * @param path     the path to enquire.
     * @param fallback permissions to use as default in the event of a flag
     *                 being completely undefined within this structure.
     * @return the flag for this path, or its closest parent.
     */
    public boolean hasPermission(String path, PermissionsDto fallback)
    {
        return root.hasPermission(path, fallback.hasPermission(path));
    }

    /**
     * Determine the permission flag for the provided path. If this path
     * has not been explicitly set with a flag, will default to nearest-set
     * flag upwards in the path.
     *
     * @param path     the path to enquire.
     * @param fallback permissions to use as default in the event of a flag
     *                 being completely undefined within this structure.
     * @return the flag for this path, or its closest parent.
     */
    public String hasPermissionMessage(String path, PermissionsDto fallback)
    {
        return root.hasPermissionMessage(path, fallback.hasPermissionMessage(path));
    }

    // =======================================================================
    // Implement JsonSerializableWithType methods
    // =======================================================================

    @Override
    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException
    {
        jgen.writeStartObject();
        root.serialize(jgen, provider);
        jgen.writeStringField("type", getType());
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException
    {
        serialize(jgen, provider);
    }

    // =======================================================================
    // Override Object methods
    // =======================================================================

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof PermissionsDto)
        {
            PermissionsDto that = (PermissionsDto)o;
            return nullSafeEquals(this.root, that.root);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return nullSafeHashCode(root);
    }

    @Override
    public PermissionAccountKey getKey()
    {
        return null;
    }

}
