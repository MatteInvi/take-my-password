package myproject.takemypassword.take_my_password.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import myproject.takemypassword.take_my_password.model.DatoAccesso;

public interface DatiRepository extends JpaRepository<DatoAccesso, Integer> {
    public List<DatoAccesso> findByUsernameContainingIgnoreCase(String username);
}
