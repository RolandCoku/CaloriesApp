package org.springboot.caloriesapp.repository;

import org.springboot.caloriesapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name) throws RuntimeException;
    boolean existsByName(String name);
}
