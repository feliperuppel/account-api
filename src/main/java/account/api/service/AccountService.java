package account.api.service;

import account.api.domain.Account;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    void deposit( @NotNull final Long id, @Positive final BigDecimal amount);
    void withdraw( @NotNull final Long id, @Positive final BigDecimal amount);
    void transfer( @NotNull final Long from, final @NotNull Long to, @Positive final BigDecimal amount);
    Account createAccount();
    Account getAccount(final @NotNull Long id);
    List<Account> listAccounts();
}
