package tw.com.ispan.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emp_audit_logs")
@Data
@NoArgsConstructor

public class EmpAuditlogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmpEntity employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "query_params", columnDefinition = "NVARCHAR(MAX)")
    private String queryParams;

    @Column(name = "access_time", nullable = false)
    private LocalDateTime accessTime;

    @Override
    public String toString() {
        return "EmpAuditlogEntity [logId=" + logId + ", employee=" + employee + ", role=" + role + ", actionType="
                + actionType + ", queryParams=" + queryParams + ", accessTime=" + accessTime + "]";
    }
} 
