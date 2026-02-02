package com.facoltosituristi.service;

import java.util.List;

import com.facoltosituristi.dao.AbitazioneDao;
import com.facoltosituristi.dao.HostDao;
import com.facoltosituristi.model.Abitazione;

public class AbitazioneService {
    private final AbitazioneDao abitazioneDao = new AbitazioneDao();
    private final HostDao hostDao = new HostDao();
    
    public Abitazione createAbitazione(Abitazione abitazione) {
        if (abitazione.getNome() == null || abitazione.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome abitazione è obbligatorio");
        }
        if (abitazione.getPrezzoPerNotte() <= 0) {
            throw new IllegalArgumentException("Prezzo per notte deve essere maggiore di 0");
        }
        if (abitazione.getDataInizioDisponibilita().isAfter(abitazione.getDataFineDisponibilita())) {
            throw new IllegalArgumentException("Data inizio disponibilità deve essere prima della data fine");
        }
        if (abitazione.getIdUtente() <= 0) {
            throw new IllegalArgumentException("ID utente non valido");
        }
        
        if (!hostDao.isHost(abitazione.getIdUtente())) {
            hostDao.promoteToHost(abitazione.getIdUtente());
        }
        
        return abitazioneDao.create(abitazione);
    }
    
    public Abitazione getAbitazioneById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID abitazione non valido");
        }
        
        Abitazione abitazione = abitazioneDao.findById(id);
        if (abitazione == null) {
            throw new RuntimeException("Abitazione non trovata con ID: " + id);
        }
        return abitazione;
    }
    
    public List<Abitazione> getAllAbitazioni() {
        return abitazioneDao.findAll();
    }
    
    public List<Abitazione> getAbitazioniByUtente(long idUtente) {
        if (idUtente <= 0) {
            throw new IllegalArgumentException("ID utente non valido");
        }
        return abitazioneDao.findByUtente(idUtente);
    }
    
    public List<Abitazione> getAbitazioniByCodiceHost(String codiceHost) {
        if (codiceHost == null || codiceHost.isEmpty()) { 
            throw new IllegalArgumentException("Codice host è obbligatorio");
        }
        return abitazioneDao.findByCodiceHost(codiceHost);
    }
    
    public Abitazione updateAbitazione(long id, Abitazione abitazione) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID abitazione non valido");
        }
        
        Abitazione existing = abitazioneDao.findById(id);
        if (existing == null) {
            throw new RuntimeException("Abitazione non trovata con ID: " + id);
        }
        
        if (abitazione.getNome() == null || abitazione.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome abitazione è obbligatorio");
        }
        if (abitazione.getPrezzoPerNotte() <= 0) {
            throw new IllegalArgumentException("Prezzo per notte deve essere maggiore di 0");
        }
        if (abitazione.getDataInizioDisponibilita().isAfter(abitazione.getDataFineDisponibilita())) {
            throw new IllegalArgumentException("Data inizio disponibilità deve essere prima della data fine");
        }
        
        return abitazioneDao.update(id, abitazione);
    }
    
    public boolean disableAbitazione(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID abitazione non valido");
        }
        
        Abitazione abitazione = abitazioneDao.findById(id);
        if (abitazione == null) {
            throw new RuntimeException("Abitazione non trovata con ID: " + id);
        }
        
        Abitazione updated = new Abitazione(
            abitazione.getIdAbitazione(),
            abitazione.getIdUtente(),
            abitazione.getNome(),
            abitazione.getIndirizzo(),
            abitazione.getNumeroLocali(),
            abitazione.getNumeroPostiLetto(),
            abitazione.getPiano(),
            abitazione.getPrezzoPerNotte(),
            abitazione.getDataInizioDisponibilita(),
            abitazione.getDataFineDisponibilita(),
            false 
        );
        
        abitazioneDao.update(id, updated); 
        return true;
    }
    
    public boolean enableAbitazione(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID abitazione non valido");
        }
        
        Abitazione abitazione = abitazioneDao.findById(id);
        if (abitazione == null) {
            throw new RuntimeException("Abitazione non trovata con ID: " + id);
        }
        
        Abitazione updated = new Abitazione(
            abitazione.getIdAbitazione(),
            abitazione.getIdUtente(),
            abitazione.getNome(),
            abitazione.getIndirizzo(),
            abitazione.getNumeroLocali(),
            abitazione.getNumeroPostiLetto(),
            abitazione.getPiano(),
            abitazione.getPrezzoPerNotte(),
            abitazione.getDataInizioDisponibilita(),
            abitazione.getDataFineDisponibilita(),
            true  
        );
        
        abitazioneDao.update(id, updated);
        return true;
    }
    
    public Abitazione getAbitazionePiuGettonataUltimoMese() {
        return abitazioneDao.findAbitazionePiuGettonataUltimoMese();
    }
    
    public double getMediaPostiLetto() {
        return abitazioneDao.calcolaMediaPostiLetto();
    }
}