package com.bt.nextgen.service.integration.options.repository;

import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.OptionType;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.model.OptionValueKey;
import com.bt.nextgen.service.integration.options.model.StringOptionValueImpl;
import com.bt.nextgen.service.integration.options.model.ToggleOptionValueImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.Collection;

@Repository("OptionValueRepository")
public class OptionValueRepositoryImpl implements OptionValueRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OptionValue<Boolean> findToggleOptionValue(OptionValueKey key) {
        return entityManager.find(ToggleOptionValueImpl.class, key);
    }

    @Override
    public OptionValue<String> findStringOptionValue(OptionValueKey key) {
        return entityManager.find(StringOptionValueImpl.class, key);
    }

    @Override
    public Collection<OptionValue<Boolean>> searchToggleOptions(OptionType optionType, CategoryKey categoryKey) {
        String queryStr = "SELECT v FROM ToggleOptionValueImpl v , ToggleOptionImpl o "
                + "where v.optionValueKey.categoryType = :categoryType " + "and v.optionValueKey.categoryId = :categoryId "
                + "and v.optionValueKey.optionName = o.optionKey.optionName " + "and o.optionType = :optionType ";
        TypedQuery<OptionValue<Boolean>> query = (TypedQuery<OptionValue<Boolean>>) (TypedQuery<?>) entityManager
                .createQuery(queryStr, ToggleOptionValueImpl.class);
        return query.setParameter("categoryType", categoryKey.getCategory())
                .setParameter("categoryId", categoryKey.getCategoryId()).setParameter("optionType", optionType).getResultList();
    }

    @Override
    public Collection<OptionValue<String>> searchStringOptions(OptionType optionType, CategoryKey categoryKey) {
        String queryStr = "SELECT v FROM StringOptionValueImpl v , StringOptionImpl o "
                + "where v.optionValueKey.categoryType = :categoryType " + "and v.optionValueKey.categoryId = :categoryId "
                + "and v.optionValueKey.optionName = o.optionKey.optionName " + "and o.optionType = :optionType ";
        TypedQuery<OptionValue<String>> query = (TypedQuery<OptionValue<String>>) (TypedQuery<?>) entityManager
                .createQuery(queryStr, StringOptionValueImpl.class);
        return query.setParameter("categoryType", categoryKey.getCategory())
                .setParameter("categoryId", categoryKey.getCategoryId()).setParameter("optionType", optionType).getResultList();
    }

}