package com.facoltosituristi.service;

import java.util.List;
import java.util.Map;

import com.facoltosituristi.dao.PrenotazioneDao;
import com.facoltosituristi.model.Prenotazione;
import com.facoltosituristi.statoprenotazione.StatoPrenotazione;

public class PrenotazioneService {
    private final PrenotazioneDao prenotazioneDao = new PrenotazioneDao();
    
    public Prenotazione createPrenotazione(Prenotazione prenotazione) {
        if (prenotazione.getIdAbitazione() <= 0) {
            throw new IllegalArgumentException("ID abitazione non valido");
        }
        if (prenotazione.getIdUtente() <= 0) {
            throw new IllegalArgumentException("ID utente non valido");
        }
        if (prenotazione.getDataInizioPrenotazione() == null || prenotazione.getDataFinePrenotazione() == null) {
            throw new IllegalArgumentException("Date di inizio e fine sono obbligatorie");
        }
        if (prenotazione.getDataInizioPrenotazione().isAfter(prenotazione.getDataFinePrenotazione())) {
            throw new IllegalArgumentException("Data inizio deve essere prima della data fine");
        }
        if (prenotazione.getPrezzoTotale() <= 0) {
            throw new IllegalArgumentException("Prezzo totale deve essere maggiore di 0");
        }
        
        return prenotazioneDao.create(prenotazione);
    }
    
    public Prenotazione getPrenotazioneById(long id) {
        Prenotazione prenotazione = prenotazioneDao.findById(id);
        if (prenotazione == null) {
            throw new RuntimeException("Prenotazione non trovata con ID: " + id);
        }
        return prenotazione;
    }
    
    public List<Prenotazione> getAllPrenotazioni() {
        return prenotazioneDao.findAll();
    }
    
    public List<Prenotazione> getPrenotazioniByUtente(long idUtente) {
        return prenotazioneDao.findByUtente(idUtente);
    }
    
    public List<Prenotazione> getPrenotazioniByAbitazione(long idAbitazione) {
        return prenotazioneDao.findByAbitazione(idAbitazione);
    }
    
    public Prenotazione getUltimaPrenotazioneByUtente(long idUtente) {
        Prenotazione prenotazione = prenotazioneDao.findUltimaPrenotazioneByUtente(idUtente);
        if (prenotazione == null) {
            throw new RuntimeException("Nessuna prenotazione trovata per l'utente ID: " + idUtente);
        }
        return prenotazione;
    }
    
    public Prenotazione updatePrenotazione(long id, Prenotazione prenotazione) {
        Prenotazione existing = prenotazioneDao.findById(id);
        if (existing == null) {
            throw new RuntimeException("Prenotazione non trovata con ID: " + id);
        }
        
        return prenotazioneDao.update(id, prenotazione);
    }
    
    public boolean updateStatoPrenotazione(long id, StatoPrenotazione nuovoStato) {
        Prenotazione existing = prenotazioneDao.findById(id);
        if (existing == null) {
            throw new RuntimeException("Prenotazione non trovata con ID: " + id);
        }
        
        return prenotazioneDao.updateStato(id, nuovoStato);
    }
    
    public boolean cancelPrenotazione(long id) {
        Prenotazione existing = prenotazioneDao.findById(id);
        if (existing == null) {
            throw new RuntimeException("Prenotazione non trovata con ID: " + id);
        }
        
        return prenotazioneDao.softDelete(id);
    }
    
    public List<Prenotazione> getPrenotazioniUltimoMese() {
        return prenotazioneDao.findPrenotazioniUltimoMese();
    }
    
    public int getGiorniPrenotatiUltimoMese(long idUtente) {
        return prenotazioneDao.calcolaGiorniPrenotatiUltimoMese(idUtente);
    }

    public List<Map<String, Object>> getTop5UtentiGiorniPrenotatiUltimoMese() {
        return prenotazioneDao.findTop5UtentiGiorniPrenotatiUltimoMese();
    }

}