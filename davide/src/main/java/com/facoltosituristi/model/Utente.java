package com.facoltosituristi.model;

public class Utente {
    private long idUtente;
    private String nome;
    private String cognome;
    private String email;
    private String indirizzo;
    private boolean attivo;

    public Utente() {
    }

    public Utente(long idUtente, String nome, String cognome, String email, String indirizzo) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.indirizzo = indirizzo;
        this.attivo = true;
    }

        
    public Utente(long idUtente, String nome, String cognome, String email, String indirizzo, boolean attivo) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.indirizzo = indirizzo;
        this.attivo = attivo;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }
}