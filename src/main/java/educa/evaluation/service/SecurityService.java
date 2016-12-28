package educa.evaluation.service;

import educa.evaluation.domain.User;
import educa.evaluation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecurityService {


    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal != null && principal instanceof CustomUserDetailsService.PersonUserDetails) {
            CustomUserDetailsService.PersonUserDetails details = (CustomUserDetailsService.PersonUserDetails) principal;
            return userRepository.findByUsername(details.getUsername());
        } else if(principal != null && principal instanceof String) {
            return userRepository.findByUsername((String)principal);
        }
        return null;
    }

}
