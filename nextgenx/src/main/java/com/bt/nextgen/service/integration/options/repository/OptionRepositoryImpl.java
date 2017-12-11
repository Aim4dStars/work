package com.bt.nextgen.service.integration.options.repository;

import com.bt.nextgen.service.integration.options.model.Option;
import com.bt.nextgen.service.integration.options.model.OptionType;
import com.bt.nextgen.service.integration.options.model.StringOptionImpl;
import com.bt.nextgen.service.integration.options.model.ToggleOptionImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.Collection;

@Repository("OptionRepository")
public class OptionRepositoryImpl implements OptionRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Collection<Option<Boolean>> searchToggleOptions(OptionType optionType) {
        String queryStr = "SELECT o FROM ToggleOptionImpl o where o.optionType = :optionType";
        TypedQuery<Option<Boolean>> query = (TypedQuery<Option<Boolean>>) (TypedQuery<?>) entityManager.createQuery(queryStr,
                ToggleOptionImpl.class);
        return query.setParameter("optionType", optionType).getResultList();
    }

    @Override
    public Collection<Option<String>> searchStringOptions(OptionType optionType) {
        String queryStr = "SELECT o FROM StringOptionImpl o where o.optionType = :optionType";
        TypedQuery<Option<String>> query = (TypedQuery<Option<String>>) (TypedQuery<?>) entityManager.createQuery(queryStr,
                StringOptionImpl.class);
        return query.setParameter("optionType", optionType).getResultList();
    }
}