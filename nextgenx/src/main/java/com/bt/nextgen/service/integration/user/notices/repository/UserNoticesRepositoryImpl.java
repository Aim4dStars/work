package com.bt.nextgen.service.integration.user.notices.repository;

import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.user.notices.model.UserNotices;
import com.bt.nextgen.service.integration.user.notices.model.UserNoticesKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("UserNoticesRepository")
public class UserNoticesRepositoryImpl implements UserNoticesRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserNotices> search(UserKey userKey) {
        TypedQuery<UserNotices> query = entityManager.createQuery(
                "SELECT u FROM UserNotices u WHERE u.userNoticesKey.userId in :id", UserNotices.class);
        return query.setParameter("id", userKey.getId()).getResultList();
    }

    @Override
    public UserNotices find(UserNoticesKey key) {
        return entityManager.find(UserNotices.class, key);
    }

    @Override
    @Transactional(value = "springJpaTransactionManager")
    public UserNotices save(UserNotices userNotices) {
        final UserNotices userUpdate = entityManager.merge(userNotices);
        entityManager.flush();
        return userUpdate;
    }
}
