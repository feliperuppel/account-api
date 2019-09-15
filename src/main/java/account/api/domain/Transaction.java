package account.api.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "TRANSACTION")
public final class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "AMOUNT")
    private BigDecimal amount;
    @Column(name = "FROM_ACCOUNT")
    private Long fromAccount;
    @Column(name = "TO_ACCOUNT")
    private Long toAccount;
    @Column(name = "OPERATION")
    private Operation operation;
    @CreationTimestamp
    @Column(name = "CREATION_TIMESTAMP")
    private LocalDateTime creationTimeStamp;
    @UpdateTimestamp
    @Column(name = "UPDATE_TIMESTAMP")
    private LocalDateTime updateTimeStamp;
    @Column(name = "STATUS")
    private Status status = Status.NEW;
    @Column(name = "FAILURE_REASON")
    private String failureReason;

    public enum Status {
        NEW,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    public enum Operation {
        DEPOSIT,
        WITHDRAW,
        TRANSFER
    }
}
