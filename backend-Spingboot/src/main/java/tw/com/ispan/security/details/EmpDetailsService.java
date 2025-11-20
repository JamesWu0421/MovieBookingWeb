package tw.com.ispan.security.details;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.repository.rollpermission.EmpRepository;

@Service
public class EmpDetailsService implements UserDetailsService {

    private final EmpRepository empRepository;

    public EmpDetailsService(EmpRepository empRepository) {
        this.empRepository = empRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 這裡的 username 就是登入時輸入的 email
        Optional<EmpEntity> optional = empRepository.findByEmpEmail(username);
        EmpEntity emp = optional.orElseThrow(
                () -> new UsernameNotFoundException("Employee not found: " + username)
        );
        return new EmpDetails(emp);
    }
}
