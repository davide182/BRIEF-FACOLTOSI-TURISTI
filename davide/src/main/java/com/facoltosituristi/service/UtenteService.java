package com.facoltosituristi.service;

import java.util.List;

import com.facoltosituristi.dao.UtenteDao;
import com.facoltosituristi.model.Utente;

public class UtenteService {
    private final UtenteDao utenteDao = new UtenteDao();
    
    public Utente createUtente(Utente utente) {
        if (utente.getEmail() == null || utente.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email è obbligatoria");
        }
        if (utente.getNome() == null || utente.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome è obbligatorio");
        }
        
        return utenteDao.create(utente);
    }
    
    public Utente getUtenteById(long id) {
        Utente utente = utenteDao.findById(id);
        if (utente == null) {
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        return utente;
    }
    
    public List<Utente> getAllUtenti() {
        return utenteDao.findAll();
    }
    
    public List<Utente> getAllUtentiConDisabilitati() {
        return utenteDao.findAllConDisabilitati();
    }
    
    public Utente updateUtente(long id, Utente utente) {
        Utente existing = utenteDao.findById(id);
        if (existing == null) {
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        
        return utenteDao.update(id, utente);
    }
    
    public boolean disableUtente(long id) {
        Utente utente = utenteDao.findById(id);
        if (utente == null) {
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        
        Utente updated = new Utente(
            utente.getIdUtente(),
            utente.getNome(),
            utente.getCognome(),
            utente.getEmail(),
            utente.getIndirizzo(),
            false 
        );
        
        utenteDao.update(id, updated);
        return true;
    }
    
    public boolean enableUtente(long id) {
        Utente utente = utenteDao.findById(id);
        if (utente == null) {
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        
        Utente updated = new Utente(
            utente.getIdUtente(),
            utente.getNome(),
            utente.getCognome(),
            utente.getEmail(),
            utente.getIndirizzo(),
            true  
        );
        
        utenteDao.update(id, updated);
        return true;
    }
    
    public Utente getUtenteByEmail(String email) {
        Utente utente = utenteDao.findByEmail(email);
        if (utente == null) {
            throw new RuntimeException("Utente non trovato con email: " + email);
        }
        return utente;
    }
}