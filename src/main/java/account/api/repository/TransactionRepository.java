package account.api.repository;

import account.api.domain.Transaction;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface TransactionRepository {
    Transaction save(@NotNull Transaction transaction);

    Transaction update(@NotNull Transaction transaction);

    List<Transaction> findByAccountId(@NotNull long accountId);
}
