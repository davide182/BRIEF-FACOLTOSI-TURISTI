package com.facoltosituristi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facoltosituristi.config.DatabaseConfig;
import com.facoltosituristi.model.Prenotazione;
import com.facoltosituristi.statoprenotazione.StatoPrenotazione;

public class PrenotazioneDao {
    private static final Logger log = LoggerFactory.getLogger(PrenotazioneDao.class);
    
    public Prenotazione create(Prenotazione prenotazione) {
        String sql = "INSERT INTO prenotazione (idAbitazione, idUtente, dataInizioPrenotazione, dataFinePrenotazione, stato, prezzoTotale) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, prenotazione.getIdAbitazione());
            pstmt.setLong(2, prenotazione.getIdUtente());
            pstmt.setDate(3, java.sql.Date.valueOf(prenotazione.getDataInizioPrenotazione()));
            pstmt.setDate(4, java.sql.Date.valueOf(prenotazione.getDataFinePrenotazione()));
            pstmt.setString(5, prenotazione.getStato().name());
            pstmt.setDouble(6, prenotazione.getPrezzoTotale());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long idPrenotazione = rs.getLong(1);
                        
                        aggiornaContatoreHost(prenotazione.getIdAbitazione());
                        
                        Prenotazione created = new Prenotazione(
                            idPrenotazione,
                            prenotazione.getIdAbitazione(),
                            prenotazione.getIdUtente(),
                            prenotazione.getDataInizioPrenotazione(),
                            prenotazione.getDataFinePrenotazione(),
                            prenotazione.getStato(),
                            prenotazione.getPrezzoTotale()
                        );
                        
                        log.info("Prenotazione creata: ID {} per abitazione ID {} e utente ID {}",idPrenotazione, prenotazione.getIdAbitazione(), prenotazione.getIdUtente());
                        return created;
                    }
                }
            }
            throw new RuntimeException("Impossibile creare prenotazione");
            
        } catch (SQLException e) {
            log.error("Errore nella creazione della prenotazione: {}", e.getMessage());
            throw new RuntimeException("Errore nella creazione della prenotazione", e);
        }
    }
    
    public Prenotazione findById(long idPrenotazione) {
        String sql = "SELECT * FROM prenotazione WHERE idPrenotazione = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idPrenotazione);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPrenotazione(rs);
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare prenotazione con ID {}: {}", idPrenotazione, e.getMessage());
            throw new RuntimeException("Errore nel recupero della prenotazione", e);
        }
    }
    
    public List<Prenotazione> findAll() {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM prenotazione ORDER BY dataPrenotazione DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
            log.info("Trovate {} prenotazioni", prenotazioni.size());
            return prenotazioni;
            
        } catch (SQLException e) {
            log.error("Errore nel recupero di tutte le prenotazioni: {}", e.getMessage());
            throw new RuntimeException("Errore nel recupero delle prenotazioni", e);
        }
    }
    
    public List<Prenotazione> findByUtente(long idUtente) {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM prenotazione WHERE idUtente = ? ORDER BY dataPrenotazione DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idUtente);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
            log.info("Trovate {} prenotazioni per utente ID {}", prenotazioni.size(), idUtente);
            return prenotazioni;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare prenotazioni per utente {}: {}", idUtente, e.getMessage());
            throw new RuntimeException("Errore nel recupero prenotazioni utente", e);
        }
    }
    
    public List<Prenotazione> findByAbitazione(long idAbitazione) {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM prenotazione WHERE idAbitazione = ? ORDER BY dataPrenotazione DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idAbitazione);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
            log.info("Trovate {} prenotazioni per abitazione ID {}", prenotazioni.size(), idAbitazione);
            return prenotazioni;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare prenotazioni per abitazione {}: {}", idAbitazione, e.getMessage());
            throw new RuntimeException("Errore nel recupero prenotazioni abitazione", e);
        }
    }
    
    public Prenotazione findUltimaPrenotazioneByUtente(long idUtente) {
        String sql = "SELECT * FROM prenotazione WHERE idUtente = ? ORDER BY dataPrenotazione DESC LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idUtente);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                log.info("Ultima prenotazione trovata per utente ID {}", idUtente);
                return mapResultSetToPrenotazione(rs);
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare ultima prenotazione per utente {}: {}", idUtente, e.getMessage());
            throw new RuntimeException("Errore nel recupero ultima prenotazione utente", e);
        }
    }
    
    public List<Prenotazione> findPrenotazioniUltimoMese() {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM prenotazione WHERE dataInizioPrenotazione >= CURRENT_DATE - INTERVAL '1 month' ORDER BY dataPrenotazione DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
            log.info("Trovate {} prenotazioni nell'ultimo mese", prenotazioni.size());
            return prenotazioni;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare prenotazioni dell'ultimo mese: {}", e.getMessage());
            throw new RuntimeException("Errore nel recupero prenotazioni ultimo mese", e);
        }
    }
    
    public Prenotazione update(long idPrenotazione, Prenotazione prenotazione) {
        String sql = """
            UPDATE prenotazione 
            SET idAbitazione = ?, idUtente = ?, dataInizioPrenotazione = ?, 
                dataFinePrenotazione = ?, stato = ?, prezzoTotale = ?, 
                cancellataDalUtente = ? 
            WHERE idPrenotazione = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, prenotazione.getIdAbitazione());
            pstmt.setLong(2, prenotazione.getIdUtente());
            pstmt.setDate(3, java.sql.Date.valueOf(prenotazione.getDataInizioPrenotazione()));
            pstmt.setDate(4, java.sql.Date.valueOf(prenotazione.getDataFinePrenotazione()));
            pstmt.setString(5, prenotazione.getStato().name());
            pstmt.setDouble(6, prenotazione.getPrezzoTotale());
            pstmt.setBoolean(7, prenotazione.isCancellataDalUtente());
            pstmt.setLong(8, idPrenotazione);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                log.info("Prenotazione aggiornata ID: {}", idPrenotazione);
                
                if (prenotazione.getStato() == StatoPrenotazione.COMPLETATA) {
                    aggiornaContatoreHost(prenotazione.getIdAbitazione());
                }
                
                return new Prenotazione(
                    idPrenotazione,
                    prenotazione.getIdAbitazione(),
                    prenotazione.getIdUtente(),
                    prenotazione.getDataInizioPrenotazione(),
                    prenotazione.getDataFinePrenotazione(),
                    prenotazione.getStato(),
                    prenotazione.getPrezzoTotale()
                );
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nell'aggiornamento della prenotazione con ID {}: {}", idPrenotazione, e.getMessage());
            throw new RuntimeException("Errore nell'aggiornamento della prenotazione", e);
        }
    }
    
    public boolean updateStato(long idPrenotazione, StatoPrenotazione nuovoStato) {
        String sql = "UPDATE prenotazione SET stato = ? WHERE idPrenotazione = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuovoStato.name());
            pstmt.setLong(2, idPrenotazione);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                log.info("Stato prenotazione aggiornato ID {}: {}", idPrenotazione, nuovoStato);
                
                if (nuovoStato == StatoPrenotazione.COMPLETATA) {
                    Prenotazione prenotazione = findById(idPrenotazione);
                    if (prenotazione != null) {
                        aggiornaContatoreHost(prenotazione.getIdAbitazione());
                    }
                }
                
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            log.error("Errore nell'aggiornamento dello stato della prenotazione con ID {}: {}", idPrenotazione, e.getMessage());
            throw new RuntimeException("Errore nell'aggiornamento dello stato della prenotazione", e);
        }
    }
    
    public boolean softDelete(long idPrenotazione) {
        String sql = "UPDATE prenotazione SET cancellataDalUtente = true, stato = 'CANCELLATA' WHERE idPrenotazione = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idPrenotazione);
            int affectedRows = pstmt.executeUpdate();
            
            boolean deleted = affectedRows > 0;
            if (deleted) {
                log.info("Prenotazione cancellata ID: {}", idPrenotazione);
            }
            return deleted;
            
        } catch (SQLException e) {
            log.error("Errore nella cancellazione della prenotazione con ID {}: {}", idPrenotazione, e.getMessage());
            throw new RuntimeException("Errore nella cancellazione della prenotazione", e);
        }
    }
    
    public int calcolaGiorniPrenotatiUltimoMese(long idUtente) {
        String sql = """
            SELECT SUM(DATE_PART('day', dataFinePrenotazione - dataInizioPrenotazione)) as giorni_totali
            FROM prenotazione 
            WHERE idUtente = ? 
              AND dataInizioPrenotazione >= CURRENT_DATE - INTERVAL '1 month'
              AND stato != 'CANCELLATA'
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idUtente);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int giorni = rs.getInt("giorni_totali");
                log.info("Utente ID {} ha {} giorni prenotati nell'ultimo mese", idUtente, giorni);
                return giorni;
            }
            return 0;
            
        } catch (SQLException e) {
            log.error("Errore nel calcolo giorni prenotati per utente {}: {}", idUtente, e.getMessage());
            throw new RuntimeException("Errore nel calcolo giorni prenotati", e);
        }
    }
    
    private void aggiornaContatoreHost(long idAbitazione) {
        String sql = "SELECT idUtente FROM abitazione WHERE idAbitazione = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idAbitazione);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                long idUtente = rs.getLong("idUtente");
                
                HostDao hostDao = new HostDao();
                hostDao.aggiornaContatorePrenotazioni(idUtente);
            }
            
        } catch (SQLException e) {
            log.error("Errore nell'aggiornamento contatore host per abitazione {}: {}", idAbitazione, e.getMessage());
        }
    }

    public List<Map<String, Object>> findTop5UtentiGiorniPrenotatiUltimoMese() {
        List<Map<String, Object>> risultati = new ArrayList<>();
        String sql = """
            SELECT 
                u.idUtente,
                u.nome,
                u.cognome,
                u.email,
                SUM(DATE_PART('day', p.dataFinePrenotazione - p.dataInizioPrenotazione)) as giorni_totali,
                COUNT(p.idPrenotazione) as num_prenotazioni
            FROM utente u
            JOIN prenotazione p ON u.idUtente = p.idUtente
            WHERE p.dataInizioPrenotazione >= CURRENT_DATE - INTERVAL '1 month'
              AND p.stato != 'CANCELLATA'
              AND u.attivo = true
            GROUP BY u.idUtente, u.nome, u.cognome, u.email
            ORDER BY giorni_totali DESC
            LIMIT 5
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> utenteStat = new HashMap<>();
                utenteStat.put("idUtente", rs.getLong("idUtente"));
                utenteStat.put("nome", rs.getString("nome"));
                utenteStat.put("cognome", rs.getString("cognome"));
                utenteStat.put("email", rs.getString("email"));
                utenteStat.put("giorniTotali", rs.getInt("giorni_totali"));
                utenteStat.put("numPrenotazioni", rs.getInt("num_prenotazioni"));
                
                risultati.add(utenteStat);
            }
            log.info("Trovati {} utenti top per giorni prenotati ultimo mese nel DB", risultati.size());
            return risultati;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare top 5 utenti per giorni prenotati ultimo mese nel DB: {}", e.getMessage());
            throw new RuntimeException("Errore nel recupero top 5 utenti giorni prenotati", e);
        }
    }
    
    private Prenotazione mapResultSetToPrenotazione(ResultSet rs) throws SQLException {
        LocalDateTime dataPrenotazione = rs.getTimestamp("dataPrenotazione") != null 
            ? rs.getTimestamp("dataPrenotazione").toLocalDateTime() 
            : null;
        
        return new Prenotazione(
            rs.getLong("idPrenotazione"),
            rs.getLong("idAbitazione"),
            rs.getLong("idUtente"),
            rs.getDate("dataInizioPrenotazione").toLocalDate(),
            rs.getDate("dataFinePrenotazione").toLocalDate(),
            StatoPrenotazione.valueOf(rs.getString("stato")),
            rs.getDouble("prezzoTotale")
        );
    }
}