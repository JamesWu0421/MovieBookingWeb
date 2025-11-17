package tw.com.ispan.entity;

import java.time.LocalDateTime;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "emp_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private EmpEntity employee;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "query_params", columnDefinition = "NVARCHAR(MAX)")
    private String queryParams;

    @Column(name = "access_time", nullable = false)
    private LocalDateTime accessTime = LocalDateTime.now();

    @Override
    public String toString() {
        return "EmployeeLogEntity [logId=" + logId + ", employee=" + employee + ", role=" + role + ", actionType="
                + actionType + ", queryParams=" + queryParams + ", accessTime=" + accessTime + "]";
    }
}

