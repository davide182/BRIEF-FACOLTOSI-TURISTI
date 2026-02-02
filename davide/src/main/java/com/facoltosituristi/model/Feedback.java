package com.facoltosituristi.model;

import java.time.LocalDateTime;

public class Feedback {
    private long idFeedback;
    private long idPrenotazione;
    private String titolo;
    private String testo;
    private Integer punteggio;
    private LocalDateTime dataCreazione;

    public Feedback() {
    }

    public Feedback(long idFeedback,long idPrenotazione, String titolo, String testo, Integer punteggio, LocalDateTime dataCreazione) {
        this.idFeedback = idFeedback;
        this.idPrenotazione = idPrenotazione;
        this.titolo = titolo;
        this.testo = testo;
        this.punteggio = punteggio;
        this.dataCreazione = dataCreazione;
    }

    public long getIdFeedback() {
        return idFeedback;
    }

    public long getIdPrenotazione() {
        return idPrenotazione;
    }
    public String getTitolo() {
        return titolo;
    }

    public String getTesto() {
        return testo;
    }

    public Integer getPunteggio() {
        return punteggio;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setTitolo(String titolo){
        this.titolo = titolo;
    }

    public void setTesto(String testo){
        this.testo = testo;
    }

    public void setPunteggio(Integer punteggio){
        this.punteggio = punteggio;
    }

    public void setDataCreazione(LocalDateTime dataCreazione){
        this.dataCreazione = dataCreazione;
    }
}