package com.isept.gestion_etudiants.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isept.gestion_etudiants.dto.ErrorResponse;
import com.isept.gestion_etudiants.entity.Etudiant;
import com.isept.gestion_etudiants.service.EtudiantService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/etudiants")
@RequiredArgsConstructor
@Tag(name = "Étudiants", description = "Gestion des étudiants de l'ISEP-AT")
public class EtudiantController {

    private final EtudiantService etudiantService;

    // ---------- AJOUTER ----------
    @PostMapping
    @Operation(summary = "Ajouter un étudiant", description = "Crée un nouvel étudiant après vérification des champs obligatoires et de l'unicité du matricule/email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Étudiant créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Un champ obligatoire est manquant"),
            @ApiResponse(responseCode = "409", description = "Matricule ou email déjà existant")
    })
    public ResponseEntity<?> ajouter(@RequestBody Etudiant etudiant) {
        // 1. Contrôles des champs obligatoires
        ResponseEntity<?> erreur = validerChamps(etudiant);
        if (erreur != null) return erreur;

        // 2. Vérification de l'unicité du matricule
        if (etudiantService.matriculeExiste(etudiant.getMatricule())) {
            return construireErreur(HttpStatus.CONFLICT, "Le matricule existe déjà.");
        }

        // 3. Vérification de l'unicité de l'email
        if (etudiantService.emailExiste(etudiant.getEmail())) {
            return construireErreur(HttpStatus.CONFLICT, "L'email existe déjà.");
        }

        // 4. Sauvegarde
        Etudiant nouvel = etudiantService.ajouter(etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvel);
    }

    // ---------- LISTER ----------
    @GetMapping
    @Operation(summary = "Lister les étudiants", description = "Retourne la liste complète des étudiants")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<List<Etudiant>> lister() {
        return ResponseEntity.ok(etudiantService.lister());
    }

    // ---------- RECHERCHER PAR ID ----------
    @GetMapping("/{id}")
    @Operation(summary = "Rechercher un étudiant par id", description = "Retourne un étudiant selon son identifiant technique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
            @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    public ResponseEntity<?> rechercher(@PathVariable Long id) {
        Optional<Etudiant> etudiant = etudiantService.rechercher(id);
        if (etudiant.isEmpty()) {
            return construireErreur(HttpStatus.NOT_FOUND, "Étudiant introuvable.");
        }
        return ResponseEntity.ok(etudiant.get());
    }

    // ---------- MODIFIER ----------
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un étudiant", description = "Met à jour les informations d'un étudiant existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Un champ obligatoire est manquant"),
            @ApiResponse(responseCode = "404", description = "Étudiant introuvable"),
            @ApiResponse(responseCode = "409", description = "Matricule ou email déjà utilisé par un autre étudiant")
    })
    public ResponseEntity<?> modifier(@PathVariable Long id, @RequestBody Etudiant etudiant) {
        // 1. L'étudiant existe-t-il ?
        Optional<Etudiant> existantOpt = etudiantService.rechercher(id);
        if (existantOpt.isEmpty()) {
            return construireErreur(HttpStatus.NOT_FOUND, "Étudiant introuvable.");
        }

        // 2. Contrôles des champs obligatoires
        ResponseEntity<?> erreur = validerChamps(etudiant);
        if (erreur != null) return erreur;

        Etudiant existant = existantOpt.get();

        // 3. Vérifier que le matricule n'est pas déjà pris par un AUTRE étudiant
        if (!existant.getMatricule().equals(etudiant.getMatricule())
                && etudiantService.matriculeExiste(etudiant.getMatricule())) {
            return construireErreur(HttpStatus.CONFLICT, "Le matricule existe déjà.");
        }

        // 4. Vérifier que l'email n'est pas déjà pris par un AUTRE étudiant
        if (!existant.getEmail().equals(etudiant.getEmail())
                && etudiantService.emailExiste(etudiant.getEmail())) {
            return construireErreur(HttpStatus.CONFLICT, "L'email existe déjà.");
        }

        Etudiant modifie = etudiantService.modifier(id, etudiant);
        return ResponseEntity.ok(modifie);
    }

    // ---------- SUPPRIMER ----------
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un étudiant", description = "Supprime un étudiant selon son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Étudiant supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        if (!etudiantService.existeParId(id)) {
            return construireErreur(HttpStatus.NOT_FOUND, "Étudiant introuvable.");
        }
        etudiantService.supprimer(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // ---------- BONUS : recherche par matricule ----------
    @GetMapping("/matricule/{matricule}")
    @Operation(summary = "Rechercher un étudiant par matricule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
            @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    public ResponseEntity<?> rechercherParMatricule(@PathVariable String matricule) {
        Optional<Etudiant> etudiant = etudiantService.rechercherParMatricule(matricule);
        if (etudiant.isEmpty()) {
            return construireErreur(HttpStatus.NOT_FOUND, "Étudiant introuvable.");
        }
        return ResponseEntity.ok(etudiant.get());
    }

    // ---------- BONUS : liste triée par nom ----------
    @GetMapping("/tri-nom")
    @Operation(summary = "Lister les étudiants triés par nom (ordre alphabétique)")
    public ResponseEntity<List<Etudiant>> listerTrieParNom() {
        return ResponseEntity.ok(etudiantService.listerTrieParNom());
    }

    // ================== MÉTHODES UTILITAIRES PRIVÉES ==================

    // Vérifie tous les champs obligatoires ; retourne une erreur 400 si un champ manque, sinon null
    private ResponseEntity<?> validerChamps(Etudiant e) {
        if (e.getMatricule() == null || e.getMatricule().isBlank()) {
            return construireErreur(HttpStatus.BAD_REQUEST, "Le matricule est obligatoire.");
        }
        if (e.getPrenom() == null || e.getPrenom().isBlank()) {
            return construireErreur(HttpStatus.BAD_REQUEST, "Le prénom est obligatoire.");
        }
        if (e.getNom() == null || e.getNom().isBlank()) {
            return construireErreur(HttpStatus.BAD_REQUEST, "Le nom est obligatoire.");
        }
        if (e.getEmail() == null || e.getEmail().isBlank()) {
            return construireErreur(HttpStatus.BAD_REQUEST, "L'email est obligatoire.");
        }
        if (e.getDateNaissance() == null) {
            return construireErreur(HttpStatus.BAD_REQUEST, "La date de naissance est obligatoire.");
        }
        if (e.getLieuNaissance() == null || e.getLieuNaissance().isBlank()) {
            return construireErreur(HttpStatus.BAD_REQUEST, "Le lieu de naissance est obligatoire.");
        }
        if (e.getNationalite() == null || e.getNationalite().isBlank()) {
            return construireErreur(HttpStatus.BAD_REQUEST, "La nationalité est obligatoire.");
        }
        return null; // aucun problème
    }

    // Construit une réponse d'erreur standardisée { "code": ..., "msg": ... }
    private ResponseEntity<ErrorResponse> construireErreur(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), message));
    }
}
