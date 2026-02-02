package com.facoltosituristi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facoltosituristi.model.Host;
import com.facoltosituristi.service.HostService;

import io.javalin.http.Context;

public class HostController {
    private static final Logger log = LoggerFactory.getLogger(HostController.class);
    private final HostService hostService = new HostService();
    
    public void promoteToHost(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("idUtente"));
            Host host = hostService.promoteToHost(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Utente promosso a Host con successo");
            response.put("data", host);
            response.put("codiceHost", host.getCodiceHost());
            
            ctx.status(201).json(response);
            log.info("Utente ID {} promosso a Host con codice: {}", idUtente, host.getCodiceHost());
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID utente deve essere un numero"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(createError("Validazione fallita", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nella promozione a host: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void isHost(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("idUtente"));
            boolean isHost = hostService.isHost(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("isHost", isHost);
            response.put("idUtente", idUtente);
            
            ctx.json(response);
            log.info("Verifica host per utente ID {}: {}", idUtente, isHost);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID utente deve essere un numero"));
        } catch (Exception e) {
            log.error("Errore nella verifica host: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getById(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("id"));
            Host host = hostService.getHostById(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", host);
            
            ctx.json(response);
            log.info("Recuperato host ID utente: {}", idUtente);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Host non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nel recupero host: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getByCodiceHost(Context ctx) {
        try {
            String codiceHost = ctx.pathParam("codiceHost");
            Host host = hostService.getHostByCodice(codiceHost);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", host);
            
            ctx.json(response);
            log.info("Recuperato host con codice: {}", codiceHost);
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(createError("Parametro non valido", e.getMessage()));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Host non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nel recupero host per codice: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getAll(Context ctx) {
        try {
            List<Host> hosts = hostService.getAllHosts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", hosts.size());
            response.put("data", hosts);
            
            ctx.json(response);
            log.info("Recuperati {} host", hosts.size());
            
        } catch (Exception e) {
            log.error("Errore nel recupero host: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getSuperHosts(Context ctx) {
        try {
            List<Host> superHosts = hostService.getAllSuperHosts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", superHosts.size());
            response.put("data", superHosts);
            response.put("message", String.format("Trovati %d Super Host", superHosts.size()));
            
            ctx.json(response);
            log.info("Recuperati {} Super Host", superHosts.size());
            
        } catch (Exception e) {
            log.error("Errore nel recupero Super Host: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void checkAndPromoteToSuperHost(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("idUtente"));
            boolean promosso = hostService.checkAndPromoteToSuperHost(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("promossoASuperHost", promosso);
            response.put("idUtente", idUtente);
            
            if (promosso) {
                response.put("message", "Host promosso a Super Host!");
                log.info("Host ID {} promosso a Super Host", idUtente);
            } else {
                response.put("message", "Host non ancora idoneo per Super Host");
                log.info("Host ID {} non ancora idoneo per Super Host", idUtente);
            }
            
            ctx.json(response);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID utente deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Host non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nella verifica/promozione Super Host: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }

    public void getHostsConPiuPrenotazioniUltimoMese(Context ctx) {
        try {
            List<Host> hosts = hostService.getHostsConPiuPrenotazioniUltimoMese();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", hosts.size());
            response.put("data", hosts);
            response.put("message", String.format("Trovati %d host con più prenotazioni nell'ultimo mese", hosts.size()));
            
            ctx.json(response);
            log.info("Recuperati {} host con più prenotazioni ultimo mese via API", hosts.size());
            
        } catch (Exception e) {
            log.error("Errore nel recupero host con più prenotazioni ultimo mese via API: {}", e.getMessage());
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