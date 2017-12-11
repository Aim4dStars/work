package com.bt.nextgen.api.subscriptions.util;


import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.draftaccount.service.OrderType;
import com.bt.nextgen.api.subscriptions.model.Offer;
import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.api.subscriptions.service.FAWorkFlowFunction;
import com.bt.nextgen.api.subscriptions.service.Subscriptions;
import com.bt.nextgen.core.Function;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationIdentifierImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;

import java.util.EnumMap;
import java.util.Map;

public final class Converters {

    private static Converters converters = null;

    private static Map<OrderType, Function<SubscriptionDto, ApplicationDocument, SubscriptionDto>> subscriptionWorkflowMap =
            new EnumMap<>(OrderType.class);

    static {
        subscriptionWorkflowMap.put(OrderType.FundAdmin, fundAdminWorkflow());
        subscriptionWorkflowMap.put(OrderType.Default, defaultWorkflow());
    }

    private Converters() {
    }

    public static synchronized Converters getInstance() {
        if (converters == null) {
            converters = new Converters();
        }
        return converters;
    }

    public static WrapAccountIdentifier convertAccountId(AccountKey key) {
        String accountId = new EncodedString(key.getAccountId()).plainText();
        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(accountId);
        return wrapAccountIdentifier;
    }

    public static com.bt.nextgen.service.integration.account.AccountKey convertAccountId(String id) {
        return com.bt.nextgen.service.integration.account.AccountKey.valueOf(id);
    }

    public static Converter<String, ApplicationIdentifier> appIdCovnerter() {
        return new Converter<String, ApplicationIdentifier>() {
            @Override
            public ApplicationIdentifier convert(String from) {
                ApplicationIdentifier applicationIdentifier = new ApplicationIdentifierImpl();
                applicationIdentifier.setDocId(from);
                return applicationIdentifier;
            }
        };
    }

    public static Converter<Offer, SubscriptionDto> dtoConverterFromOffer() {
        return new Converter<Offer, SubscriptionDto>() {
            @Override
            public SubscriptionDto convert(Offer from) {
                SubscriptionDto dto = new SubscriptionDto();
                dto.setServiceName(from.getType().getName());
                dto.setServiceType(from.getType().name());
                return dto;
            }
        };
    }

    public static ApplicationDocument toSubscriptionDetail(SubscriptionDto subscriptionDto) {
        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setBpid(convertAccountId(convertAccountId(subscriptionDto.getKey()).getAccountIdentifier()));
        Subscriptions service = Subscriptions.valueOf(subscriptionDto.getServiceType());
        applicationDocument.setOrderType(service.getWorkFlowType());
        return applicationDocument;
    }

    public static SubscriptionDto setWorkFlow(ApplicationDocument application) {
        return setWorkFlow(application, getWorkFlowFunction(application.getOrderType()));
    }

    public static SubscriptionDto setWorkFlow(ApplicationDocument application, Function<SubscriptionDto, ApplicationDocument, SubscriptionDto> workFlow) {
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        Offer offer = Subscriptions.getOfferFromOderType(application.getOrderType());
        subscriptionDto.setOrderNumber(application.getAppNumber());
        //TODO: get the type from the offer not from Subscriptions
        subscriptionDto.setServiceType(offer.getType().name());
        subscriptionDto.setServiceName(offer.getName());
        subscriptionDto.setSubmitDate(new DateTime(application.getAppSubmitDate()));
        subscriptionDto.setKey(new AccountKey(EncodedString.fromPlainText(application.getBpid().getId()).toString()));
        return workFlow.apply(subscriptionDto, application);
    }

    public static synchronized Function<SubscriptionDto, ApplicationDocument, SubscriptionDto> getWorkFlowFunction(String type) {
        return subscriptionWorkflowMap.get(OrderType.orderOf(type));
    }

    public static Function<SubscriptionDto, ApplicationDocument, SubscriptionDto> fundAdminWorkflow() {
        return new FAWorkFlowFunction();
    }

    public static Function<SubscriptionDto, ApplicationDocument, SubscriptionDto> defaultWorkflow() {
        return new Function<SubscriptionDto, ApplicationDocument, SubscriptionDto>() {
            @Override
            public SubscriptionDto apply(SubscriptionDto subscriptionDto, ApplicationDocument document) {
                return subscriptionDto;
            }

            @Override
            public String toString() {
                return "DefaultWorkflowFunction";
            }
        };
    }
}
