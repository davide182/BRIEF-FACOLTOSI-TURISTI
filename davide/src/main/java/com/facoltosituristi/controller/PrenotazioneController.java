package com.facoltosituristi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facoltosituristi.model.Prenotazione;
import com.facoltosituristi.service.PrenotazioneService;
import com.facoltosituristi.statoprenotazione.StatoPrenotazione;

import io.javalin.http.Context;

public class PrenotazioneController {
    private static final Logger log = LoggerFactory.getLogger(PrenotazioneController.class);
    private final PrenotazioneService prenotazioneService = new PrenotazioneService();
    
    public void create(Context ctx) {
        try {
            Prenotazione prenotazione = ctx.bodyAsClass(Prenotazione.class);
            
            if (prenotazione.getIdAbitazione() <= 0 || prenotazione.getIdUtente() <= 0 ||
                prenotazione.getDataInizioPrenotazione() == null || 
                prenotazione.getDataFinePrenotazione() == null ||
                prenotazione.getPrezzoTotale() <= 0) {
                ctx.status(400).json(createError("Dati mancanti o non validi", "Tutti i campi sono obbligatori e devono essere validi"));
                return;
            }
            
            Prenotazione created = prenotazioneService.createPrenotazione(prenotazione);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Prenotazione creata con successo");
            response.put("data", created);
            response.put("id", created.getIdPrenotazione());
            
            ctx.status(201).json(response);
            log.info("Prenotazione creata: ID {} per abitazione ID {}", created.getIdPrenotazione(), created.getIdAbitazione());
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(createError("Validazione fallita", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nella creazione della prenotazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getById(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Prenotazione prenotazione = prenotazioneService.getPrenotazioneById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", prenotazione);
            
            ctx.json(response);
            log.info("Recuperata prenotazione ID: {}", id);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Prenotazione non trovata", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nel recupero prenotazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getAll(Context ctx) {
        try {
            List<Prenotazione> prenotazioni = prenotazioneService.getAllPrenotazioni();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", prenotazioni.size());
            response.put("data", prenotazioni);
            
            ctx.json(response);
            log.info("Recuperate {} prenotazioni", prenotazioni.size());
            
        } catch (Exception e) {
            log.error("Errore nel recupero prenotazioni: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getByUtente(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("idUtente"));
            List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniByUtente(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", prenotazioni.size());
            response.put("data", prenotazioni);
            
            ctx.json(response);
            log.info("Recuperate {} prenotazioni per utente ID: {}", prenotazioni.size(), idUtente);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID utente deve essere un numero"));
        } catch (Exception e) {
            log.error("Errore nel recupero prenotazioni utente: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getByAbitazione(Context ctx) {
        try {
            long idAbitazione = Long.parseLong(ctx.pathParam("idAbitazione"));
            List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniByAbitazione(idAbitazione);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", prenotazioni.size());
            response.put("data", prenotazioni);
            
            ctx.json(response);
            log.info("Recuperate {} prenotazioni per abitazione ID: {}", prenotazioni.size(), idAbitazione);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID abitazione deve essere un numero"));
        } catch (Exception e) {
            log.error("Errore nel recupero prenotazioni abitazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getUltimaPrenotazioneByUtente(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("idUtente"));
            Prenotazione prenotazione = prenotazioneService.getUltimaPrenotazioneByUtente(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", prenotazione);
            response.put("idUtente", idUtente);
            
            ctx.json(response);
            log.info("Ultima prenotazione trovata per utente ID: {}", idUtente);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID utente deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Nessuna prenotazione trovata", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nel recupero ultima prenotazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void update(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Prenotazione prenotazione = ctx.bodyAsClass(Prenotazione.class);
            
            if (prenotazione.getIdAbitazione() <= 0 || prenotazione.getIdUtente() <= 0 ||
                prenotazione.getDataInizioPrenotazione() == null || 
                prenotazione.getDataFinePrenotazione() == null ||
                prenotazione.getPrezzoTotale() <= 0) {
                ctx.status(400).json(createError("Dati mancanti o non validi", "Tutti i campi sono obbligatori e devono essere validi"));
                return;
            }
            
            Prenotazione updated = prenotazioneService.updatePrenotazione(id, prenotazione);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Prenotazione aggiornata con successo");
            response.put("data", updated);
            
            ctx.json(response);
            log.info("Prenotazione aggiornata ID: {}", id);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Prenotazione non trovata", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nell'aggiornamento prenotazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void updateStato(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            String statoStr = ctx.body();
            
            if (statoStr == null || statoStr.isEmpty()) {
                ctx.status(400).json(createError("Stato mancante", "Lo stato è obbligatorio"));
                return;
            }
            
            StatoPrenotazione nuovoStato = StatoPrenotazione.valueOf(statoStr.toUpperCase());
            boolean updated = prenotazioneService.updateStatoPrenotazione(id, nuovoStato);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Stato prenotazione aggiornato con successo");
            response.put("id", id);
            response.put("nuovoStato", nuovoStato);
            
            ctx.json(response);
            log.info("Stato prenotazione aggiornato ID {}: {}", id, nuovoStato);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(createError("Stato non valido", "Stato deve essere: IN_ATTESA, CONFERMATA, CANCELLATA, COMPLETATA"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Prenotazione non trovata", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nell'aggiornamento stato prenotazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void cancel(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            boolean cancelled = prenotazioneService.cancelPrenotazione(id);
            
            if (cancelled) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Prenotazione cancellata con successo");
                response.put("id", id);
                response.put("note", "La prenotazione è stata cancellata (soft delete)");
                
                ctx.json(response);
                log.info("Prenotazione cancellata ID: {}", id);
            } else {
                ctx.status(404).json(createError("Prenotazione non trovata", "ID: " + id));
            }
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Prenotazione non trovata", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nella cancellazione prenotazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getPrenotazioniUltimoMese(Context ctx) {
        try {
            List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniUltimoMese();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", prenotazioni.size());
            response.put("data", prenotazioni);
            response.put("periodo", "Ultimo mese");
            
            ctx.json(response);
            log.info("Recuperate {} prenotazioni dell'ultimo mese", prenotazioni.size());
            
        } catch (Exception e) {
            log.error("Errore nel recupero prenotazioni ultimo mese: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getGiorniPrenotatiUltimoMese(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("idUtente"));
            int giorni = prenotazioneService.getGiorniPrenotatiUltimoMese(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("giorniPrenotatiUltimoMese", giorni);
            response.put("idUtente", idUtente);
            response.put("message", String.format("Utente ID %d ha %d giorni prenotati nell'ultimo mese", idUtente, giorni));
            
            ctx.json(response);
            log.info("Giorni prenotati per utente ID {}: {}", idUtente, giorni);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID utente deve essere un numero"));
        } catch (Exception e) {
            log.error("Errore nel calcolo giorni prenotati: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }

    public void getTop5UtentiGiorniPrenotatiUltimoMese(Context ctx) {
        try {
            List<Map<String, Object>> topUtenti = prenotazioneService.getTop5UtentiGiorniPrenotatiUltimoMese();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", topUtenti.size());
            response.put("data", topUtenti);
            response.put("message", String.format("Top %d utenti per giorni prenotati nell'ultimo mese", topUtenti.size()));
            
            ctx.json(response);
            log.info("Recuperati {} utenti top per giorni prenotati via API", topUtenti.size());
            
        } catch (Exception e) {
            log.error("Errore nel recupero top 5 utenti giorni prenotati via API: {}", e.getMessage());
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