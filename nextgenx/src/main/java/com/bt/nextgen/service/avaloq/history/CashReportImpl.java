package com.bt.nextgen.service.avaloq.history;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.history.CashRateComponent;
import com.bt.nextgen.service.integration.history.CashReport;
import com.bt.nextgen.service.integration.history.InterestDate;

@ServiceBean(xpath = "report_foot_list", type = ServiceBeanType.CONCRETE)
public class CashReportImpl implements CashReport {
        
        private static final Logger logger = LoggerFactory.getLogger(CashReportImpl.class);
        
        private AssetKey assetKey;
        @ServiceElement(xpath = "report_foot/asset_id/val")
        private String assetId;
        @ServiceElement(xpath = "report_foot/asset_id/annot/displ_text")
        private String assetName;
        @ServiceElement(xpath = "report_foot/compo_type_id/val")
        private String componentTypeId;
        @ServiceElement(xpath = "report_foot/compo_type_id/annot/displ_text")
        private String componentTypeName;
        @ServiceElement(xpath = "report_foot/base_contri_list", type = CashRateComponentImpl.class)
        private CashRateComponent baseCashRateComponent;
        @ServiceElement(xpath = "report_foot/marg_contri_list", type = CashRateComponentImpl.class)
        private CashRateComponent marginCashRateComponent;
        @ServiceElement(xpath = "report_foot/spec_contri_list", type = CashRateComponentImpl.class)
        private CashRateComponent specialCashRateComponent;
        
        public BigDecimal getBaseRate()
        {
                DateTime recentDate = baseCashRateComponent.getInterestDates().get(0).getEffectiveDate();
                BigDecimal currentRate = baseCashRateComponent.getInterestDates().get(0).getInterestRate();
                for (InterestDate interestDate : baseCashRateComponent.getInterestDates())
                {
                        DateTime tempDate = interestDate.getEffectiveDate();
                        if(tempDate.isAfter(recentDate))
                        {
                                recentDate = tempDate;
                                currentRate = interestDate.getInterestRate();
                        }
                }
                return currentRate;
        }
        
        public BigDecimal getCurrentRate()
        {
                BigDecimal marginComponent = marginCashRateComponent !=null ? marginCashRateComponent.getSummatedRate() : new BigDecimal(0.0);
                BigDecimal specialComponent = specialCashRateComponent !=null ? specialCashRateComponent.getSummatedRate() : new BigDecimal(0.0);
                
                return getBaseRate().add(marginComponent).add(specialComponent);
        }
        
        public AssetKey getAssetKey()
        {
                if(assetKey==null)
                        assetKey = AssetKey.valueOf(assetId);
                return assetKey;
        }
        
        public void setAssetId(String assetId) {
                this.assetId = assetId;
        }
        public String getAssetName() {
                return assetName;
        }
        public void setAssetName(String assetName) {
                this.assetName = assetName;
        }
        public String getComponentTypeId() {
                return componentTypeId;
        }
        public void setComponentTypeId(String componentTypeId) {
                this.componentTypeId = componentTypeId;
        }
        public String getComponentTypeName() {
                return componentTypeName;
        }
        public void setComponentTypeName(String componentTypeName) {
                this.componentTypeName = componentTypeName;
        }
        public CashRateComponent getBaseCashRateComponent() {
                return baseCashRateComponent;
        }
        public void setBaseCashRateComponent(CashRateComponent baseCashRateComponent) {
                this.baseCashRateComponent = baseCashRateComponent;
        }
        public CashRateComponent getMarginCashRateComponent() {
                return marginCashRateComponent;
        }
        public void setMarginCashRateComponent(CashRateComponent marginCashRateComponent) {
                this.marginCashRateComponent = marginCashRateComponent;
        }
        public CashRateComponent getSpecialCashRateComponent() {
                return specialCashRateComponent;
        }
        public void setSpecialCashRateComponent(CashRateComponent specialCashRateComponent) {
                this.specialCashRateComponent = specialCashRateComponent;
        }

        @Override
        public List<InterestDate> getInterestRates() {
                
                List<InterestDate> interestRates = new ArrayList<>();
                Map<DateTime, BigDecimal> baseContributionMap = new LinkedHashMap<>();
                Map<DateTime, BigDecimal> marginContributionMap = new LinkedHashMap<>();
                Map<DateTime, BigDecimal> specialContributionMap = new LinkedHashMap<>();
                Set<DateTime> dates = new LinkedHashSet<DateTime>();
                Set<DateTime> sortedDates = new TreeSet<DateTime>();
                
                if(baseCashRateComponent != null && baseCashRateComponent.getInterestDates() != null)
                {
                        for(InterestDate interestDate : baseCashRateComponent.getInterestDates())
                        {
                                logger.info("CashReportImpl.getInterestRates().baseCashRate ::: "+interestDate.getEffectiveDate());
                                if(interestDate.getEffectiveDate() != null)
                                {
                                        baseContributionMap.put(interestDate.getEffectiveDate(), interestDate.getInterestRate());
                                        dates.add(interestDate.getEffectiveDate());
                                }
                        }
                }
                
                if(marginCashRateComponent != null && marginCashRateComponent.getInterestDates() != null)
                {
                        for(InterestDate interestDate : marginCashRateComponent.getInterestDates())
                        {
                                logger.info("CashReportImpl.getInterestRates().marginCashRate ::: "+interestDate.getEffectiveDate());
                                if(interestDate.getEffectiveDate() != null)
                                {
                                        marginContributionMap.put(interestDate.getEffectiveDate(), interestDate.getInterestRate());
                                        dates.add(interestDate.getEffectiveDate());
                                }
                        }
                }
                
                if(specialCashRateComponent != null && specialCashRateComponent.getInterestDates() != null)
                {
                        for(InterestDate interestDate : specialCashRateComponent.getInterestDates())
                        {
                                if(interestDate.getEffectiveDate() != null)
                                {
                                        specialContributionMap.put(interestDate.getEffectiveDate(), interestDate.getInterestRate());
                                        dates.add(interestDate.getEffectiveDate());
                                }
                        }
                }
        
                
                
                BigDecimal lastBaseRate = new BigDecimal(0.0);
                BigDecimal lastMarginRate = new BigDecimal(0.0);
                BigDecimal lastSpecialRate = new BigDecimal(0.0);
                sortedDates.addAll(dates);
                for(DateTime date : sortedDates)
                {
                        if(baseContributionMap.get(date) != null)
                        {
                                lastBaseRate = baseContributionMap.get(date);
                                baseContributionMap.put(date, baseContributionMap.get(date));
                        }
                        else
                        {
                                baseContributionMap.put(date, lastBaseRate);
                        }
                        if(marginContributionMap.get(date) != null)
                        {
                                lastMarginRate = marginContributionMap.get(date);
                                marginContributionMap.put(date, marginContributionMap.get(date));
                        }
                        else
                        {
                                marginContributionMap.put(date, lastMarginRate);
                        }
                        if(specialContributionMap.get(date) != null)
                        {
                                lastSpecialRate = specialContributionMap.get(date);
                                specialContributionMap.put(date, specialContributionMap.get(date));
                        }
                        else
                        {
                                specialContributionMap.put(date, lastSpecialRate);
                        }
                }
                
                for(DateTime date : sortedDates)
                {
                        InterestDateImpl interestDate = new InterestDateImpl();
                        if(baseContributionMap.get(date) != null || marginContributionMap.get(date) != null || specialContributionMap.get(date) != null)
                        {
                                BigDecimal baseComponent = baseContributionMap.get(date) !=null 
                                                                                 ? baseContributionMap.get(date) 
                                                                             : new BigDecimal(0.0);
                                                                                 
                                                                         
                                BigDecimal marginComponent = marginContributionMap.get(date) !=null 
                                                                                   ? marginContributionMap.get(date) 
                                                                                   : new BigDecimal(0.0);
                                                                                                 
                                BigDecimal specialComponent = specialContributionMap.get(date) !=null 
                                                                                        ? specialContributionMap.get(date) 
                                                                                        : new BigDecimal(0.0);
                                logger.info("CashReportImpl.getInterestRates() baseComponent : "+baseComponent+", marginComponent : "+marginComponent
                                                +", specialComponent : "+specialComponent);                                             
                                interestDate.setEffectiveDate(date);
                                interestDate.setInterestRate(baseComponent.add(marginComponent).add(specialComponent));
                                interestRates.add(interestDate);
                        }
                }
                
                return interestRates;
        }
}
