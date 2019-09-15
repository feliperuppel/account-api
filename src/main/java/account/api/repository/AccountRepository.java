package account.api.repository;

import account.api.domain.Account;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save();
    Account save(@NotNull Account account);
    Account save(@NotNull Account from, @NotNull Account to);
    Optional<Account> findById(@NotNull long accountId);
    List<Account> findAll();
}
