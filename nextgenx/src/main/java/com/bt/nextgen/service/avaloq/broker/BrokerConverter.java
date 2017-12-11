package com.bt.nextgen.service.avaloq.broker;

import java.util.HashMap;
import java.util.Map;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avaloq.abs.screen_rep.hira.btfg$ui_oe_struct_person_hira.Person;
import com.avaloq.abs.screen_rep.hira.btfg$ui_oe_struct_person_hira.PersonHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_oe_struct_person_hira.Rep;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.bt.nextgen.core.mapping.MappingUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.user.UserKey;

@Service
class BrokerConverter extends AbstractMappingConverter
{
    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected Map <BrokerKey, Broker> toBrokerMap(Rep report, ServiceErrors serviceErrors)
    {
        Map <BrokerKey, Broker> brokerMap = new HashMap <>();
        if (!MappingUtil.isEmpty(report, serviceErrors))
        {
            //xsd name is "person but this is actually a broker/oe
            for (Person person : report.getData().getPersonList().getPerson())
            {
                PersonHead brokerHead = MappingUtil.singleItem(person.getPersonHeadList().getPersonHead(), serviceErrors);
                BrokerKey brokerKey = BrokerKey.valueOf(AvaloqGatewayUtil.ctxIdOf(brokerHead.getOe()));
                BrokerType brokerType = BrokerType.forCode(staticIntegrationService.loadCode(CodeCategory.PERSON_CLASS,
                        AvaloqGatewayUtil.asString(brokerHead.getIntmEntityTypeId()),
                        serviceErrors));
                if (brokerType == BrokerType.OTHER)
                {
                    brokerType = BrokerType.forCode(staticIntegrationService.loadCode(CodeCategory.PERSON_CLASS,
                            AvaloqGatewayUtil.asString(brokerHead.getOeTypeId()),
                            serviceErrors));
                }
                BrokerImpl broker = getMapper().map(brokerHead, BrokerImpl.class, serviceErrors);
                broker.setBrokerType(brokerType);
                broker.setPositionName(brokerHead.getOe().getVal());
                //Populate the gcm_id and openDate
                if(null!= brokerHead.getOeGcmId()||null!=brokerHead.getOePersGcmId()){
                    if(null!=brokerHead.getOeGcmId())
                        broker.setBankId(AvaloqGatewayUtil.asString(brokerHead.getOeGcmId()));
                    else
                        broker.setBankId(AvaloqGatewayUtil.asString(brokerHead.getOePersGcmId()));
                }
                broker.setBankKey(UserKey.valueOf(broker.getBankReferenceId()));

                if(null!= brokerHead.getOeOpenDate()||null!=brokerHead.getOePersOpenDate()){
                    if(null!=brokerHead.getOeOpenDate())
                        broker.setBrokerStartDate(AvaloqGatewayUtil.asDateTime(brokerHead.getOeOpenDate()));
                    else
                        broker.setBrokerStartDate(AvaloqGatewayUtil.asDateTime(brokerHead.getOePersOpenDate()));
                }
                if(null!=brokerHead.getHasDgAdvcongFee())
                    broker.setLicenseeFeeActive(AvaloqGatewayUtil.asBoolean(brokerHead.getHasDgAdvcongFee()));

                if(null!=brokerHead.getIsPayableParty())
                    broker.setPayableParty(AvaloqGatewayUtil.asBoolean(brokerHead.getIsPayableParty()));

                if(null!=brokerHead.getOeEbiKey())
                    broker.setExternalBrokerKey(ExternalBrokerKey.valueOf(AvaloqGatewayUtil.asString(brokerHead.getOeEbiKey())));
                if(null!=brokerHead.getOeParentEbiKey())
                    broker.setParentEBIKey(ExternalBrokerKey.valueOf(AvaloqGatewayUtil.asString(brokerHead.getOeParentEbiKey())));

                brokerMap.put(brokerKey, broker);
            }
        }
        BrokerUtils.resolveHierarchy(brokerMap);
        return brokerMap;
    }

    /**
     *
     * @param broker
     * @param brokerHead
     */

    /*
    private void setBrokerDetail(BrokerImpl broker, PersonHead brokerHead, ServiceErrors serviceErrors)
    {
        if (brokerHead.getJobList() != null && brokerHead.getJobList().getJob() != null)
        {
            List <Job> jobs = brokerHead.getJobList().getJob();
            for (Job job : jobs)
            {
                if (job.getJobPersId() != null)
                {
                    broker.setFirstName(AvaloqUtils.asString(job.getPersFirstName()));
                    broker.setMiddleName(AvaloqUtils.asString(job.getPersMiddleName()));
                    broker.setLastName(AvaloqUtils.asString(job.getPersLastName()));
                    if (job.getAddrList() != null && job.getAddrList().getAddr() != null)
                    {
                        setContactDetails(broker, job, serviceErrors);
                    }
                }
            }
        }
    }
    */

    /*
    private void setContactDetails(BrokerImpl broker, Job job, ServiceErrors serviceErrors)
    {

        List <Address> addresses = new ArrayList <>();
        List <Phone> phones = new ArrayList <>();
        List <Email> emails = new ArrayList <>();
        for (Addr addr : job.getAddrList().getAddr()) {
            if (addr.getPersAddrId() != null) {
                AddressImpl address = getMapper().map(addr, AddressImpl.class, serviceErrors);
                Code addrType = staticIntegrationService.loadCode(CodeCategory.ADDR_CATEGORY,
                        AvaloqUtils.asString(addr.getAddrCatgId()),
                        serviceErrors);
                AddressType addressType = null;
                if (addrType != null) {
                    addressType = AddressType.getAddressType(addrType.getIntlId());
                }
                Code addrMedium = staticIntegrationService.loadCode(CodeCategory.ADDR_MEDIUM,
                        AvaloqUtils.asString(addr.getAddrMediumId()),
                        serviceErrors);
                AddressMedium addressMedium = null;
                if (addrMedium != null) {
                    addressMedium = AddressMedium.getAddressMedium(addrMedium.getIntlId());
                }
                boolean prefered = false;
                String addressKind = AvaloqUtils.asString(addr.getAddrKindId());
                if (!StringUtils.isBlank(addressKind)) {
                    Code code = staticIntegrationService.loadCode(CodeCategory.ADDR_KIND, addressKind, serviceErrors);
                    prefered = code != null && Constants.PREFERRED_CONTACT.equals(code.getIntlId()) ? true : false;

                }
                if (AddressType.ELECTRONIC.equals(addressType)) {
                    if (AddressMedium.isPhoneNumber(addressMedium)) {
                        String number = AvaloqUtils.asString(addr.getElecAddr());
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
                                AvaloqUtils.asString(addr.getElecAddr()),
                                address.getModificationSeq(),
                                prefered,
                                addressType);
                        emails.add(email);
                    }
                } else {
                    address.setPreferred(prefered);
                    address.setAddressType(addressMedium);
                    setAddressDetailsFromCode(address.getCountryCode(), address.getStateCode(), address, serviceErrors);
                    addresses.add(address);
                }
            }
        }
        broker.setAddresses(addresses);
        broker.setEmails(emails);
        broker.setPhones(phones);
    }*/

    /**
     * Set address details - Country and state in the address
     * @param countryCode
     * @param stateCode
     * @param address
     * @param serviceErrors
     */

    /*
    public void setAddressDetailsFromCode(String countryCode, String stateCode, AddressImpl address, ServiceErrors serviceErrors)
    {
        Code country = staticIntegrationService.loadCode(CodeCategory.COUNTRY, countryCode, serviceErrors);
        if(country!=null)address.setCountry(country.getName());
        if (null != country && Constants.COUNTRY_AUSTRALIA.equals(country.getName()))
        {
            //address.setState(staticIntegrationService.loadCode(CodeCategory.STATES, stateCode, serviceErrors).getName());
            Code stateCodeCat = staticIntegrationService.loadCode(CodeCategory.STATES, stateCode, serviceErrors);
            if(stateCodeCat!=null)address.setState(stateCodeCat.getName());
            address.setStateAbbr(stateCodeCat.getUserId());
        }

    }*/



}
