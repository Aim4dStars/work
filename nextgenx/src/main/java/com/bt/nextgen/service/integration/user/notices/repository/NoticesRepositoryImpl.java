package com.bt.nextgen.service.integration.user.notices.repository;

import com.bt.nextgen.service.integration.user.notices.model.Notices;
import com.bt.nextgen.service.integration.user.notices.model.NoticesKey;
import com.bt.nextgen.service.integration.user.notices.model.NoticeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("NoticesRepository")
public class NoticesRepositoryImpl implements NoticesRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Notices> findAll() {
        TypedQuery<Notices> query = entityManager.createQuery("SELECT u FROM Notices u", Notices.class);
        return query.getResultList();
    }

    @Override
    public Notices find(NoticesKey noticesKey) {
        return entityManager.find(Notices.class, noticesKey);
    }

    @Override
    public Map<NoticeType, Notices> getLatestUpdatesMap() {
        final List<Notices> availableUpdates = findAll();
        Map<NoticeType, Notices> updatesTypeUpdatesMap = new HashMap<>();
        for (Notices update : availableUpdates) {
            Notices existingUpdate = updatesTypeUpdatesMap.get(update.getNoticesKey().getNoticeTypeId());
            if (existingUpdate == null) {
                updatesTypeUpdatesMap.put(update.getNoticesKey().getNoticeTypeId(), update);
            } else {
                if (update.getNoticesKey().getVersion() > existingUpdate.getNoticesKey().getVersion()) {
                    updatesTypeUpdatesMap.put(update.getNoticesKey().getNoticeTypeId(), update);
                }
            }
        }
        return updatesTypeUpdatesMap;
    }

    @Override
    @Transactional(value = "springJpaTransactionManager")
    public Notices save(Notices newUpdate) {
        final Notices result = entityManager.merge(newUpdate);
        entityManager.flush();
        return result;
    }
}
