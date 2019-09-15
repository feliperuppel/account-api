package account.api.domain;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ACCOUNT")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "BALANCE")
    @ColumnDefault("0.00")
    private BigDecimal balance = BigDecimal.ZERO;
    @CreationTimestamp
    @Column(name = "CREATION_TIMESTAMP")
    private LocalDateTime creationTimeStamp;
}
