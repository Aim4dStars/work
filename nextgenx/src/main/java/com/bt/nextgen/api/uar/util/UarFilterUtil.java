package com.bt.nextgen.api.uar.util;



import com.bt.nextgen.api.uar.model.*;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.JobProfileUser;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.bt.nextgen.service.avaloq.broker.JobProfileAnnotatedHolder;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.uar.*;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by L081012 on 13/01/2016.
 */

/** This Utility Class provides methods for returning the List of UAR records for the User based on Search Parameters */
public class UarFilterUtil {

    private static final Logger logger = LoggerFactory.getLogger(UarFilterUtil.class);
    private UarIntegrationService uarIntegrationService;
    private UserProfileService userProfileService;
    private BrokerIntegrationService brokerIntegrationService;


    private static final String COMPLETED = "Completed";
    private static final String PENDING = "Pending";
    private static final int REVIEW_DATE_TO_UAR_DATE = 28;

    /**
     *
     * @param uarIntegrationService
     * @param userProfileService
     * @param brokerIntegrationService
     */
    public UarFilterUtil(UarIntegrationService uarIntegrationService, UserProfileService userProfileService, BrokerIntegrationService brokerIntegrationService) {
        this.uarIntegrationService = uarIntegrationService;
        this.brokerIntegrationService = brokerIntegrationService;
        this.userProfileService = userProfileService;
    }

    /**
     *
     * @param serviceErrors
     * @return
     */
    public String getUarDocId(ServiceErrors serviceErrors) {
        List<String> keys = new ArrayList<>();
        String docId = null;
        List<Broker> broker = brokerIntegrationService.getBrokersForJob(userProfileService.getActiveProfile(), serviceErrors);
        if (null!=broker) {
            keys.add(broker.get(0).getKey().getId());
            UarDoc uarDoc = uarIntegrationService.getUarOrderId(keys, serviceErrors);
            if (null != uarDoc.getDocId())
                docId = uarDoc.getDocId().toString();
        }
        logger.info("getUarDocId():: Broker:{} UarDocID:{}", keys.get(0), docId);
        return docId;
    }

    /**
     * Returns the list of UAR records for the Authoriser
     *
     * @param serviceErrors
     * @return
     */
    public List<UarDetailsDto> getUarClients(final ServiceErrors serviceErrors) {
        UarRequest request = new UarRequestImpl();
        UarDetailsDto uarDetailDto = new UarDetailsDto();
        List<UarDetailsDto> uarDetailsDto = new ArrayList<>();

        String docId = getUarDocId(serviceErrors);
        if (null!=docId) {
            request.setDocId(docId);
            final UarResponse uarResponse = uarIntegrationService.getUarAccounts(request, serviceErrors);
            uarDetailDto = getUarDetailDto(uarResponse, serviceErrors);

        }
        uarDetailsDto.add(uarDetailDto);
        return uarDetailsDto;
    }

    private UarDetailsDto getUarDetailDto(UarResponse uarResponse, ServiceErrors serviceErrors) {
        UarDetailsDto uarDetailDto = new UarDetailsDto();
        if (null!= uarResponse.getUarRecords() && !uarResponse.getUarRecords().isEmpty()) {
            logger.info("getUarClients()::Uar response not null DocId:{}, Status:{}", uarResponse.getDocId(),uarResponse.getWfcDisplayText());
            uarDetailDto.setUarComponent(toUarDto(uarResponse, serviceErrors));
        }
        uarDetailDto.setUserPermissions(getUarPermissions());
        uarDetailDto.setUarDate(getDateOnly(uarResponse.getUarDate().plusDays(REVIEW_DATE_TO_UAR_DATE)));
        uarDetailDto.setKey(UserKey.valueOf(uarResponse.getBrokerId()));
        uarDetailDto.setDocId(uarResponse.getDocId());
        return uarDetailDto;
    }

    /**
     * Returns the list of UAR records for the Authoriser
     *
     * @param serviceErrors
     * @return
     * */
    public UarDetailsDto submitUarRecords(UarDetailsDto uarDetailsDto, final ServiceErrors serviceErrors) {

        UarDetailsDto uarDetailDto1 = new UarDetailsDto();
        if((uarDetailsDto.getDocId().toString().equalsIgnoreCase(getUarDocId(serviceErrors)))) {
            UarRequest request = toUarRequest(uarDetailsDto);
            final UarResponse uarResponse = uarIntegrationService.submitUarAccounts(request, serviceErrors);
            uarDetailDto1 = getUarDetailDto(uarResponse, serviceErrors);
            uarDetailDto1.setSubmitStatus("SUCCESS");
        }
        else{
            uarDetailDto1.setSubmitStatus("Failure");
        }
        return uarDetailDto1;
    }

    private UarRequest toUarRequest(UarDetailsDto uarDetailsDto) {
        UarRequest request = new UarRequestImpl();
        request.setBrokerId(uarDetailsDto.getKey().toString());
        request.setDocId(uarDetailsDto.getDocId()+"");
        List<UarRecords> uars = new ArrayList<UarRecords>();
        for(UarDto uarDto : uarDetailsDto.getUarComponent()) {
           UarRecords uarRecords = new UarRecords();
           uarRecords.setPersonId(uarDto.getUserPersonId());
           uarRecords.setJobId(uarDto.getJobId());
           uarRecords.setDecisionId(uarDto.getUarAction());
           uarRecords.setBrokerId(uarDto.getBrokerId());
           uarRecords.setRecordType(uarDto.getUarType());
            uarRecords.setRecordIndex(uarDto.getRecordIndex());

            if( null != uarDto.getUserPermissionLevel() && !uarDto.getUserPermissionLevel().isEmpty() ){
                UarPermissions uarPermissions= UarPermissions.getUarPermissions(uarDto.getUserPermissionLevel());
                if( uarPermissions !=null )
                    uarRecords.setPermissionId(uarPermissions.getCode());
            }
           uars.add(uarRecords);
        }
        request.setUarRecords(uars);
        return request;
    }

    /**
     *
     * @param uarResponse
     * @param serviceErrors
     * @return
     */
    private List<UarDto> toUarDto(UarResponse uarResponse, ServiceErrors serviceErrors) {
        List <UarDto> uarRecords = new ArrayList<>();
        if (null!=uarResponse) {
            for(UarRecords uarRecord : uarResponse.getUarRecords()){
                if (!uarRecord.getIsFrozen() && ( (getUarStatus(uarRecord).equalsIgnoreCase(PENDING) && !uarRecord.getIsInvalid()) || getUarStatus(uarRecord).equalsIgnoreCase(COMPLETED) ) ) {
                    UarDto uarDto = new UarDto();
                    BrokerUser brokerUser = brokerIntegrationService.getBrokerUser(getJobProfileIdentifier(uarRecord.getJobId()), serviceErrors);
                    uarDto.setUserName(null != brokerUser ? getUserName(brokerUser.getFirstName(), brokerUser.getMiddleName(), brokerUser.getLastName()) : uarRecord.getPersonName());
                    uarDto.setUserRole(setUserRole(brokerUser.getRoles(), uarRecord, serviceErrors));
                    uarDto.setUserAdviserName(getNameWithoutPosition(uarRecord.getBrokerName()));
                    uarDto.setUserAdviserId(uarRecord.getPersonId());
                    uarDto.setUserPersonId(uarRecord.getPersonId());
                    uarDto.setDaysSinceLastUar(null != uarRecord.getLastUarDate() ? Days.daysBetween(uarRecord.getLastUarDate(), new DateTime()).getDays() : 0);
                    uarDto.setLastUarDate(null != uarRecord.getLastUarDate() ? getDateOnly(uarRecord.getLastUarDate()) : null);
                    uarDto.setUserPermissionLevel(uarRecord.getCurrPermissionName());
                    uarDto.setIsFrozen(uarRecord.getIsFrozen());
                    uarDto.setIsInvalid(uarRecord.getIsInvalid());
                    uarDto.setUarType(uarRecord.getRecordType());
                    setStatus(uarRecord, uarDto);
                    setUserLinkedTo(uarResponse, uarRecord, uarDto, serviceErrors);
                    if (null != uarRecord.getErrors() && !uarRecord.getErrors().isEmpty())
                        setUarError(uarRecord, uarDto);
                    uarDto.setBrokerId(uarRecord.getBrokerId());
                    uarDto.setJobId(uarRecord.getJobId());
                    uarDto.setRowId(uarRecord.getBrokerId() + uarRecord.getJobId());
                    uarDto.setRecordIndex(uarRecord.getRecordIndex());
                    uarRecords.add(uarDto);
                }
            }
        }
        return uarRecords;
    }

    /*
    * @params JobProfileUser, UarRecords, ServiceErrors
    * @returns userRole
    * @description User role will vary for asim oe, need to append asim displayname to user role
    * */

    private String setUserRole(JobProfileUser jobProfileUser, UarRecords uarRecord, ServiceErrors serviceErrors) {
        String userRole = null != jobProfileUser ? WordUtils.capitalizeFully(((JobProfileAnnotatedHolder) jobProfileUser).getRole().name().replaceAll("_", " ")) : null;
        if ("Adviser".equalsIgnoreCase(userRole) || "Paraplanner".equalsIgnoreCase(userRole)) {
            final Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(uarRecord.getBrokerId()), serviceErrors);
            if (UserExperience.ASIM == broker.getUserExperience()) {
                userRole =  new StringBuilder(userRole).append(" (").append(UserExperience.ASIM.getDisplayName()).append(")").toString();
            }
        }
        return userRole;
    }

    private String setUserRole(Collection<BrokerRole> brokerRoles, UarRecords uarRecord, ServiceErrors serviceErrors) {

        String userRole = null != brokerRoles ? WordUtils.capitalizeFully((brokerRoles.iterator().next()).getRole().name().replaceAll("_", " ")) : null;
        if ("Adviser".equalsIgnoreCase(userRole) || "Paraplanner".equalsIgnoreCase(userRole)) {
            final Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(uarRecord.getBrokerId()), serviceErrors);
            if (UserExperience.ASIM == broker.getUserExperience()) {
                userRole =  new StringBuilder(userRole).append(" (").append(UserExperience.ASIM.getDisplayName()).append(")").toString();
            }
        }
        return userRole;
    }

    private void setUserLinkedTo(UarResponse uarResponse, UarRecords uarRecord, UarDto uarDto, ServiceErrors serviceErrors) {
        if (null!=uarDto.getUserRole()) {
            switch (uarDto.getUserRole()) {
                case "Dealer Group Manager":
                case "Practice Manager":
                    setDgmPmUserLinkedTo(uarRecord, uarDto, serviceErrors);
                    break;
                case "Adviser":
                    setAdviserUserLinkedTo(uarResponse, uarDto, serviceErrors);
                    break;
                case "Assistant":
                case "Paraplanner":
                    setAsstParaPlnUserLinkedTo(uarRecord, uarDto, serviceErrors);
                    break;
                default:
                    uarDto.setUserLinkedTo("");
                    break;
            }
        } else {
            logger.debug("setUserLinkedTo():: User role set to:{}", uarDto.getUserRole());
        }
    }

    private void setDgmPmUserLinkedTo(UarRecords uarRecord, UarDto uarDto, ServiceErrors serviceErrors) {
        Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(uarRecord.getBrokerId()), serviceErrors);
        uarDto.setUserLinkedTo(null!=broker?getNameWithoutPosition(broker.getPositionName()):null);
    }

    private void setAsstParaPlnUserLinkedTo(UarRecords uarRecord, UarDto uarDto, ServiceErrors serviceErrors) {
        Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(uarRecord.getBrokerId()), serviceErrors);
        if (broker!=null) {
            if (broker.getBrokerType() == BrokerType.ADVISER)
                uarDto.setUserLinkedTo("For "+getNameWithoutPosition(broker.getPositionName()));
            else if (broker.getDealerKey() != null || broker.getOfficeKey() != null || broker.getPracticeKey() != null) {
                if (broker.getOfficeKey() != null)
                    broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(broker.getOfficeKey().getId()), serviceErrors);
                else if (broker.getPracticeKey() != null)
                    broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(broker.getPracticeKey().getId()), serviceErrors);
                else
                    broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(broker.getDealerKey().getId()), serviceErrors);
                uarDto.setUserLinkedTo(getNameWithoutPosition(broker.getPositionName()));
            }
        }
    }

    private void setAdviserUserLinkedTo(UarResponse uarResponse, UarDto uarDto, ServiceErrors serviceErrors) {
        Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(uarResponse.getBrokerId()), serviceErrors);
        if (null!=broker) {
            if (broker.getDealerKey() != null || broker.getOfficeKey() != null || broker.getPracticeKey() != null) {
                if (broker.getOfficeKey() != null)
                    broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(broker.getOfficeKey().getId()), serviceErrors);
                else if(broker.getPracticeKey() != null)
                    broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(broker.getPracticeKey().getId()), serviceErrors);
                else
                    broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(broker.getDealerKey().getId()), serviceErrors);
                uarDto.setUserLinkedTo(null!=broker?getNameWithoutPosition(broker.getPositionName()):null);
            }
        }
    }

    private String getDateOnly(DateTime dateTime) {
        DecimalFormat df = new DecimalFormat("00") ;
        return dateTime.getYear()+"-" + df.format(dateTime.getMonthOfYear())+"-"+df.format(dateTime.getDayOfMonth());
    }

    private String getUserName(String firstName, String middleName, String lastName) {
        return lastName+","+" "+(middleName!=null?middleName+" ":"")+firstName;
    }

    private JobProfileUser getJobProfile(UarRecords uarRecord, BrokerAnnotationHolder broker) {
        JobProfileUser jobProfileUser=null;

        if (null!=broker) {
            List<JobProfileUser> jobList = broker.getJobList();
            if(null!=jobList) {
                logger.debug("Joblist:{}", jobList.size());
                for (int i=0;i<jobList.size();i++) {
                    if (null!=jobList.get(i).getJob() && jobList.get(i).getJob().getId().equalsIgnoreCase(uarRecord.getJobId())) {
                        logger.debug("Job matched:{}", uarRecord.getJobId());
                        jobProfileUser = jobList.get(i);
                        break;
                    }
                }
            }
        }
        return jobProfileUser;
    }

    private String getUarStatus (UarRecords uarRecord) {
        String status = "";
        if (null==uarRecord.getErrors() && null!=uarRecord.getUarDoneDate()) {
            status = COMPLETED;
        } else {
            status = PENDING;
        }
        return status;
    }

    private void setStatus(UarRecords uarRecord, UarDto uarDto) {
        uarDto.setUarStatus(getUarStatus(uarRecord));
    }

    private void setUarError(UarRecords uarRecord, UarDto uarDto) {
        Iterator<ServiceError> serviceErrorIterator = uarRecord.getErrors().iterator();
        List<ServiceError> errorList = new ArrayList<>();

        while (serviceErrorIterator.hasNext()) {
            ServiceError serror = (ServiceError) serviceErrorIterator.next();
            errorList.add(serror);
            uarDto.setErrors(errorList);
        }
    }

    private List<UarPermissionDto> getUarPermissions() {
        List<UarPermissionDto> uarPermissionDtoList = new ArrayList<>();
        for (JobRole jobRole: JobRole.values()) {
            List<String> permissionsList = new ArrayList<>();
            UarPermissionDto uarPermissionDto = new UarPermissionDto();
            uarPermissionDto.setRoleName(WordUtils.capitalizeFully(jobRole.name().replaceAll("_", " ")));
            if (JobRole.PRACTICE_MANAGER.equals(jobRole)) {
                permissionsList.add(UarPermissions.CAN_UPDATE.getLabel());
                permissionsList.add(UarPermissions.READ_ONLY.getLabel());
            }
            else if (JobRole.ASSISTANT.equals(jobRole) || JobRole.PARAPLANNER.equals(jobRole) ) {
                permissionsList.add(UarPermissions.CAN_TRAN.getLabel());
                permissionsList.add(UarPermissions.CAN_NOT_TRAN.getLabel());
                permissionsList.add(UarPermissions.READ_ONLY.getLabel());
            }
            uarPermissionDto.setPermissionValues(permissionsList);
            uarPermissionDtoList.add(uarPermissionDto);
        }
        return uarPermissionDtoList;
    }

    private String getNameWithoutPosition(String name) {
        String nameWithoutPos=name;
        if (name.indexOf('(') > -1 && name.contains("OE"))
            nameWithoutPos = name.substring(0,name.indexOf('(')).replaceFirst("OE ","");
        else if (name.indexOf('(') > -1)
            nameWithoutPos = name.substring(0, name.indexOf('('));
        else if(name.contains("OE"))
            nameWithoutPos = name.replaceFirst("OE ", "");

        return nameWithoutPos;
    }

    private JobProfileIdentifier getJobProfileIdentifier(final String jobId){
        JobProfileIdentifier jobProfileIdentifier = new JobProfileIdentifier() {
            @Override
            public JobKey getJob() {
                return JobKey.valueOf(jobId);
            }

            @Override
            public String getProfileId() {
                return null;
            }
        };
        return jobProfileIdentifier;
    }
}
