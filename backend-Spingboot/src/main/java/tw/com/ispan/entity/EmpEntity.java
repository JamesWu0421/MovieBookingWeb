package tw.com.ispan.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "emp_name", nullable = false, length = 50)
    private String empName;

    @Column(name = "emp_password_hash", nullable = false, length = 255)
    private String empPasswordHash;

    @Column(name = "emp_phone", length = 15)
    private String empPhone;

    @Column(name = "emp_email", length = 100)
    private String empEmail;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Byte status;
    // 0: 帳號停用, 1: 帳號啟用 , 2: 鎖住
    @ManyToMany
    @JoinTable(
        name = "employee_roles",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
        private Set<RoleEntity> roles;

    @Override
    public String toString() {
        return "EmployeeEntity [id=" + id + ", empName=" + empName + ", empPhone=" + empPhone + ", empEmail=" + empEmail
                + ", createdAt=" + createdAt + ", status=" + status + "]";
    }
    
}

