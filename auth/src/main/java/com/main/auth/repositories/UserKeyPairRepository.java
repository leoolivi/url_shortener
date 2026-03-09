package com.main.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.main.auth.domain.models.UserKeyPair;

import jakarta.transaction.Transactional;

public interface UserKeyPairRepository extends JpaRepository<UserKeyPair, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE UserKeyPair k SET k.isValid = false")
    public void invalidateAll();

    @Query("SELECT 1 FROM UserKeyPair k WHERE k.isValid = true")
    public UserKeyPair findCurrentUserKeyPair();
}
