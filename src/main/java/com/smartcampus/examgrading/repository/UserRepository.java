package com.smartcampus.examgrading.repository;

import com.smartcampus.examgrading.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // From first version
    List<User> findAllById(Iterable<Long> ids);

    // From second version
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(User.Role role);
}
