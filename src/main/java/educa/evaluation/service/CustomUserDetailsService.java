package educa.evaluation.service;

import educa.evaluation.domain.Permission;
import educa.evaluation.domain.User;
import educa.evaluation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return transactionTemplate.execute((status) -> {
            User user = userRepository.findByUsername(username);

            if(user == null) {
                throw new UsernameNotFoundException(username);
            }

            return new PersonUserDetails(user);
        });
    }

    public static class PersonUserDetails implements UserDetails {

        private String username;
        private String password;
        private Collection<? extends GrantedAuthority> authorities;

        public PersonUserDetails(User user) {
            username = user.getUsername();
            password = user.getPassword();

            List<String> stringAuthorities = new ArrayList<>();
            stringAuthorities.add("ROLE_" + user.getRole().getId().toUpperCase());

            stringAuthorities.addAll(user.getRole().getPermissions().stream()
                    .map(Permission::getId)
                    .map(id -> "PERM_" + id.toUpperCase())
                    .collect(Collectors.toList()));

            authorities = AuthorityUtils.createAuthorityList(stringAuthorities.toArray(new String[stringAuthorities.size()]));

        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

}
