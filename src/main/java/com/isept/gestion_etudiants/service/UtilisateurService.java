package com.isept.gestion_etudiants.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.isept.gestion_etudiants.entity.Utilisateur;
import com.isept.gestion_etudiants.repository.UtilisateurRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    // Inscription : encode le mot de passe avant sauvegarde
    public Utilisateur inscrire(String nom, String email, String motDePasseBrut) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasseBrut));
        utilisateur.setRole("USER");
        return utilisateurRepository.save(utilisateur);
    }

    // Authentification : vérifie email + mot de passe
    public Optional<Utilisateur> authentifier(String email, String motDePasseBrut) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        if (utilisateurOpt.isEmpty()) {
            return Optional.empty();
        }
        Utilisateur utilisateur = utilisateurOpt.get();
        if (!passwordEncoder.matches(motDePasseBrut, utilisateur.getMotDePasse())) {
            return Optional.empty();
        }
        return Optional.of(utilisateur);
    }

    public Optional<Utilisateur> trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
}
