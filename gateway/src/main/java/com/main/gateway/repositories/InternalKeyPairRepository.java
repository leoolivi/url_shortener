package com.main.gateway.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.main.gateway.domain.models.InternalKeyPair;

import jakarta.transaction.Transactional;

public interface InternalKeyPairRepository extends JpaRepository<InternalKeyPair, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE InternalKeyPair k SET k.isValid = false")
    public void invalidateAll();

    @Query("SELECT 1 FROM InternalKeyPair k WHERE k.isValid = true")
    public InternalKeyPair findCurrentInternalKeyPair();
}
