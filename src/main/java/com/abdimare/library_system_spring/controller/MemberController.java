package com.abdimare.library_system_spring.controller;

import com.abdimare.library_system_spring.dto.LoginRequest;
import com.abdimare.library_system_spring.dto.RegisterRequest;
import com.abdimare.library_system_spring.model.Member;
import com.abdimare.library_system_spring.repository.MemberRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberController(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // check first so we can send a clear 409 instead of a database error
        if (memberRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("email", "is already registered"));
        }

        Member member = new Member();
        member.setName(request.name());
        member.setEmail(request.email());
        // bcrypt adds a random salt, so two people with the same password still get different hashes
        member.setPasswordHash(passwordEncoder.encode(request.password()));

        return ResponseEntity.status(HttpStatus.CREATED).body(memberRepository.save(member));
    }

    // same 401 whether the email or the password is wrong, so nobody can probe which emails exist
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return memberRepository.findByEmail(request.email())
                .filter(member -> passwordEncoder.matches(request.password(), member.getPasswordHash()))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "invalid email or password")));
    }

    @GetMapping
    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}