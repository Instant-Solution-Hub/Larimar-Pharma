package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
