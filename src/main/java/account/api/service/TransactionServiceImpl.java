package account.api.service;

import account.api.domain.Transaction;
import account.api.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Singleton
public class TransactionServiceImpl implements TransactionService {

    private ConcurrentLinkedQueue<Transaction> queue;
    private TransactionRepository repository;

    public TransactionServiceImpl(TransactionRepository repository) {
        this.repository = repository;
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void register(Transaction t) {
        repository.save(t);
        queue.add(t);
        log.info("Transaction added to the queue : {}", t);
    }

    @Override
    public boolean hasElements() {
        return !queue.isEmpty();
    }

    @Override
    public Transaction getNext() {
        return queue.poll();
    }

    @Override
    public List<Transaction> getHistory(final Long id) {
        return repository.findByAccountId(id);
    }
}
