package com.facoltosituristi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facoltosituristi.model.Feedback;
import com.facoltosituristi.service.FeedbackService;

import io.javalin.http.Context;

public class FeedbackController {
    private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);
    private final FeedbackService feedbackService = new FeedbackService();
    
    public void create(Context ctx) {
        try {
            Feedback feedback = ctx.bodyAsClass(Feedback.class);
            
            if (feedback.getTitolo() == null || feedback.getTitolo().isEmpty() ||
                feedback.getTesto() == null || feedback.getTesto().isEmpty() ||
                feedback.getPunteggio() == null || feedback.getIdPrenotazione() <= 0) {
                ctx.status(400).json(createError("Dati mancanti o non validi", "Tutti i campi sono obbligatori"));
                return;
            }
            
            if (feedback.getPunteggio() < 1 || feedback.getPunteggio() > 5) {
                ctx.status(400).json(createError("Punteggio non valido", "Il punteggio deve essere tra 1 e 5"));
                return;
            }
            
            Feedback created = feedbackService.createFeedback(feedback);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Feedback creato con successo");
            response.put("data", created);
            response.put("id", created.getIdFeedback());
            
            ctx.status(201).json(response);
            log.info("Feedback creato: ID {} per prenotazione ID {}", created.getIdFeedback(), created.getIdPrenotazione());
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(createError("Validazione fallita", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nella creazione del feedback: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getById(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Feedback feedback = feedbackService.getFeedbackById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", feedback);
            
            ctx.json(response);
            log.info("Recuperato feedback ID: {}", id);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Feedback non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nel recupero feedback: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getAll(Context ctx) {
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedback();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", feedbacks.size());
            response.put("data", feedbacks);
            
            ctx.json(response);
            log.info("Recuperati {} feedback", feedbacks.size());
            
        } catch (Exception e) {
            log.error("Errore nel recupero feedback: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getByPrenotazione(Context ctx) {
        try {
            long idPrenotazione = Long.parseLong(ctx.pathParam("idPrenotazione"));
            Feedback feedback = feedbackService.getFeedbackByPrenotazione(idPrenotazione);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", feedback);
            
            ctx.json(response);
            log.info("Recuperato feedback per prenotazione ID: {}", idPrenotazione);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID prenotazione deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Feedback non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nel recupero feedback per prenotazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getByAbitazione(Context ctx) {
        try {
            long idAbitazione = Long.parseLong(ctx.pathParam("idAbitazione"));
            List<Feedback> feedbacks = feedbackService.getFeedbackByAbitazione(idAbitazione);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", feedbacks.size());
            response.put("data", feedbacks);
            
            ctx.json(response);
            log.info("Recuperati {} feedback per abitazione ID: {}", feedbacks.size(), idAbitazione);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID abitazione deve essere un numero"));
        } catch (Exception e) {
            log.error("Errore nel recupero feedback per abitazione: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getByHost(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("idUtente"));
            List<Feedback> feedbacks = feedbackService.getFeedbackByHost(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", feedbacks.size());
            response.put("data", feedbacks);
            
            ctx.json(response);
            log.info("Recuperati {} feedback per host ID: {}", feedbacks.size(), idUtente);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID utente deve essere un numero"));
        } catch (Exception e) {
            log.error("Errore nel recupero feedback per host: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getConAltoPunteggio(Context ctx) {
        try {
            String minPunteggioParam = ctx.queryParam("minPunteggio");
            int minPunteggio = 4;
            
            if (minPunteggioParam != null && !minPunteggioParam.isEmpty()) {
                minPunteggio = Integer.parseInt(minPunteggioParam);
                if (minPunteggio < 1 || minPunteggio > 5) {
                    ctx.status(400).json(createError("Parametro non valido", "Il punteggio minimo deve essere tra 1 e 5"));
                    return;
                }
            }
            
            List<Feedback> feedbacks = feedbackService.getFeedbackConAltoPunteggio(minPunteggio);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", feedbacks.size());
            response.put("data", feedbacks);
            response.put("minPunteggio", minPunteggio);
            
            ctx.json(response);
            log.info("Recuperati {} feedback con punteggio >= {}", feedbacks.size(), minPunteggio);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("Parametro non valido", "Il punteggio minimo deve essere un numero"));
        } catch (Exception e) {
            log.error("Errore nel recupero feedback con punteggio alto: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void getMediaPunteggioHost(Context ctx) {
        try {
            long idUtente = Long.parseLong(ctx.pathParam("idUtente"));
            double media = feedbackService.getMediaPunteggioHost(idUtente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("mediaPunteggio", media);
            response.put("idUtente", idUtente);
            response.put("message", String.format("Media punteggio per host ID %d: %.2f", idUtente, media));
            
            ctx.json(response);
            log.info("Media punteggio per host ID {}: {}", idUtente, media);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID utente deve essere un numero"));
        } catch (Exception e) {
            log.error("Errore nel calcolo media punteggio host: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void update(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Feedback feedback = ctx.bodyAsClass(Feedback.class);
            
            if (feedback.getTitolo() == null || feedback.getTitolo().isEmpty() ||
                feedback.getTesto() == null || feedback.getTesto().isEmpty() ||
                feedback.getPunteggio() == null) {
                ctx.status(400).json(createError("Dati mancanti", "Titolo, testo e punteggio sono obbligatori"));
                return;
            }
            
            if (feedback.getPunteggio() < 1 || feedback.getPunteggio() > 5) {
                ctx.status(400).json(createError("Punteggio non valido", "Il punteggio deve essere tra 1 e 5"));
                return;
            }
            
            Feedback updated = feedbackService.updateFeedback(id, feedback);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Feedback aggiornato con successo");
            response.put("data", updated);
            
            ctx.json(response);
            log.info("Feedback aggiornato ID: {}", id);
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Feedback non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nell'aggiornamento feedback: {}", e.getMessage());
            ctx.status(500).json(createError("Errore interno", e.getMessage()));
        }
    }
    
    public void delete(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            boolean deleted = feedbackService.deleteFeedback(id);
            
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Feedback eliminato con successo");
                response.put("id", id);
                
                ctx.json(response);
                log.info("Feedback eliminato ID: {}", id);
            } else {
                ctx.status(404).json(createError("Feedback non trovato", "ID: " + id));
            }
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(createError("ID non valido", "L'ID deve essere un numero"));
        } catch (RuntimeException e) {
            ctx.status(404).json(createError("Feedback non trovato", e.getMessage()));
        } catch (Exception e) {
            log.error("Errore nell'eliminazione feedback: {}", e.getMessage());
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