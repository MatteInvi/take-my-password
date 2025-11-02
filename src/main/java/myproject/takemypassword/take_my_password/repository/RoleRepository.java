package myproject.takemypassword.take_my_password.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import myproject.takemypassword.take_my_password.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    
}
