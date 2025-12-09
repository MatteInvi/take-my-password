package myproject.takemypassword.take_my_password.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import myproject.takemypassword.take_my_password.model.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {

    public Optional<AuthToken> findByToken(String token);

    // Modifica la query per usare l'ID dell'utente correlato
    @Modifying
    @Transactional
    @Query("DELETE FROM AuthToken t WHERE t.user.id = :userId")
    int deleteByUserId(@Param("userId") Integer userId); // Usa il tipo ID corretto
}
