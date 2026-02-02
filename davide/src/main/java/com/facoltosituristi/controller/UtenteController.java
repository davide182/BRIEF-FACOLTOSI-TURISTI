package com.facoltosituristi.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facoltosituristi.model.Utente;
import com.facoltosituristi.service.UtenteService;

import io.javalin.http.Context;

public class UtenteController {
    private static final Logger log = LoggerFactory.getLogger(UtenteController.class);
    private final UtenteService utenteService = new UtenteService();
    
    public void create(Context ctx) {
        try {
            Utente utente = ctx.bodyAsClass(Utente.class);
            
            if (utente.getNome() == null || utente.getCognome() == null || 
                utente.getEmail() == null || utente.getIndirizzo() == null) {
                ctx.status(400).json(createError("Dati mancanti", "Tutti i campi sono obbligatori"));
                return;
            }
            
            Utente created = utenteService.createUtente(utente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Utente creato con successo");
            response.put("data", created);
            response.put("id", created.getIdUtente());
            
            ctx.status(201).json(response);
            log.info("Utente creato: {}", created.getEmail());
            
        } catch (Exception e) {
            log.error("Errore nella creazione utente: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getById(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Utente utente = utenteService.getUtenteById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", utente);
            
            ctx.json(response);
            log.info(" Recuperato utente ID: {}", id);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Utente non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nel recupero utente: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getAll(Context ctx) {
        try {
            var utenti = utenteService.getAllUtenti();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", utenti.size());
            response.put("data", utenti);
            
            ctx.json(response);
            log.info("Recuperati {} utenti", utenti.size());
            
        } catch (Exception e) {
            log.error("Errore nel recupero utenti: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void update(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Utente utente = ctx.bodyAsClass(Utente.class);
            
            if (utente.getNome() == null || utente.getCognome() == null || 
                utente.getEmail() == null || utente.getIndirizzo() == null) {
                ctx.status(400).json(createError("Dati mancanti", "Tutti i campi sono obbligatori"));
                return;
            }
            
            Utente updated = utenteService.updateUtente(id, utente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Utente aggiornato con successo");
            response.put("data", updated);
            
            ctx.json(response);
            log.info("Utente aggiornato ID: {}", id);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Utente non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nell'aggiornamento utente: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void disable(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            boolean disabled = utenteService.disableUtente(id);
            
            if (disabled) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Utente disabilitato con successo");
                response.put("id", id);
                response.put("note", "L'utente è stato disabilitato (soft delete)");
                
                ctx.json(response);
                log.info("Utente disabilitato ID: {}", id);
            } else {
                ctx.status(404).json(createError("Utente non trovato", "ID: " + id));
            }
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Utente non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nella disabilitazione utente: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void enable(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            boolean enabled = utenteService.enableUtente(id);
            
            if (enabled) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Utente abilitato con successo");
                response.put("id", id);
                
                ctx.json(response);
                log.info("Utente abilitato ID: {}", id);
            } else {
                ctx.status(404).json(createError("Utente non trovato", "ID: " + id));
            }
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Utente non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nell'abilitazione utente: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    private Map<String, String> createError(String error, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }
}