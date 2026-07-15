package com.isept.gestion_etudiants.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isept.gestion_etudiants.dto.AuthResponse;
import com.isept.gestion_etudiants.dto.LoginRequest;
import com.isept.gestion_etudiants.dto.RegisterRequest;
import com.isept.gestion_etudiants.entity.Utilisateur;
import com.isept.gestion_etudiants.service.JwtService;
import com.isept.gestion_etudiants.service.UtilisateurService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription et connexion des utilisateurs")
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Inscrire un nouvel utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "409", description = "Cet email existe déjà")
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (utilisateurService.emailExiste(request.getEmail())) {
            return construireErreur(HttpStatus.CONFLICT, "Cet email existe déjà.");
        }

        Utilisateur utilisateur = utilisateurService.inscrire(
                request.getNom(), request.getEmail(), request.getMotDePasse());

        // On ne renvoie jamais le mot de passe, même encodé, dans la réponse
        utilisateur.setMotDePasse(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateur);
    }

    @PostMapping("/login")
    @Operation(summary = "Authentifier un utilisateur et récupérer un Token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie, token renvoyé"),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Utilisateur> utilisateurOpt = utilisateurService.authentifier(
                request.getEmail(), request.getMotDePasse());

        if (utilisateurOpt.isEmpty()) {
            return construireErreur(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect.");
        }

        String token = jwtService.genererToken(utilisateurOpt.get().getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    private ResponseEntity<com.isept.gestion_etudiants.dto.ErrorResponse> construireErreur(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new com.isept.gestion_etudiants.dto.ErrorResponse(status.value(), message));
    }
}
