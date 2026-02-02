package com.facoltosituristi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facoltosituristi.config.DatabaseConfig;
import com.facoltosituristi.model.Feedback;

public class FeedbackDao {
    private static final Logger log = LoggerFactory.getLogger(FeedbackDao.class);
    
    public Feedback create(Feedback feedback) {
        String sql = "INSERT INTO feedback (idPrenotazione, titolo, testo, punteggio) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, feedback.getIdPrenotazione());
            pstmt.setString(2, feedback.getTitolo());
            pstmt.setString(3, feedback.getTesto());
            pstmt.setInt(4, feedback.getPunteggio());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long idFeedback = rs.getLong(1);
                        
                        Feedback created = new Feedback(
                            idFeedback,
                            feedback.getIdPrenotazione(),
                            feedback.getTitolo(),
                            feedback.getTesto(),
                            feedback.getPunteggio(),
                            LocalDateTime.now()
                        );
                        
                        log.info("Feedback creato: ID {} per prenotazione ID {}", idFeedback, feedback.getIdPrenotazione());
                        return created;
                    }
                }
            }
            throw new RuntimeException("Impossibile creare feedback");
            
        } catch (SQLException e) {
            log.error("Errore nella creazione del feedback: {}", e.getMessage());
            throw new RuntimeException("Errore nella creazione del feedback", e);
        }
    }
    
    public Feedback findById(long idFeedback) {
        String sql = "SELECT * FROM feedback WHERE idFeedback = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idFeedback);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToFeedback(rs);
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare feedback con ID {}: {}", idFeedback, e.getMessage());
            throw new RuntimeException("Errore nel recupero del feedback", e);
        }
    }
    
    public Feedback findByPrenotazione(long idPrenotazione) {
        String sql = "SELECT * FROM feedback WHERE idPrenotazione = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idPrenotazione);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToFeedback(rs);
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare feedback per prenotazione {}: {}", idPrenotazione, e.getMessage());
            throw new RuntimeException("Errore nel recupero del feedback per prenotazione", e);
        }
    }
    
    public List<Feedback> findAll() {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM feedback ORDER BY dataCreazione DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                feedbacks.add(mapResultSetToFeedback(rs));
            }
            log.info("Trovati {} feedback", feedbacks.size());
            return feedbacks;
            
        } catch (SQLException e) {
            log.error("Errore nel recupero di tutti i feedback: {}", e.getMessage());
            throw new RuntimeException("Errore nel recupero dei feedback", e);
        }
    }
    
    public List<Feedback> findByAbitazione(long idAbitazione) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = """
            SELECT f.* 
            FROM feedback f
            JOIN prenotazione p ON f.idPrenotazione = p.idPrenotazione
            WHERE p.idAbitazione = ?
            ORDER BY f.dataCreazione DESC
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idAbitazione);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                feedbacks.add(mapResultSetToFeedback(rs));
            }
            log.info("Trovati {} feedback per abitazione ID {}", feedbacks.size(), idAbitazione);
            return feedbacks;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare feedback per abitazione {}: {}", idAbitazione, e.getMessage());
            throw new RuntimeException("Errore nel recupero feedback abitazione", e);
        }
    }
    
    public List<Feedback> findByHost(long idUtente) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = """
            SELECT f.* 
            FROM feedback f
            JOIN prenotazione p ON f.idPrenotazione = p.idPrenotazione
            JOIN abitazione a ON p.idAbitazione = a.idAbitazione
            WHERE a.idUtente = ?
            ORDER BY f.dataCreazione DESC
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idUtente);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                feedbacks.add(mapResultSetToFeedback(rs));
            }
            log.info("Trovati {} feedback per host ID {}", feedbacks.size(), idUtente);
            return feedbacks;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare feedback per host {}: {}", idUtente, e.getMessage());
            throw new RuntimeException("Errore nel recupero feedback host", e);
        }
    }
    
    public List<Feedback> findConAltoPunteggio(int minimoPunteggio) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE punteggio >= ? ORDER BY dataCreazione DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, minimoPunteggio);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                feedbacks.add(mapResultSetToFeedback(rs));
            }
            log.info("Trovati {} feedback con punteggio >= {}", feedbacks.size(), minimoPunteggio);
            return feedbacks;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare feedback con punteggio alto: {}", e.getMessage());
            throw new RuntimeException("Errore nel recupero feedback punteggio alto", e);
        }
    }
    
    public double calcolaMediaPunteggioHost(long idUtente) {
        String sql = """
            SELECT AVG(f.punteggio) as media_punteggio
            FROM feedback f
            JOIN prenotazione p ON f.idPrenotazione = p.idPrenotazione
            JOIN abitazione a ON p.idAbitazione = a.idAbitazione
            WHERE a.idUtente = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idUtente);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double media = rs.getDouble("media_punteggio");
                log.info("Media punteggio per host ID {}: {}", idUtente, media);
                return media;
            }
            return 0.0;
            
        } catch (SQLException e) {
            log.error("Errore nel calcolo media punteggio per host {}: {}", idUtente, e.getMessage());
            throw new RuntimeException("Errore nel calcolo media punteggio host", e);
        }
    }
    
    public Feedback update(long idFeedback, Feedback feedback) {
        String sql = "UPDATE feedback SET titolo = ?, testo = ?, punteggio = ? WHERE idFeedback = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, feedback.getTitolo());
            pstmt.setString(2, feedback.getTesto());
            pstmt.setInt(3, feedback.getPunteggio());
            pstmt.setLong(4, idFeedback);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                log.info("Feedback aggiornato ID: {}", idFeedback);
                return new Feedback(
                    idFeedback,
                    feedback.getIdPrenotazione(),
                    feedback.getTitolo(),
                    feedback.getTesto(),
                    feedback.getPunteggio(),
                    feedback.getDataCreazione()
                );
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nell'aggiornamento del feedback con ID {}: {}", idFeedback, e.getMessage());
            throw new RuntimeException("Errore nell'aggiornamento del feedback", e);
        }
    }
    
    public boolean delete(long idFeedback) {
        String sql = "DELETE FROM feedback WHERE idFeedback = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idFeedback);
            int affectedRows = pstmt.executeUpdate();
            
            boolean deleted = affectedRows > 0;
            if (deleted) {
                log.info("Feedback eliminato ID: {}", idFeedback);
            }
            return deleted;
            
        } catch (SQLException e) {
            log.error("Errore nell'eliminazione del feedback con ID {}: {}", idFeedback, e.getMessage());
            throw new RuntimeException("Errore nell'eliminazione del feedback", e);
        }
    }
    
    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        LocalDateTime dataCreazione = rs.getTimestamp("dataCreazione") != null 
            ? rs.getTimestamp("dataCreazione").toLocalDateTime() 
            : null;
        
        return new Feedback(
            rs.getLong("idFeedback"),
            rs.getLong("idPrenotazione"),
            rs.getString("titolo"),
            rs.getString("testo"),
            rs.getInt("punteggio"),
            dataCreazione
        );
    }
}