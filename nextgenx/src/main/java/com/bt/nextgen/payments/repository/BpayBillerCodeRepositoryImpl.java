package com.bt.nextgen.payments.repository;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;

@Repository
public class BpayBillerCodeRepositoryImpl implements BpayBillerCodeRepository {
    private static final Logger logger = LoggerFactory.getLogger(BpayBillerCodeRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Collection<BpayBiller> findByPartialBillerCode(final String partialBillerCode) {
        logger.info("Searching for BpayBiller with BillerCode like {}", partialBillerCode);
        Query query = entityManager.createQuery("from BpayBiller where billerCode like :billerCode || '%'");
        //Cash 5
        //query.setParameter("billerCode", partialBillerCode);
        query.setParameter("billerCode", '%' + partialBillerCode + '%');
        return (Collection<BpayBiller>) query.getResultList();
    }

    @Override
    public BpayBiller load(String billerCode) {
        logger.info("Loading the BpayBiller with BillerCode {}", billerCode);

        //Cash 5
        //BpayBiller bpayBiller = entityManager.find(BpayBiller.class, billerCode, 10, '0');
        //StringUtil.leftPad was present in Cash 4
        BpayBiller bpayBiller = entityManager.find(BpayBiller.class, StringUtils.leftPad(billerCode, 10, '0'));
        return bpayBiller;
    }

    @Override
    public Collection<BpayBiller> loadAllBillers() {
        Query query = entityManager.createQuery("from BpayBiller");

        return (Collection<BpayBiller>) query.getResultList();
    }
}
