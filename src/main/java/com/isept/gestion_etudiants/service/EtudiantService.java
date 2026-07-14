package com.isept.gestion_etudiants.service;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.isept.gestion_etudiants.entity.Etudiant;
import com.isept.gestion_etudiants.repository.EtudiantRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // injection par constructeur (Lombok génère le constructeur avec les champs "final")
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    /*  Ajouter un étudiant
    public Etudiant ajouter(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }*/

    // Modifier un étudiant existant
    public Etudiant modifier(Long id, Etudiant nouvellesDonnees) {
        Etudiant existant = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        existant.setMatricule(nouvellesDonnees.getMatricule());
        existant.setPrenom(nouvellesDonnees.getPrenom());
        existant.setNom(nouvellesDonnees.getNom());
        existant.setEmail(nouvellesDonnees.getEmail());
        existant.setDateNaissance(nouvellesDonnees.getDateNaissance());
        existant.setLieuNaissance(nouvellesDonnees.getLieuNaissance());
        existant.setNationalite(nouvellesDonnees.getNationalite());

        return etudiantRepository.save(existant);
    }

    // Supprimer un étudiant
    public void supprimer(Long id) {
        etudiantRepository.deleteById(id);
    }

    // Rechercher un étudiant par id
    public Optional<Etudiant> rechercher(Long id) {
        return etudiantRepository.findById(id);
    }

    // Lister tous les étudiants
    public List<Etudiant> lister() {
        return etudiantRepository.findAll();
    }

    // Vérifications d'existence (utilisées par le contrôleur)
    public boolean matriculeExiste(String matricule) {
        return etudiantRepository.existsByMatricule(matricule);
    }

    public boolean emailExiste(String email) {
        return etudiantRepository.existsByEmail(email);
    }

    public boolean existeParId(Long id) {
        return etudiantRepository.existsById(id);
    }

    // Bonus : recherche par matricule
    public Optional<Etudiant> rechercherParMatricule(String matricule) {
        return etudiantRepository.findByMatricule(matricule);
    }

    // Bonus : liste triée par nom
    public List<Etudiant> listerTrieParNom() {
        return etudiantRepository.findAllByOrderByNomAsc();
    }

    //
    public Etudiant ajouter(Etudiant etudiant) {
    etudiant.setId(null); // sécurité : on ignore tout id envoyé par le client
    return etudiantRepository.save(etudiant);
}
}
