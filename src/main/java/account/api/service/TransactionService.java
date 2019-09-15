package account.api.service;

import account.api.domain.Transaction;

import java.util.List;

public interface TransactionService {
    void register(Transaction t);

    boolean hasElements();

    Transaction getNext();

    List<Transaction> getHistory(Long accountId);
}
