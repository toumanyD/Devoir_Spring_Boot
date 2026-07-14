package com.isept.gestion_etudiants.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.isept.gestion_etudiants.entity.Etudiant;

import java.util.List;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    // Vérifie si un matricule existe déjà
    boolean existsByMatricule(String matricule);

    // Vérifie si un email existe déjà
    boolean existsByEmail(String email);

    // Bonus : recherche par matricule
    Optional<Etudiant> findByMatricule(String matricule);

    // Bonus : tri par nom en ordre alphabétique
    List<Etudiant> findAllByOrderByNomAsc();
}
