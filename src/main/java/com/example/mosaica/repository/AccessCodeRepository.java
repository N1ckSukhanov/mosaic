package com.example.mosaica.repository;

import com.example.mosaica.entity.AccessCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccessCodeRepository extends JpaRepository<AccessCode, UUID> {
    Optional<AccessCode> findByCode(String code);

    Page<AccessCode> findAllByOrderByCreatedDesc(Pageable pageable);

    boolean existsByCode(String code);
}
