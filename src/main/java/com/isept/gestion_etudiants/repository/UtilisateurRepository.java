package com.isept.gestion_etudiants.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isept.gestion_etudiants.entity.Utilisateur;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
}
