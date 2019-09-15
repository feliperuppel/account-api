package account.api.repository;

import account.api.domain.Transaction;
import io.micronaut.spring.tx.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.List;

@Singleton
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final String QUERY_HISTORY = "SELECT t FROM Transaction as t where t.fromAccount = :id or t.toAccount = :id order by t.updateTimeStamp desc";

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public TransactionRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Transaction save(@NotNull Transaction transaction) {
        entityManager.persist(transaction);
        return transaction;
    }

    @Override
    @Transactional
    public Transaction update(@NotNull Transaction transaction) {
        return entityManager.merge(transaction);
    }

    @Override
    @Transactional
    public List<Transaction> findByAccountId(@NotNull long accountId) {
        return entityManager.createQuery(QUERY_HISTORY, Transaction.class)
                .setParameter("id", accountId)
                .getResultList();
    }
}
