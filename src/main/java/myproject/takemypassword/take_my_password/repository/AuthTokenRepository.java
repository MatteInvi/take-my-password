package myproject.takemypassword.take_my_password.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import myproject.takemypassword.take_my_password.model.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {
    
     public Optional<AuthToken> findByToken(String token);
}
