package com.facoltosituristi.model;

import java.time.LocalDate;

public class Host {
    private long idUtente;  
    private String codiceHost;
    private boolean isSuperHost; 
    private LocalDate dataDiventatoSuper;
    private int totPrenotazioni;  

    public Host() {
    }  

    public Host(long idUtente, String codiceHost, boolean isSuperHost, LocalDate dataDiventatoSuper, int totPrenotazioni) {
        this.idUtente = idUtente;
        this.codiceHost = codiceHost;
        this.isSuperHost = isSuperHost;
        this.dataDiventatoSuper = dataDiventatoSuper;
        this.totPrenotazioni = totPrenotazioni;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public String getCodiceHost() {
        return codiceHost;
    }

    public boolean isSuperHost() { 
        return isSuperHost;
    }

    public LocalDate getDataDiventatoSuper() {
        return dataDiventatoSuper;
    }

    public int getTotPrenotazioni() {
        return totPrenotazioni;
    }

    public void setSuperHost(boolean superHost) {
        isSuperHost = superHost;
    }


    public void setDataDiventatoSuper(LocalDate dataDiventatoSuper) {
        this.dataDiventatoSuper = dataDiventatoSuper;
    }

    public void setTotPrenotazioni(int totPrenotazioni) {
        this.totPrenotazioni = totPrenotazioni;
    }
}