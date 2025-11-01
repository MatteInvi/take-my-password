package myproject.takemypassword.take_my_password.Security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import myproject.takemypassword.take_my_password.model.User;
import myproject.takemypassword.take_my_password.repository.UserRepository;

public class DatabaseUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Optional<User> user = userRepository.findByEmail(username);

       if (user.isPresent()){
        return new DatabaseUserDetails(user.get());
       } else{
        throw new UsernameNotFoundException("Utente non trovato!");
       }
    }
    
}
