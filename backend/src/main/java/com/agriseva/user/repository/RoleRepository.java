package com.agriseva.user.repository;

import com.agriseva.user.model.Role;
import com.agriseva.user.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);
}