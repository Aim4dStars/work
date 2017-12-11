package com.bt.nextgen.core.security.api.model;

import static java.lang.System.arraycopy;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.springframework.util.ObjectUtils.nullSafeEquals;
import static org.springframework.util.ObjectUtils.nullSafeHashCode;
import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.commons.lang3.StringUtils;

/**
 * Recursive permission node that comprises an hierarchical tree. Used as the
 * principal structure of the {@code PermissionsDto} object.
 * @author M013938
 */
final class PermissionNode implements JsonSerializable, Serializable {

	/** Serial version. */
	private static final long serialVersionUID = 1L;

	private static final String DOT = ".";

	private static final String DOT_REGEX = "\\" + DOT;

	private static final String DEFAULT = "default";

	/** Comparator for sorting sub-nodes by their name attribute. */
	private static final Comparator<PermissionNode> BY_NAME = new Comparator<PermissionNode>() {
		@Override
		public int compare(PermissionNode pn1, PermissionNode pn2) {
			return pn1.name.compareTo(pn2.name);
		}
	};

	/** The name of this specific node - must <b>NOT</b> contain any period characters. */
	private final String name;

	/** If not null, then the specific flag for this permission node. */
	private Boolean flag;

	/** List of all child permission nodes, possibly null. */
	private List<PermissionNode> children;

	/** Whether the list of children has been sorted. */
	private boolean sorted = false;

    /** If not null/empty, then set the message for this permission node. */
    private String message;

	/**
	 * Full constructor.
	 * @param name name of the node.
	 * @param flag flag value - can be {@code null}, but only if at least one
	 *   child node is passed in.
	 * @param children child nodes.
	 */
	PermissionNode(String name, Boolean flag, PermissionNode... children) {
		this.name = validate(name);
		this.flag = flag;
		if (children.length == 0) {
			this.children = null;
		} else {
			this.children = new ArrayList<>(asList(children));
			sortChildren();
		}
	}

	/**
	 * Null flag constructor - must have at least one child.
	 * @param name node name.
	 * @param children children nodes.
	 */
	PermissionNode(String name, PermissionNode... children) {
		this(name, null, children);
	}

	private static String validate(String name) {
		if (!hasText(name) || name.indexOf(DOT) != -1 || DEFAULT.equals(name)) {
			throw new IllegalArgumentException();
		}
		return name;
	}

	void setPermission(boolean flag) {
		this.flag = flag;
	}

    void setPermissionMessage(String message) {
        this.message = message;
    }

	void setPermission(String path, boolean flag) {
		if (!hasText(path)) {
			setPermission(flag);
		} else {
			setPermission(path.split(DOT_REGEX), flag);
		}
	}

    void setPermissionMessage(String path, String message) {
        setPermissionMessage(path.split(DOT_REGEX), message);
    }

	boolean hasPermission(String path, boolean fallback) {
		boolean defFlag = flag == null ? fallback : flag.booleanValue();
		if (!hasText(path)) {
			return defFlag;
		}
		return hasPermission(path.split(DOT_REGEX), defFlag);
	}

    String hasPermissionMessage(String path, String fallback) {
        String fallbackMessage = message == null ? fallback : message;
        if (!hasText(path)) {
            return fallbackMessage;
        }
        return hasPermissionMessage(path.split(DOT_REGEX), fallbackMessage);
    }

	boolean hasPermission(String path) {
		return hasPermission(path, false);
	}

    String hasPermissionMessage(String path) {
        return hasPermissionMessage(path, null);
    }

	void prunePermission(String path) {
		if (!hasText(path)) {
			setPermission(false);
			children = null;
		} else {
			prunePermission(path.split(DOT_REGEX));
		}
	}

	private boolean hasPermission(String[] names, boolean defFlag) {
		if (flag != null) {
			defFlag = flag.booleanValue();
		}
		if (names.length > 0) {
			PermissionNode child = findChild(names[0]);
			if (child != null) {
				return child.hasPermission(subnames(names), defFlag);
			}
		}
		return defFlag;
	}

    private String hasPermissionMessage(String[] names, String fallback) {
        if (StringUtils.isNotBlank(message)) {
            fallback = message;
        }
        if (names.length > 0) {
            PermissionNode child = findChild(names[0]);
            if (child != null) {
                return child.hasPermissionMessage(subnames(names), fallback);
            }
        }
        return fallback;
    }

	private void setPermission(String[] names, boolean flag) {
		switch (names.length) {
		case 0:
			setPermission(flag);
			break;
		case 1:
			findOrMakeChild(names[0]).setPermission(flag);
			break;
		default:
			findOrMakeChild(names[0]).setPermission(subnames(names), flag);
			break;
		}
	}

    private void setPermissionMessage(String[] names, String message) {
        switch (names.length) {
            case 0:
                setPermissionMessage(message);
                break;
            case 1:
                findOrMakeChild(names[0]).setPermissionMessage(message);
                break;
            default:
                findOrMakeChild(names[0]).setPermissionMessage(subnames(names), message);
                break;
        }
    }

	private void prunePermission(String[] names) {
		int index = findChildIndex(names[0]);
		if (index >= 0) {
			if (names.length == 1) {
				if (children.size() == 1) {
					children = null;
				} else {
					children.remove(index);
				}
			} else {
				children.get(index).prunePermission(subnames(names));
			}
		}
	}

	private PermissionNode findChild(final String childName) {
		final int index = findChildIndex(childName);
		return index >= 0 ? children.get(index) : null;
	}

	private int findChildIndex(final String childName) {
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).name.equals(childName)) {
					return i;
				}
			}
		}
		return -1;
	}

	private PermissionNode findOrMakeChild(final String name) {
		PermissionNode child = findChild(name);
		if (child == null) {
			child = new PermissionNode(name);
			sorted = false;
			if (children == null) {
				children = new ArrayList<>();
				sorted = true;
			}
			children.add(child);
		}
		return child;
	}

	private List<PermissionNode> sortChildren() {
		if (children != null) {
			if (!sorted) {
				sort(children, BY_NAME);
				sorted = true;
			}
			for (PermissionNode child : children) {
				child.sortChildren();
			}
		}
		return children;
	}

	private static String[] subnames(String[] names) {
		String[] subnames = new String[names.length - 1];
		arraycopy(names, 1, subnames, 0, subnames.length);
		return subnames;
	}

	@Override
	public void serialize(JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeFieldName(name);
		if (children == null) {
			if (flag != null) {
				jgen.writeBoolean(flag);
			} else if (StringUtils.isNotBlank(message)) {
                jgen.writeString(message);
            } else {
                jgen.writeStartObject();
                jgen.writeEndObject();
            }
		} else {
			jgen.writeStartObject();
			if (flag != null) {
				jgen.writeBooleanField(DEFAULT, flag);
			}
			for (PermissionNode child : sortChildren()) {
				child.serialize(jgen, provider);
			}
			jgen.writeEndObject();
		}
	}

	@Override
	public void serializeWithType(JsonGenerator jgen,
			SerializerProvider provider, TypeSerializer typeSer)
			throws IOException, JsonProcessingException {
		serialize(jgen, provider);
	}

	private Object[] fields() {
		return new Object[]{ name, flag, message, sortChildren() };
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PermissionNode) {
			PermissionNode that = (PermissionNode) o;
			return nullSafeEquals(this.fields(), that.fields());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return nullSafeHashCode(fields());
	}
}
