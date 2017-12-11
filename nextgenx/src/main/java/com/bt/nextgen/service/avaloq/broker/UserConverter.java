package com.bt.nextgen.service.avaloq.broker;

import com.avaloq.abs.screen_rep.hira.btfg$ui_oe_struct_person_hira.*;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.bt.nextgen.core.mapping.MappingUtil;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.*;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SuppressWarnings({"squid:UnusedProtectedMethod","squid:MethodCyclomaticComplexity"})
@Service
class UserConverter extends AbstractMappingConverter
{
    @Autowired
    private StaticIntegrationService staticIntegrationService;

    protected JobBrokerHolder toUserBrokerMap(Rep report, ServiceErrors serviceErrors)
    {
        Map <UserKey, Collection<JobKey>> userJobMap = new HashMap<>();

		Map <JobKey, BrokerUser> jobMap = new HashMap <>();
        if (!MappingUtil.isEmpty(report, serviceErrors))
        {
            //xsd name is "person but this is actually a broker/oe
            for (Person person : report.getData().getPersonList().getPerson())
            {
                PersonHead brokerHead = MappingUtil.singleItem(person.getPersonHeadList().getPersonHead(), serviceErrors);
                BrokerKey brokerKey = BrokerKey.valueOf(AvaloqGatewayUtil.ctxIdOf(brokerHead.getOe()));
                if (brokerHead.getJobList() != null && brokerHead.getJobList().getJob().size() > 0)
                {
                    //Jobs are constructed from sequential job records.
                    //you will have 0-1 "user" records which have a jobKey element and 0-1 "person" records
                    //which have a jobPersId element. Between each will be an empty record with neither
                    int i = 0;
                    int listSize = brokerHead.getJobList().getJob().size();
                    BrokerUserImpl user = null;

                    while (i < listSize)
                    {
                        Job jobRecord = brokerHead.getJobList().getJob().get(i);
                        if (jobRecord.getJobId() != null)
                        {
                            user = processUserRecord(userJobMap,jobMap, brokerKey, jobRecord, serviceErrors);
                        }
                        if (jobRecord.getJobPersId() != null)
                        {
                            processPersonRecord(user, jobRecord, brokerHead);
                        }
                        i++;
                    }
                }
            }
        }
		JobBrokerHolder jobBrokerHolder = new JobBrokerHolder(userJobMap,jobMap);
        return jobBrokerHolder;
    }

    private BrokerUserImpl processUserRecord(Map <UserKey, Collection<JobKey>> userJobMap, Map <JobKey, BrokerUser> jobMap, BrokerKey brokerKey, Job job,
                                             ServiceErrors serviceErrors)
    {

        UserKey userKey = UserKey.valueOf(AvaloqGatewayUtil.asString(job.getJobKey()));

		JobKey jobKey = JobKey.valueOf(AvaloqGatewayUtil.asString(job.getJobId()));

		Collection<JobKey> jobs = userJobMap.get(userKey);
		if(jobs==null)
		{
			jobs = new ArrayList<JobKey>();
			userJobMap.put(userKey,jobs);
		}
		jobs.add(jobKey);

        BrokerUserImpl user =(BrokerUserImpl)jobMap.get(jobKey);
        if (user == null) {
            user = new BrokerUserImpl(userKey,jobKey);
            jobMap.put(jobKey, user);
        }

		user.setCustomerId(AvaloqGatewayUtil.asString(job.getPersGcmId()));

        JobRole role = JobRole.forCode(staticIntegrationService.loadCode(CodeCategory.JOB_TYPE,
                AvaloqGatewayUtil.asString(job.getJobTypeId()),
                serviceErrors));

        JobAuthorizationRole authRole = JobAuthorizationRole.forCode(staticIntegrationService.loadCode(CodeCategory.AUTH_ROLE,
                AvaloqGatewayUtil.asString(job.getOeJobAuthRoleId()),
                serviceErrors));
        user.addBroker(role,brokerKey,authRole);
        user.setProfileId(AvaloqGatewayUtil.asString(job.getJobUserId()));
        user.setRegisteredOnline(AvaloqGatewayUtil.asBoolean(job.getPersonRegOnline()));
        user.setCISKey(CISKey.valueOf(AvaloqGatewayUtil.asString(job.getPersCisKey())));
        return user;
    }

    private void processPersonRecord(BrokerUserImpl user, Job job, PersonHead brokerHead)
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        user.setFirstName(AvaloqGatewayUtil.asString(job.getPersFirstName()));
        user.setMiddleName(AvaloqGatewayUtil.asString(job.getPersMiddleName()));
        user.setLastName(AvaloqGatewayUtil.asString(job.getPersLastName()));
        user.setCISKey(CISKey.valueOf(AvaloqGatewayUtil.asString(job.getPersCisKey())));
        user.setClientKey(ClientKey.valueOf(AvaloqGatewayUtil.asString(job.getJobPersId())));
        user.setEntityId(AvaloqGatewayUtil.asString(brokerHead.getIntmEntityTypeId()));
        user.setPracticeName(AvaloqGatewayUtil.asString(brokerHead.getOe()));
        //Set the GCM ID and referenceStartDate

        user.setCustomerId(AvaloqGatewayUtil.asString(job.getPersGcmId()));
        user.setReferenceStartDate(AvaloqGatewayUtil.asDateTime(job.getPersOpenDate()));

        //Address Collection
        if (job.getAddrList() != null && job.getAddrList().getAddr() != null)
        {

            setContactDetails(user, job, serviceErrors);
        }
    }


    private void setContactDetails(BrokerUserImpl broker, Job job, ServiceErrors serviceErrors)
    {

        List <Address> addresses = new ArrayList <>();
        List <Phone> phones = new ArrayList <>();
        List <Email> emails = new ArrayList <>();
        for (Addr addr : job.getAddrList().getAddr()) {
            if (addr.getPersAddrId() != null) {
                AddressImpl address = getMapper().map(addr, AddressImpl.class, serviceErrors);
                Code addrType = staticIntegrationService.loadCode(CodeCategory.ADDR_CATEGORY,
                        AvaloqGatewayUtil.asString(addr.getAddrCatgId()),
                        serviceErrors);
                AddressType addressType= null;
                if(addrType!=null) {
                    addressType  = AddressType.getAddressType(addrType.getIntlId());
                }
                Code addrMedium = staticIntegrationService.loadCode(CodeCategory.ADDR_MEDIUM,
                        AvaloqGatewayUtil.asString(addr.getAddrMediumId()),
                        serviceErrors);
                AddressMedium addressMedium = null;
                if(addrMedium!=null) {
                    addressMedium = AddressMedium.getAddressMedium(addrMedium.getIntlId());
                }
                boolean prefered = false;
                String addressKind = AvaloqGatewayUtil.asString(addr.getAddrKindId());
                if (!StringUtils.isBlank(addressKind)) {
                    Code code = staticIntegrationService.loadCode(CodeCategory.ADDR_KIND, addressKind, serviceErrors);
                    prefered = code != null && Constants.PREFERRED_CONTACT.equals(code.getIntlId()) ? true : false;

                }
                if (AddressType.ELECTRONIC.equals(addressType)) {
                    if (AddressMedium.isPhoneNumber(addressMedium)) {
                        String number = AvaloqGatewayUtil.asString(addr.getElecAddr());
                        Phone phone = new PhoneImpl(address.getAddressKey(),
                                addressMedium,
                                number,
                                null,
                                null,
                                address.getModificationSeq(),
                                prefered,
                                addressType);
                        phones.add(phone);
                    } else {
                        Email email = new EmailImpl(address.getAddressKey(),
                                addressMedium,
                                AvaloqGatewayUtil.asString(addr.getElecAddr()),
                                address.getModificationSeq(),
                                prefered,
                                addressType);
                        emails.add(email);
                    }
                } else {
                    address.setPreferred(prefered);
                    address.setAddressType(addressMedium);
                    setAddressDetailsFromCode(address.getCountryCode(), address.getStateCode(), address, serviceErrors);
                    if (address.getPoBoxPrefix() != null)
                    {
                        setPoBoxPrefixFromCode(address.getPoBoxPrefix(), address, serviceErrors);
                    }
                    addresses.add(address);
                }
            }
            broker.setAddresses(addresses);
            broker.setEmails(emails);
            broker.setPhones(phones);
        }
    }

    public void setPoBoxPrefixFromCode(String poBoxPrefix, AddressImpl address, ServiceErrors serviceErrors)
    {
        Code prefix = staticIntegrationService.loadCode(CodeCategory.BOX_ADDR_TYPE, poBoxPrefix, serviceErrors);
        address.setPoBoxPrefix(prefix.getName());
    }

    /**
     * Set address details - Country and state in the address
     * @param countryCode
     * @param stateCode
     * @param address
     * @param serviceErrors
     */
    public void setAddressDetailsFromCode(String countryCode, String stateCode, AddressImpl address, ServiceErrors serviceErrors)
    {
        Code country = staticIntegrationService.loadCode(CodeCategory.COUNTRY, countryCode, serviceErrors);
        if(country!=null)address.setCountry(country.getName());

        if (null != country && Constants.COUNTRY_AUSTRALIA.equals(country.getName()))
        {
            //address.setState(staticIntegrationService.loadCode(CodeCategory.STATES, stateCode, serviceErrors).getName());
            Code stateCodeCat = staticIntegrationService.loadCode(CodeCategory.STATES, stateCode, serviceErrors);
            if(stateCodeCat!=null)
			{
				address.setState(stateCodeCat.getName());
				address.setStateAbbr(stateCodeCat.getUserId());
			}
        }
    }


}
