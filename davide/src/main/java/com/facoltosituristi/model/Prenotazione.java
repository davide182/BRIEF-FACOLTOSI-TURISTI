package com.facoltosituristi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.facoltosituristi.statoprenotazione.StatoPrenotazione;

public class Prenotazione {
    private long idPrenotazione;
    private long idAbitazione;
    private long idUtente;
    private LocalDate dataInizioPrenotazione;
    private LocalDate dataFinePrenotazione;
    private StatoPrenotazione stato;
    private LocalDateTime dataPrenotazione;
    private double prezzoTotale;
    private boolean cancellataDalUtente;;

    public Prenotazione() {
    }

    public Prenotazione(long idPrenotazione, long idAbitazione, long idUtente,LocalDate dataInizioPrenotazione, LocalDate dataFinePrenotazione,StatoPrenotazione stato, double prezzoTotale) {
        this.idPrenotazione = idPrenotazione;
        this.idAbitazione = idAbitazione;
        this.idUtente = idUtente;
        this.dataInizioPrenotazione = dataInizioPrenotazione;
        this.dataFinePrenotazione = dataFinePrenotazione;
        this.stato = stato;
        this.prezzoTotale = prezzoTotale;
        this.cancellataDalUtente = false;
        this.dataPrenotazione = LocalDateTime.now();
    }

    public Prenotazione(long idPrenotazione, long idAbitazione, long idUtente,LocalDate dataInizioPrenotazione, LocalDate dataFinePrenotazione,StatoPrenotazione stato, double prezzoTotale, boolean cancellataDalUtente) {
        this.idPrenotazione = idPrenotazione;
        this.idAbitazione = idAbitazione;
        this.idUtente = idUtente;
        this.dataInizioPrenotazione = dataInizioPrenotazione;
        this.dataFinePrenotazione = dataFinePrenotazione;
        this.stato = stato;
        this.prezzoTotale = prezzoTotale;
        this.cancellataDalUtente = cancellataDalUtente;
        this.dataPrenotazione = LocalDateTime.now();
    }

    public long getIdPrenotazione() {
        return idPrenotazione;
    }

    public long getIdAbitazione() {
        return idAbitazione;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public LocalDate getDataInizioPrenotazione() {
        return dataInizioPrenotazione;
    }

    public LocalDate getDataFinePrenotazione() {
        return dataFinePrenotazione;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }

    public LocalDateTime getDataPrenotazione() {
        return dataPrenotazione;
    }

    public double getPrezzoTotale() {
        return prezzoTotale;
    }

    public boolean isCancellataDalUtente() {
        return cancellataDalUtente;
    }

    public void setDataInizioDisponibilita(LocalDate dataInizioPrenotazione) {
        this.dataInizioPrenotazione = dataInizioPrenotazione;
    }

    public void setDataFinePrenotazione(LocalDate dataFinePrenotazione) {
        this.dataFinePrenotazione = dataFinePrenotazione;
    }

    public void setStato(StatoPrenotazione stato) {
        this.stato = stato;
    }

    public void setPrezzoTotale(double prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }

    public void setCancellataDalUtente(boolean cancellataDalUtente) {
        this.cancellataDalUtente = cancellataDalUtente;
    }
}