package account.api.repository;

import account.api.domain.Account;
import io.micronaut.spring.tx.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Singleton
public class AccountRepositoryImpl implements AccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(AccountRepositoryImpl.class);

    private static final String QUERY_ALL = "SELECT a FROM Account as a";

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public AccountRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Account save() {
        Account acct = new Account();
        entityManager.persist(acct);
        return acct;
    }

    @Override
    @Transactional
    public Account save(Account account) {
        entityManager.merge(account);
        return account;
    }

    @Override
    @Transactional
    public Account save(Account from, Account to) {
        entityManager.merge(from);
        entityManager.merge(to);
        return from;
    }

    @Override
    @Transactional
    public Optional<Account> findById(@NotNull long accountId) {
        return Optional.ofNullable(entityManager.find(Account.class, accountId));
    }

    @Override
    @Transactional
    public List<Account> findAll() {
        TypedQuery<Account> query = entityManager.createQuery(QUERY_ALL, Account.class);
        return query.getResultList();
    }
}
