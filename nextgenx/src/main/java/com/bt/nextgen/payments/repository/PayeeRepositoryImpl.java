package com.bt.nextgen.payments.repository;

import com.bt.nextgen.payments.domain.CRNType;
import com.bt.nextgen.payments.domain.PayeeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class PayeeRepositoryImpl implements PayeeRepository
{
	private static final Logger logger = LoggerFactory.getLogger(PayeeRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional(value = "springJpaTransactionManager")
	public void save(Payee payee)
	{
		entityManager.persist(payee);
	}

	public Payee load(Long id)
	{
		logger.info("Loading payee for Id {}", id);
		Payee payee = entityManager.find(Payee.class, id);
		setCrnType(payee);
		return payee;
	}

	@Override
	public List<Payee> loadAll(String cashAccountId)
	{
		logger.info("Loading payees for cashAccout {}", cashAccountId);
		Query query = entityManager.createQuery("from Payee where cashAccountId=:cashAccountId");
		query.setParameter("cashAccountId", cashAccountId);
		List<Payee> payeeList = (List<Payee>) query.getResultList();

		for (Payee payee : payeeList)
		{
			setCrnType(payee);
		}

		return payeeList;
	}

	@Transactional(value = "springJpaTransactionManager")
	public void delete(Long id)
	{
		logger.info("Deleting payee with Id {}", id);
		entityManager.remove(load(id));
	}

	public Payee find(Payee payee)
	{
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Payee> criteriaQuery = criteriaBuilder.createQuery(Payee.class);
		Root from;

		if (payee instanceof BpayPayee)
		{
			BpayPayee bpayPayee = (BpayPayee) payee;
			from = criteriaQuery.from(BpayPayee.class);
			Join<BpayPayee, BpayBiller> biller = from.join("biller");

			criteriaQuery.where(criteriaBuilder.equal(from.get("cashAccountId"), bpayPayee.getCashAccountId()),
				criteriaBuilder.equal(biller.get("billerCode"), bpayPayee.getBiller().getBillerCode()),
				criteriaBuilder.equal(from.get("customerReference"), bpayPayee.getCustomerReference()));
		}
		else if (payee instanceof PayAnyonePayee)
		{
			PayAnyonePayee payAnyone = (PayAnyonePayee) payee;
			from = criteriaQuery.from(PayAnyonePayee.class);
			Join<PayAnyonePayee, Bsb> bsb = from.join("bsb");

			criteriaQuery.where(criteriaBuilder.equal(from.get("cashAccountId"), payAnyone.getCashAccountId()),
				criteriaBuilder.equal(bsb.get("bsbCode"), payAnyone.getBsb().getBsbCode()),
				criteriaBuilder.equal(from.get("accountNumber"), payAnyone.getAccountNumber()));
		}
		else
		{
			LinkedAccount linkedPayee = (LinkedAccount) payee;
			from = criteriaQuery.from(LinkedAccount.class);
			Join<LinkedAccount, Bsb> bsb = from.join("bsb");

			criteriaQuery.where(criteriaBuilder.equal(from.get("cashAccountId"), linkedPayee.getCashAccountId()),
				criteriaBuilder.equal(bsb.get("bsbCode"), linkedPayee.getBsb().getBsbCode()),
				criteriaBuilder.equal(from.get("accountNumber"), linkedPayee.getAccountNumber()));
		}


		CriteriaQuery<Payee> select = criteriaQuery.select(from);
		TypedQuery<Payee> typedQuery = entityManager.createQuery(select);
		List<Payee> resultList = typedQuery.getResultList();

		if (resultList.size() >= 1)
		{
			logger.info("Returning the existing Payee");
			return resultList.iterator().next();
		}

		logger.info("No matching Payee found");
		return null;
	}

	private void setCrnType(Payee payee)
	{
		if (payee != null && payee.getPayeeType() != null && payee.getPayeeType() == PayeeType.BPAY)
		{
			BpayPayee bpayPayee = (BpayPayee) payee;
			//TODO: setting up CRNType for testing purpose. in future it will come from database
			switch (bpayPayee.getName())
			{
				case "RACWA HOLDINGS PTY LTD":
					bpayPayee.setCrnType(CRNType.ICRN);
					break;
				case "WOOLWORTHS CREDIT CARD":
					bpayPayee.setCrnType(CRNType.VCRN);
					break;
				default:
					bpayPayee.setCrnType(CRNType.CRN);
			}
		}
	}

	@Override
	public List<PayAnyonePayee> loadAllPayanyone(String cashAccountId)
	{
		logger.info("Loading PayAnyonePayees for cashAccount {}", cashAccountId);
		Query query = entityManager.createQuery("from Payee where cashAccountId=:arg1 and payeeType=:arg2");
		query.setParameter("arg1", cashAccountId);
		query.setParameter("arg2", PayeeType.PAY_ANYONE);
		List<PayAnyonePayee> payeeList = query.getResultList();
		return payeeList;

	}

	@Override
	public PayAnyonePayee findPayanyone(String cashAccountId, String accountNumber)
	{
		logger.info("Finding PayAnyonePayee {} {}", cashAccountId, accountNumber);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(Payee.class);

		Root from = criteriaQuery.from(PayAnyonePayee.class);

		criteriaQuery.where(criteriaBuilder.equal(from.get("cashAccountId"), cashAccountId),
			criteriaBuilder.equal(from.get("accountNumber"), accountNumber));
		PayAnyonePayee account = null;
		try
		{
			CriteriaQuery<PayAnyonePayee> select = criteriaQuery.select(from);
			TypedQuery<PayAnyonePayee> typedQuery = entityManager.createQuery(select);
			account = typedQuery.getSingleResult();
		}
		catch (NoResultException e)
		{
			logger.info("Payanyone Account not found");
		}
		return account;
	}
	
	//Changes as the part of defect:789
	@Override
	public PayAnyonePayee findPayanyone(Bsb bsb, String accountNumber)
	{
		logger.info("Finding PayAnyonePayee {} {}", bsb, accountNumber);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(Payee.class);

		Root from = criteriaQuery.from(PayAnyonePayee.class);

		criteriaQuery.where(criteriaBuilder.equal(from.get("bsb"), bsb),
			criteriaBuilder.equal(from.get("accountNumber"), accountNumber));
		PayAnyonePayee account = null;
		try
		{
			CriteriaQuery<PayAnyonePayee> select = criteriaQuery.select(from);
			TypedQuery<PayAnyonePayee> typedQuery = entityManager.createQuery(select);
			account = typedQuery.getSingleResult();
		}
		catch (NoResultException e)
		{
			logger.info("Payanyone Account not found");
		}
		return account;
	}

	@Override @Transactional(value = "springJpaTransactionManager")
	public int deletePayAnyone(PayAnyonePayee payee)
	{
		logger.info("Deleting the PayAnyonePayee {}", payee.getAccountNumber());
		int removeCount = 0;
		PayAnyonePayee account = findPayanyone(payee.getCashAccountId(), payee.getAccountNumber());
		if (account != null)
		{
			entityManager.remove(account);
			removeCount = 1;
		}
		return removeCount;
	}

	@Override @Transactional(value = "springJpaTransactionManager")
	public int updatePayanyone(PayAnyonePayee payee)
	{
		logger.info("Updating PayAnyonePayee: {} nickname: {}", payee.getAccountNumber(), payee.getNickname());
		int updateCount = 0;
		PayAnyonePayee account = findPayanyone(payee.getCashAccountId(), payee.getAccountNumber());
		if (account != null)
		{
			account.setNickname(payee.getNickname());
			save(account);
			updateCount = 1;
		}
		return updateCount;
	}

	@Transactional(value = "springJpaTransactionManager")
	public Payee update(Long payeeId, String nickname)
	{
		logger.info("Updating payee: {} nickname: {}", payeeId, nickname);
		Payee payee = entityManager.find(Payee.class, payeeId);
		payee.setNickname(nickname);
		save(payee);
		setCrnType(payee);
		return payee;
	}

	@Override
	public List<LinkedAccount> loadAllLinkedAccount(String cashAccountId)
	{

		logger.info("Loading all liked accounts, cashAccountIs: {} ", cashAccountId);
		Query query = entityManager.createQuery(
			"from Payee where cashAccountId=:arg1 and payeeType=:arg2 or payeeType=:arg3");
		query.setParameter("arg1", cashAccountId);
		query.setParameter("arg2", PayeeType.PRIMARY_LINKED);
		query.setParameter("arg3", PayeeType.SECONDARY_LINKED);
		List<LinkedAccount> linkedAccountList = query.getResultList();
		return linkedAccountList;
	}

	@Override @Transactional(value = "springJpaTransactionManager")
	public Payee updateLinkedAccountType(String cashAccountId, String payeeId)
	{

		logger.debug("Start Of Method :updateLinkedAccountType");
		Payee payee = null;
		Query query = entityManager.createQuery(
			"update Payee set payeeType=:arg1 where cashAccountId=:arg2 and payeeType=:arg3");
		query.setParameter("arg1", PayeeType.SECONDARY_LINKED);
		query.setParameter("arg2", cashAccountId);
		query.setParameter("arg3", PayeeType.PRIMARY_LINKED);
		int updateCount = query.executeUpdate();
		//if(updateCount != 0)
		//{
		payee = entityManager.find(Payee.class, Long.parseLong(payeeId));
		payee.setPayeeType(PayeeType.PRIMARY_LINKED);
		save(payee);
		//}	
		return payee;
	}

}
