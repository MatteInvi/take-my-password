package myproject.takemypassword.take_my_password.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import myproject.takemypassword.take_my_password.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
