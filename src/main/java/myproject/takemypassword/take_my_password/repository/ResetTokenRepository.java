package myproject.takemypassword.take_my_password.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import myproject.takemypassword.take_my_password.model.ResetToken;
import myproject.takemypassword.take_my_password.model.User;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Integer> {
    
     public Optional<ResetToken> findByToken(String token);
     public Optional<ResetToken> findByUser(User user);





    @Modifying
    @Transactional
    @Query("DELETE FROM ResetToken t WHERE t.user = :user")
    int deleteByUser(@Param("user") User user);
}
