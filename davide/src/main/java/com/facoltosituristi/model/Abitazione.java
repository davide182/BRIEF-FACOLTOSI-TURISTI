package com.facoltosituristi.model;

import java.time.LocalDate;

public class Abitazione {
    private long idAbitazione;
    private long idUtente;
    private String nome;
    private String indirizzo;
    private Integer numeroLocali;
    private Integer numeroPostiLetto;
    private Integer piano;
    private double prezzoPerNotte;
    private LocalDate dataInizioDisponibilita;
    private LocalDate dataFineDisponibilita;
    private boolean disponibile;

    public Abitazione() {
    }

    public Abitazione(long idAbitazione, long idUtente, String nome, String indirizzo, Integer numeroLocali, Integer numeroPostiLetto, Integer piano,double prezzoPerNotte,LocalDate dataInizioDisponibilita,LocalDate dataFineDisponibilita) {
        this.idAbitazione = idAbitazione;
        this.idUtente = idUtente;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.numeroLocali = numeroLocali;
        this.numeroPostiLetto = numeroPostiLetto;
        this.piano = piano;
        this.prezzoPerNotte = prezzoPerNotte;
        this.dataInizioDisponibilita = dataInizioDisponibilita;
        this.dataFineDisponibilita = dataFineDisponibilita;
        this.disponibile = true;
    }

    public Abitazione(long idAbitazione, long idUtente, String nome, String indirizzo, Integer numeroLocali, Integer numeroPostiLetto, Integer piano,double prezzoPerNotte,LocalDate dataInizioDisponibilita,LocalDate dataFineDisponibilita, boolean disponibile) {
        this.idAbitazione = idAbitazione;
        this.idUtente = idUtente;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.numeroLocali = numeroLocali;
        this.numeroPostiLetto = numeroPostiLetto;
        this.piano = piano;
        this.prezzoPerNotte = prezzoPerNotte;
        this.dataInizioDisponibilita = dataInizioDisponibilita;
        this.dataFineDisponibilita = dataFineDisponibilita;
        this.disponibile = disponibile;
    }

    public long getIdAbitazione() {
        return idAbitazione;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public String getNome() {
        return nome;
    }


    public String getIndirizzo() {
        return indirizzo;
    }

    public Integer getNumeroLocali() {
        return numeroLocali;
    }

    public Integer getNumeroPostiLetto() {
        return numeroPostiLetto;
    }

    public Integer getPiano() {
        return piano;
    }

    public double getPrezzoPerNotte() {
        return prezzoPerNotte;
    }

    public LocalDate getDataInizioDisponibilita() {
        return dataInizioDisponibilita;
    }

    public LocalDate getDataFineDisponibilita() {
        return dataFineDisponibilita;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public String setNome(String nome) {
        return this.nome = nome;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void setNumeroLocali(Integer numeroLocali) {
        this.numeroLocali = numeroLocali;
    }

    public void setNumeroPostiLetto(Integer numeroPostiLetto) {
        this.numeroPostiLetto = numeroPostiLetto;
    }   

    public void setPiano(Integer piano) {
        this.piano = piano;
    }

    public void setPrezzoPerNotte(double prezzoPerNotte) {
        this.prezzoPerNotte = prezzoPerNotte;
    }

    public void setDataInizioDisponibilita(LocalDate dataInizioDisponibilita) {
        this.dataInizioDisponibilita = dataInizioDisponibilita;
    }

    public void setDataFineDisponibilita(LocalDate dataFineDisponibilita) {
        this.dataFineDisponibilita = dataFineDisponibilita;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }
}