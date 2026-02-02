package com.facoltosituristi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facoltosituristi.config.DatabaseConfig;
import com.facoltosituristi.model.Utente;

public class UtenteDao {
    private static final Logger log = LoggerFactory.getLogger(UtenteDao.class);
    
    public Utente create(Utente utente) {
        String sql = "INSERT INTO utente (nome, cognome, email, indirizzo) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getEmail());
            pstmt.setString(4, utente.getIndirizzo());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        utente = new Utente(
                            rs.getLong(1),
                            utente.getNome(),
                            utente.getCognome(),
                            utente.getEmail(),
                            utente.getIndirizzo(),
                            true  
                        );
                        log.info("Utente creato con ID: {}", utente.getIdUtente());
                    }
                }
            }
            return utente;
            
        } catch (SQLException e) {
            log.error("Errore nella creazione dell'utente: {}", e.getMessage());
            throw new RuntimeException("Errore nella creazione dell'utente", e);
        }
    }
    
    public Utente findById(long id) {
        String sql = "SELECT * FROM utente WHERE idUtente = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUtente(rs);
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare utente con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Errore nel recupero dell'utente", e);
        }
    }
    
    public List<Utente> findAll() {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM utente WHERE attivo = true ORDER BY idUtente";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utenti.add(mapResultSetToUtente(rs));
            }
            log.info("Trovati {} utenti attivi", utenti.size());
            return utenti;
            
        } catch (SQLException e) {
            log.error("Errore nel recupero di tutti gli utenti: {}", e.getMessage());
            throw new RuntimeException("Errore nel recupero degli utenti", e);
        }
    }
    
    public List<Utente> findAllConDisabilitati() {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM utente ORDER BY idUtente";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utenti.add(mapResultSetToUtente(rs));
            }
            log.info("Trovati {} utenti (totali)", utenti.size());
            return utenti;
            
        } catch (SQLException e) {
            log.error("Errore nel recupero di tutti gli utenti: {}", e.getMessage());
            throw new RuntimeException("Errore nel recupero degli utenti", e);
        }
    }
    
    public Utente update(long id, Utente utente) {
        String sql = "UPDATE utente SET nome = ?, cognome = ?, email = ?, indirizzo = ?, attivo = ? WHERE idUtente = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getEmail());
            pstmt.setString(4, utente.getIndirizzo());
            pstmt.setBoolean(5, utente.isAttivo());
            pstmt.setLong(6, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                log.info("Utente aggiornato con ID: {}", id);
                return new Utente(id, utente.getNome(), utente.getCognome(), utente.getEmail(), utente.getIndirizzo(), utente.isAttivo());
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nell'aggiornamento dell'utente con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Errore nell'aggiornamento dell'utente", e);
        }
    }
    
    public boolean delete(long id) {
        String sql = "DELETE FROM utente WHERE idUtente = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            boolean deleted = affectedRows > 0;
            if (deleted) {
                log.info("Utente eliminato con ID: {}", id);
            }
            return deleted;
            
        } catch (SQLException e) {
            log.error("Errore nell'eliminazione dell'utente con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Errore nell'eliminazione dell'utente", e);
        }
    }
    
    public Utente findByEmail(String email) {
        String sql = "SELECT * FROM utente WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUtente(rs);
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare utente con email {}: {}", email, e.getMessage());
            throw new RuntimeException("Errore nel recupero dell'utente per email", e);
        }
    }
    
    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        return new Utente(
            rs.getLong("idUtente"),
            rs.getString("nome"),
            rs.getString("cognome"),
            rs.getString("email"),
            rs.getString("indirizzo"),
            rs.getBoolean("attivo")
        );
    }
}