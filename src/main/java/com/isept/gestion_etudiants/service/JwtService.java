package com.isept.gestion_etudiants.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    // ⚠️ En production, cette clé doit venir d'une variable d'environnement, jamais codée en dur
    private static final String SECRET_KEY = "IsepAtSecretKey2026ChangeMoiEnProd";
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 10; // 10 heures

    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    // Génère un token à partir de l'email de l'utilisateur
    public String genererToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .sign(algorithm);
    }

    // Extrait l'email depuis le token (le "subject")
    public String extraireEmail(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getSubject();
    }

    // Vérifie que le token est valide (signature + expiration)
    public boolean estValide(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
