package account.api.service;

import account.api.domain.Account;
import account.api.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Singleton
@Slf4j
public class AccountServiceImpl implements AccountService {

    private AccountRepository repository;

    @Inject
    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deposit(@NotNull final Long id, @Positive final BigDecimal amount) {
        final Account acct = getAccount(id);
        addFunds(acct, amount);
        repository.save(acct);
    }

    @Override
    public void withdraw(@NotNull final Long id, @Positive final BigDecimal amount) {
        Account acct = getAccount(id);
        subtractFunds(acct, amount);
        repository.save(acct);
    }

    @Override
    public void transfer(@NotNull final Long from, @NotNull final Long to,  @Positive final BigDecimal amount) {
        if (from.equals(to)) {
            log.info("Igonring attempt to trasnfer money to the same account - From Account ID {} and To Account ID {} ", from, to);
            return;
        }
        Account fromAcct = getAccount(from);
        Account toAcct = getAccount(to);
        subtractFunds(fromAcct, amount);
        addFunds(toAcct, amount);
        repository.save(fromAcct, toAcct);
    }

    @Override
    public Account createAccount() {
        return repository.save();
    }

    @Override
    public Account getAccount(@NotNull final Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException(String.format("Account ID %s Not Found", id)));
    }

    @Override
    public List<Account> listAccounts() {
        return repository.findAll();
    }

    private void subtractFunds(Account acct, @Positive BigDecimal amount){
        if (acct.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException(String.format("Insuficient funds for Account ID %s: %s available", acct.getId(), acct.getBalance()));
        }
        acct.setBalance(acct.getBalance().subtract(amount));
    }

    private void addFunds(Account acct, @Positive BigDecimal amount){
        acct.setBalance(acct.getBalance().add(amount));
    }
}