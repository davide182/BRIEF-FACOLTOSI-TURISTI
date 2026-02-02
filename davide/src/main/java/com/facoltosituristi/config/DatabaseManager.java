package com.facoltosituristi.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String[] CREATE_TABLES_SQL = {
        """
        CREATE TABLE IF NOT EXISTS utente (
            idUtente SERIAL PRIMARY KEY,
            nome VARCHAR(100) NOT NULL,
            cognome VARCHAR(100) NOT NULL,
            email VARCHAR(100) UNIQUE NOT NULL,
            indirizzo VARCHAR(100) NOT NULL,
            data_registrazione DATE DEFAULT CURRENT_DATE,
            attivo BOOLEAN DEFAULT TRUE
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS host (
            idUtente INTEGER PRIMARY KEY REFERENCES utente(idUtente) ON DELETE RESTRICT,
            codiceHost VARCHAR(50) UNIQUE NOT NULL,
            isSuperHost BOOLEAN DEFAULT FALSE,
            dataDiventatoSuper DATE,
            totPrenotazioni INTEGER DEFAULT 0
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS abitazione (
            idAbitazione SERIAL PRIMARY KEY,
            idUtente INTEGER NOT NULL REFERENCES utente(idUtente) ON DELETE RESTRICT,
            nome VARCHAR(150) NOT NULL,
            indirizzo VARCHAR(100) NOT NULL,
            numeroLocali INTEGER NOT NULL,
            numeroPostiLetto INTEGER NOT NULL,
            piano INTEGER,
            prezzoPerNotte DECIMAL(10,2) NOT NULL,
            dataInizioDisponibilita DATE NOT NULL,
            dataFineDisponibilita DATE NOT NULL,
            disponibile BOOLEAN DEFAULT TRUE,
            CHECK (dataInizioDisponibilita <= dataFineDisponibilita),
            CHECK (prezzoPerNotte > 0),
            CHECK (numeroPostiLetto > 0)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS prenotazione (
            idPrenotazione SERIAL PRIMARY KEY,
            idAbitazione INTEGER NOT NULL REFERENCES abitazione(idAbitazione) ON DELETE RESTRICT,
            idUtente INTEGER NOT NULL REFERENCES utente(idUtente) ON DELETE RESTRICT,
            dataInizioPrenotazione DATE NOT NULL,
            dataFinePrenotazione DATE NOT NULL,
            stato VARCHAR(20) NOT NULL DEFAULT 'IN_ATTESA' 
                CHECK (stato IN ('IN_ATTESA', 'CONFERMATA', 'CANCELLATA', 'COMPLETATA')),
            dataPrenotazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            prezzoTotale DECIMAL(10,2) NOT NULL,
            cancellataDalUtente BOOLEAN DEFAULT FALSE,
            CHECK (dataInizioPrenotazione < dataFinePrenotazione),
            CHECK (prezzoTotale > 0)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS feedback (
            idFeedback SERIAL PRIMARY KEY,
            idPrenotazione INTEGER UNIQUE NOT NULL REFERENCES prenotazione(idPrenotazione) ON DELETE RESTRICT,
            titolo VARCHAR(200) NOT NULL,
            testo TEXT NOT NULL,
            punteggio INTEGER NOT NULL CHECK (punteggio BETWEEN 1 AND 5),
            dataCreazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        """
    };
    
    public static void createTables() {
        log.info("CREAZIONE TABELLE..");
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : CREATE_TABLES_SQL) {
                try {
                    stmt.executeUpdate(sql);
                    log.info("TABELLA CREATA O GIÀ ESISTENTE");
                } catch (SQLException e) {
                    log.error("CREAZIONE TABELLA FALLITA: {}", e.getMessage());
                    log.error("Query fallita: {}", sql);
                }
            }
            
            log.info("Creazione tabelle completata");
            
        } catch (SQLException e) {
            log.error("CONNESSIONE AL DATABASE FALLITA: {}", e.getMessage(), e);
            throw new RuntimeException("ERRORE CONNESSIONE DATABASE", e);
        }
    }
}