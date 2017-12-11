package com.bt.nextgen.cms.service;

import com.bt.nextgen.cms.CmsEntry;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.SETTINGS;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.core.xml.XmlUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Collections.unmodifiableCollection;

public class CmsServiceXmlResourceImpl implements CmsService
{
	private static final Logger logger = LoggerFactory.getLogger(CmsServiceXmlResourceImpl.class);

    private final List<Resource> cmsIndexResources;
    private Map<String, CmsEntryJaxb> cmsDatabase;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private RequestQuery requestQuery;

    public CmsServiceXmlResourceImpl(List<Resource> resources)
	{
        cmsIndexResources = resources;
		loadCmsContent();
	}

	@Override
    public STATUS reLoadCmsContent()
	{
		STATUS status = STATUS.FAILURE;

		try
		{
			loadCmsContent();
			status = STATUS.SUCCESS;
		}
		catch (RuntimeException e)
		{
			logger.warn("Problem reloading the cms content, returning failure", e);
		}

		return status;
	}


	private Roles determineUserRole()
	{
		Roles userRole = null;

		// Else looks like there might be an authenticated session happening - try to rip the role from there...
		if (userProfileService != null)
		{
			try
			{
				userRole = userProfileService.getPrimaryRole();
		}
			catch (Exception e)
			{
				logger.debug("User does not have an authenticated sesssion - check request header to determine type of user", e);
			}
		}

		// User currently not in an authenticated session - read the request header to determine environment
		if (userRole == null)
		{
			try
			{
				if (requestQuery != null)
				{
					String domain = requestQuery.getOriginalHost();

					if (domain.contains(SETTINGS.ADVISER_URL_MATCH.value()))
					{
						userRole = Roles.ROLE_ADVISER;
					}
				}

				if (userRole == null)
				{
					userRole = Roles.ROLE_INVESTOR;
				}
			}
			catch (Exception e)
			{
				userRole = Roles.ROLE_INVESTOR; // Catch-all role
				logger.warn("Unable to determine the user role type based on the current domain. Defaulting to investor messages",
					e);

			}
		}

		return userRole;
	}

	@Override
	public String getContent(String key)
	{
		Roles userRole = determineUserRole();

		String message = getRawContent(key).getValue();

		if (!StringUtils.isEmpty(message))
		{
			message = resolveContactDetails(message, userRole);
		}

		return message;
	}

	public String resolveContactDetails(String message, Roles userRole)
	{
		final String ADVISER_CONTACT_NUMBER_KEY = "ADVISER_CONTACT_NUMBER";
		final String INVESTOR_CONTACT_NUMBER_KEY = "INVESTOR_CONTACT_NUMBER";
		final String CONTACT_NUMBER_PLACEHOLDER = "[CONTACT_NUMBER_PLACEHOLDER]";

		if (!message.contains(CONTACT_NUMBER_PLACEHOLDER))
			return message;

		try
		{
			if (userRole != null)
			{
				if (Roles.ROLE_ADVISER.equals(userRole))
				{
					String adviserContact = getContent(ADVISER_CONTACT_NUMBER_KEY);
					message = message.replace(CONTACT_NUMBER_PLACEHOLDER, adviserContact);
				}
				else if (Roles.ROLE_INVESTOR.equals(userRole))
				{
					String investorContact = getContent(INVESTOR_CONTACT_NUMBER_KEY);
					message = message.replace(CONTACT_NUMBER_PLACEHOLDER, investorContact);
				}
				else if (Roles.ROLE_SERVICE_OP.equals(userRole) || Roles.ROLE_TRUSTEE.equals(userRole))
				{
					message = message.replace(CONTACT_NUMBER_PLACEHOLDER, "Production Support team");
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Error resolving contact details: " + message);
		}

		return message;
	}

	@Override
	public CmsEntry getRawContent(String key)
	{
        CmsEntry entry = resolveFirstMatch(key);
		return FileCmsEntry.wrapIfValueUrl(entry);
	}



	/**
	 * Give a list of keys, find the first match. Keys are assumed to be in priority order
	 *
	 * @param keys list of keys in priority order
	 * @return a CmsEntry or null if nothing found
	 */
	private CmsEntry resolveFirstMatch(String... keys)
	{
		for (String key : keys)
		{
            CmsEntry value = cmsDatabase.get(key);
			if (value != null)
			{
				return value;
			}
		}
		return MISSING;
	}

	/**
	 * Responsible for loading the content into memory
	 */
	private void loadCmsContent()
	{
        String filename = "";
        try
		{
			JAXBContext jc = JAXBContext.newInstance(CmsDatabase.class);
			Unmarshaller uMarsh = jc.createUnmarshaller();
            cmsDatabase = new HashMap<>();
            for (Resource cmsIndexResource : cmsIndexResources) {
                filename = cmsIndexResource.getFilename();
                CmsDatabase cms = (CmsDatabase) uMarsh.unmarshal(XmlUtil.getXMLReader(cmsIndexResource.getInputStream()));
                logger.info("Parsed {} entries from the index file {}.", cms.entries.size(), filename);
                cmsDatabase.putAll(cms.entries);
            }

            mergeErrorCodesInCmsDatabase();
		}
		catch (JAXBException | IOException | XMLStreamException e)
		{
            logger.error("Problem parsing the cms index file {}", filename);
			throw new RuntimeException(e);
		}
	}

    private void mergeErrorCodesInCmsDatabase() throws JAXBException, IOException, XMLStreamException {
        Set<Entry<String, CmsEntryJaxb>> cmsEntrySet = getErrorCodeEntrySet();
        for (Entry<String, CmsEntryJaxb> entry : cmsEntrySet) {
            String key = entry.getKey();
            cmsDatabase.put(key, entry.getValue());
        }
    }

    public Set<Entry<String, CmsEntryJaxb>> getErrorCodeEntrySet() throws JAXBException, IOException, XMLStreamException {
        Set<Entry<String, CmsEntryJaxb>> cmsEntrySet = new HashSet<>();
        CmsEntry errorMsgFile = resolveFirstMatch(Attribute.LOAD_VALIDATION_ERROR_MESSAGES);
        if (MISSING.equals(errorMsgFile)) {
            logger.warn("validation messages file entry not found in cms-index.xml");
        } else {
            CmsEntry cmsEntry = FileCmsEntry.wrapIfValueUrl(errorMsgFile);
            logger.info("Loaded Cms entry on validation messages, resource is {}", cmsEntry.getValue());
            JAXBContext jc = JAXBContext.newInstance(CmsDatabase.class);
            Unmarshaller uMarsh = jc.createUnmarshaller();
            CmsDatabase errorCodeDatabase = (CmsDatabase) uMarsh.unmarshal(XmlUtil.getXMLReader(cmsEntry.getStream()));
            logger.info("Loaded CMS database from validation error file. {} entries loaded", errorCodeDatabase.entries.size());
            cmsEntrySet = errorCodeDatabase.entries.entrySet();
        }
        return cmsEntrySet;
    }

	/**
	 * Method to get the new dynamic message built with the parameter values passed in the params[] array.
	 * In the content message put {} at the place where params value get replaced.
	 * Ordering is mandate.
	 *
	 * TODO Review this and see if it should be removed, this is possibly included due to a bad merge. (AJRB)
	 */
	@Override
    public String getDynamicContent(String key, String[] params)
	{
		String content = getContent(key);
		if (null != content)
		{
			String[] newParams = new String[params != null ? (params.length + 1) : 1];
			newParams[0] = "";
			if (null != params)
			{
				for (int i = 0; i < params.length; i++)
				{
					newParams[i + 1] = params[i];

				}
			}
			MessageFormat messageFormat = new MessageFormat(getContent(key));
			String formattedString = messageFormat.format(getContent(key), newParams);
            return formattedString.toString().replaceAll("\\s+", " ").replaceAll(", ,", ",");
		}
		return null;
	}

	@Override
	public Collection <String> getRawContentIndex()
	{
        return unmodifiableCollection(cmsDatabase.keySet());
	}
}
