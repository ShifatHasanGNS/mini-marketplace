package com.marketplace.app.repository;

import com.marketplace.app.entity.Login;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    Optional<Login> findByName(String name);
    List<Login> findByRoleNot(String role); // all users except admin
}
