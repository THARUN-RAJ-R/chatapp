package com.chatapp.backend.repository;

import com.chatapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    // Find registered users from a list of phone numbers (contact sync)
    @Query("SELECT u FROM User u WHERE u.phone IN :phones")
    List<User> findByPhoneIn(List<String> phones);
}
