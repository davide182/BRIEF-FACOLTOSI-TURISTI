package com.facoltosituristi.config; 

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

import com.facoltosituristi.controller.AbitazioneController;
import com.facoltosituristi.controller.FeedbackController;
import com.facoltosituristi.controller.HostController;
import com.facoltosituristi.controller.PrenotazioneController;
import com.facoltosituristi.controller.UtenteController;

import io.javalin.Javalin;

public class WebConfig {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    private static Javalin app;

    public static void startServer() {
        UtenteController utenteController = new UtenteController();
        AbitazioneController abitazioneController = new AbitazioneController();
        HostController hostController = new HostController();
        PrenotazioneController prenotazioneController = new PrenotazioneController();
        FeedbackController feedbackController = new FeedbackController();
        
        app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        });

        app.get("/", ctx -> ctx.result("Backend attivato!"));
        
        app.get("/api/db-test", ctx -> {
            Map<String, String> response = new HashMap<>();
            try (Connection conn = DatabaseConfig.getConnection()) {
                response.put("status", "OK");
                response.put("database", conn.getCatalog());
                ctx.json(response);
            } catch (Exception e) {
                response.put("status", "ERRORE");
                response.put("message", e.getMessage());
                ctx.status(500).json(response);
            }
        });

        app.post("/api/create-tables", ctx -> {
            log.info("Richiesta creazione tabelle ricevuta");
            Map<String, String> response = new HashMap<>();
            try {
                DatabaseManager.createTables();
                response.put("status", "OK");
                response.put("message", "Tabelle create con successo");
                response.put("tables", "utente, host, abitazione, prenotazione, feedback");
                log.info("Tabelle create con successo");
                ctx.json(response);
            } catch (Exception e) {
                response.put("status", "ERROR");
                response.put("message", e.getMessage());
                response.put("detail", "Controlla i log del server per maggiori informazioni");
                log.error("Errore nella creazione delle tabelle: {}", e.getMessage(), e);
                ctx.status(500).json(response);
            }
        });

        app.post("/api/utenti", utenteController::create);
        app.get("/api/utenti", utenteController::getAll);
        app.get("/api/utenti/{id}", utenteController::getById);
        app.put("/api/utenti/{id}", utenteController::update);
        app.put("/api/utenti/{id}/disable", utenteController::disable);
        app.put("/api/utenti/{id}/enable", utenteController::enable);

        app.post("/api/abitazioni", abitazioneController::create);
        app.get("/api/abitazioni", abitazioneController::getAll);
        app.get("/api/abitazioni/{id}", abitazioneController::getById);
        app.get("/api/abitazioni/utente/{idUtente}", abitazioneController::getByUtente);
        app.get("/api/abitazioni/host/{codiceHost}", abitazioneController::getByCodiceHost);
        app.put("/api/abitazioni/{id}", abitazioneController::update);
        app.put("/api/abitazioni/{id}/disable", abitazioneController::disable);
        app.put("/api/abitazioni/{id}/enable", abitazioneController::enable);
        app.get("/api/abitazioni/statistiche/piu-gettonata", abitazioneController::getAbitazionePiuGettonataUltimoMese);
        app.get("/api/abitazioni/statistiche/media-posti-letto", abitazioneController::getMediaPostiLetto);

        app.post("/api/host/promuovi/{idUtente}", hostController::promoteToHost);
        app.get("/api/host/verifica/{idUtente}", hostController::isHost);
        app.get("/api/host/{id}", hostController::getById);
        app.get("/api/host/codice/{codiceHost}", hostController::getByCodiceHost);
        app.get("/api/host", hostController::getAll);
        app.get("/api/host/statistiche/super", hostController::getSuperHosts);
        app.get("/api/host/statistiche/prenotazioni-ultimo-mese", hostController::getHostsConPiuPrenotazioniUltimoMese);
        app.post("/api/host/verifica-super/{idUtente}", hostController::checkAndPromoteToSuperHost);

        app.post("/api/prenotazioni", prenotazioneController::create);
        app.get("/api/prenotazioni", prenotazioneController::getAll);
        app.get("/api/prenotazioni/{id}", prenotazioneController::getById);
        app.get("/api/prenotazioni/utente/{idUtente}", prenotazioneController::getByUtente);
        app.get("/api/prenotazioni/abitazione/{idAbitazione}", prenotazioneController::getByAbitazione);
        app.get("/api/prenotazioni/utente/{idUtente}/ultima", prenotazioneController::getUltimaPrenotazioneByUtente);
        app.put("/api/prenotazioni/{id}", prenotazioneController::update);
        app.put("/api/prenotazioni/{id}/stato", prenotazioneController::updateStato);
        app.put("/api/prenotazioni/{id}/cancella", prenotazioneController::cancel);
        app.get("/api/prenotazioni/statistiche/ultimo-mese", prenotazioneController::getPrenotazioniUltimoMese);
        app.get("/api/prenotazioni/statistiche/utente/{idUtente}/giorni-ultimo-mese", prenotazioneController::getGiorniPrenotatiUltimoMese);
        app.get("/api/prenotazioni/statistiche/utenti-top-5", prenotazioneController::getTop5UtentiGiorniPrenotatiUltimoMese);

        app.post("/api/feedback", feedbackController::create);
        app.get("/api/feedback", feedbackController::getAll);
        app.get("/api/feedback/{id}", feedbackController::getById);
        app.get("/api/feedback/prenotazione/{idPrenotazione}", feedbackController::getByPrenotazione);
        app.get("/api/feedback/abitazione/{idAbitazione}", feedbackController::getByAbitazione);
        app.get("/api/feedback/host/{idUtente}", feedbackController::getByHost);
        app.get("/api/feedback/statistiche/alto-punteggio", feedbackController::getConAltoPunteggio);
        app.get("/api/feedback/statistiche/host/{idUtente}/media-punteggio", feedbackController::getMediaPunteggioHost);
        app.put("/api/feedback/{id}", feedbackController::update);
        app.delete("/api/feedback/{id}", feedbackController::delete);

        app.start(8080);
        
        log.info("Server avviato su http://localhost:8080");
        log.info("Frontend React può connettersi a http://localhost:8080");
        
        log.info("Endpoint disponibili:");
        log.info("  - GET  /api/abitazioni/statistiche/piu-gettonata");
        log.info("  - GET  /api/abitazioni/statistiche/media-posti-letto");
        log.info("  - GET  /api/host/statistiche/super");
        log.info("  - GET  /api/host/statistiche/prenotazioni-ultimo-mese");
        log.info("  - GET  /api/prenotazioni/statistiche/utenti-top-5");
    }

    public static void stopServer() {
        if (app != null) {
            app.stop();
            log.info("Server Javalin fermato.");
        }
    }
}