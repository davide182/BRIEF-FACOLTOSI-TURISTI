package com.facoltosituristi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facoltosituristi.config.DatabaseConfig;
import com.facoltosituristi.config.DatabaseManager;
import com.facoltosituristi.config.WebConfig;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("=======================");
        log.info(" TURISTA FACOLTOSO - Backend");
        log.info("=======================");
        
        try {
            log.info("Caricamento configurazione database...");
            DatabaseConfig.loadConfig();
            
            log.info(" Test connessione database...");
            if (!DatabaseConfig.testConnection()) {
                log.error(" Database non raggiungibile!");
                System.exit(1);
            }
            
            log.info("Database connesso con successo");
            
            log.info(" Creazione tabelle database...");
            DatabaseManager.createTables();
            log.info("Tabelle create con successo");
            
            WebConfig.startServer();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Arresto applicazione in corso...");
                WebConfig.stopServer();
                log.info("Applicazione chiusa correttamente");
            }));
            
            log.info("Applicazione pronta!");
            log.info("Premi CTRL+C per fermare");
            log.info("Frontend React può connettersi a http://localhost:8080");
            
        } catch (Exception e) {
            log.error("Errore durante l'avvio: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}