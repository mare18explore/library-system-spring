package com.abdimare.library_system_spring.repository;

import com.abdimare.library_system_spring.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // Optional because there might not be a member with that email
    Optional<Member> findByEmail(String email);

    // used to block duplicate signups before trying to save
    boolean existsByEmail(String email);
}