package com.facoltosituristi.service;

import java.util.List;

import com.facoltosituristi.dao.FeedbackDao;
import com.facoltosituristi.model.Feedback;

public class FeedbackService {
    private final FeedbackDao feedbackDao = new FeedbackDao();
    
    public Feedback createFeedback(Feedback feedback) {
        if (feedback.getTitolo() == null || feedback.getTitolo().isEmpty()) {
            throw new IllegalArgumentException("Titolo è obbligatorio");
        }
        if (feedback.getTesto() == null || feedback.getTesto().isEmpty()) {
            throw new IllegalArgumentException("Testo è obbligatorio");
        }
        if (feedback.getPunteggio() < 1 || feedback.getPunteggio() > 5) {
            throw new IllegalArgumentException("Punteggio deve essere tra 1 e 5");
        }
        if (feedback.getIdPrenotazione() <= 0) {
            throw new IllegalArgumentException("ID prenotazione non valido");
        }
        
        return feedbackDao.create(feedback);
    }
    
    public Feedback getFeedbackById(long id) {
        Feedback feedback = feedbackDao.findById(id);
        if (feedback == null) {
            throw new RuntimeException("Feedback non trovato con ID: " + id);
        }
        return feedback;
    }
    
    public List<Feedback> getAllFeedback() {
        return feedbackDao.findAll();
    }
    
    public Feedback getFeedbackByPrenotazione(long idPrenotazione) {
        Feedback feedback = feedbackDao.findByPrenotazione(idPrenotazione);
        if (feedback == null) {
            throw new RuntimeException("Feedback non trovato per prenotazione ID: " + idPrenotazione);
        }
        return feedback;
    }
    
    public List<Feedback> getFeedbackByAbitazione(long idAbitazione) {
        return feedbackDao.findByAbitazione(idAbitazione);
    }
    
    public List<Feedback> getFeedbackByHost(long idUtente) {
        return feedbackDao.findByHost(idUtente);
    }
    
    public List<Feedback> getFeedbackConAltoPunteggio(int minimoPunteggio) {
        if (minimoPunteggio < 1 || minimoPunteggio > 5) {
            throw new IllegalArgumentException("Punteggio minimo deve essere tra 1 e 5");
        }
        return feedbackDao.findConAltoPunteggio(minimoPunteggio);
    }
    
    public double getMediaPunteggioHost(long idUtente) {
        return feedbackDao.calcolaMediaPunteggioHost(idUtente);
    }
    
    public Feedback updateFeedback(long id, Feedback feedback) {
        Feedback existing = feedbackDao.findById(id);
        if (existing == null) {
            throw new RuntimeException("Feedback non trovato con ID: " + id);
        }
        
        return feedbackDao.update(id, feedback);
    }
    
    public boolean deleteFeedback(long id) {
        Feedback existing = feedbackDao.findById(id);
        if (existing == null) {
            throw new RuntimeException("Feedback non trovato con ID: " + id);
        }
        
        return feedbackDao.delete(id);
    }
}