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
import com.facoltosituristi.model.Abitazione;

public class AbitazioneDao {
    private static final Logger log = LoggerFactory.getLogger(AbitazioneDao.class);

    public Abitazione create(Abitazione abitazione) {
        String sql = "INSERT INTO abitazione (idUtente, nome, indirizzo, numeroLocali, numeroPostiLetto,piano, prezzoPerNotte, dataInizioDisponibilita, dataFineDisponibilita)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, abitazione.getIdUtente());
            pstmt.setString(2, abitazione.getNome());
            pstmt.setString(3, abitazione.getIndirizzo());
            pstmt.setInt(4, abitazione.getNumeroLocali());
            pstmt.setInt(5, abitazione.getNumeroPostiLetto());
            
            if (abitazione.getPiano() != null) {
                pstmt.setInt(6, abitazione.getPiano());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            pstmt.setDouble(7, abitazione.getPrezzoPerNotte());
            pstmt.setDate(8, java.sql.Date.valueOf(abitazione.getDataInizioDisponibilita()));
            pstmt.setDate(9, java.sql.Date.valueOf(abitazione.getDataFineDisponibilita()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long idAbitazione = rs.getLong(1);
                        
                        Abitazione created = new Abitazione(
                            idAbitazione,
                            abitazione.getIdUtente(),
                            abitazione.getNome(),
                            abitazione.getIndirizzo(),
                            abitazione.getNumeroLocali(),
                            abitazione.getNumeroPostiLetto(),
                            abitazione.getPiano(),
                            abitazione.getPrezzoPerNotte(),
                            abitazione.getDataInizioDisponibilita(),
                            abitazione.getDataFineDisponibilita()
                        );
                        
                        log.info("Abitazione creata nel DB: ID {} - '{}' per utente ID {}", idAbitazione, abitazione.getNome(), abitazione.getIdUtente());
                        return created;
                    }
                }
            }
            throw new RuntimeException("Impossibile creare abitazione nel database");
            
        } catch (SQLException e) {
            log.error("Errore nella creazione dell'abitazione nel DB: {}", e.getMessage());
            throw new RuntimeException("Errore nella creazione dell'abitazione", e);
        }
    }
    
    public Abitazione findById(long idAbitazione) {
        String sql = "SELECT * FROM abitazione WHERE idAbitazione = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idAbitazione);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAbitazione(rs);
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare abitazione con ID {} nel DB: {}", idAbitazione, e.getMessage());
            throw new RuntimeException("Errore nel recupero dell'abitazione", e);
        }
    }
    
    public List<Abitazione> findAll() {
        List<Abitazione> abitazioni = new ArrayList<>();
        String sql = "SELECT * FROM abitazione ORDER BY idAbitazione";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                abitazioni.add(mapResultSetToAbitazione(rs));
            }
            log.info("Trovate {} abitazioni nel DB", abitazioni.size());
            return abitazioni;
            
        } catch (SQLException e) {
            log.error("Errore nel recupero di tutte le abitazioni dal DB: {}", e.getMessage());
            throw new RuntimeException("Errore nel recupero delle abitazioni", e);
        }
    }
    
    public List<Abitazione> findByUtente(long idUtente) {
        List<Abitazione> abitazioni = new ArrayList<>();
        String sql = "SELECT * FROM abitazione WHERE idUtente = ? ORDER BY idAbitazione";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, idUtente);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                abitazioni.add(mapResultSetToAbitazione(rs));
            }
            log.info("Trovate {} abitazioni per utente ID {} nel DB", abitazioni.size(), idUtente);
            return abitazioni;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare abitazioni per utente {} nel DB: {}", idUtente, e.getMessage());
            throw new RuntimeException("Errore nel recupero abitazioni utente", e);
        }
    }
    
    public List<Abitazione> findByCodiceHost(String codiceHost) {
        List<Abitazione> abitazioni = new ArrayList<>();
        String sql = """
            SELECT a.* 
            FROM abitazione a
            JOIN host h ON a.idUtente = h.idUtente
            WHERE h.codiceHost = ?
            ORDER BY a.idAbitazione
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codiceHost);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                abitazioni.add(mapResultSetToAbitazione(rs));
            }
            log.info("Trovate {} abitazioni per host con codice {} nel DB", abitazioni.size(), codiceHost);
            return abitazioni;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare abitazioni per codice host {} nel DB: {}", codiceHost, e.getMessage());
            throw new RuntimeException("Errore nel recupero abitazioni per codice host", e);
        }
    }
    
    public Abitazione update(long idAbitazione, Abitazione abitazione) {
        String sql = """
            UPDATE abitazione 
            SET nome = ?, indirizzo = ?, numeroLocali = ?, numeroPostiLetto = ?, 
                piano = ?, prezzoPerNotte = ?, dataInizioDisponibilita = ?, dataFineDisponibilita = ?,
                disponibile = ?
            WHERE idAbitazione = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, abitazione.getNome());
            pstmt.setString(2, abitazione.getIndirizzo());
            pstmt.setInt(3, abitazione.getNumeroLocali());
            pstmt.setInt(4, abitazione.getNumeroPostiLetto());
            
            if (abitazione.getPiano() != null) {
                pstmt.setInt(5, abitazione.getPiano());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            pstmt.setDouble(6, abitazione.getPrezzoPerNotte());
            pstmt.setDate(7, java.sql.Date.valueOf(abitazione.getDataInizioDisponibilita()));
            pstmt.setDate(8, java.sql.Date.valueOf(abitazione.getDataFineDisponibilita()));
            pstmt.setBoolean(9, abitazione.isDisponibile());
            pstmt.setLong(10, idAbitazione);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                log.info("Abitazione aggiornata nel DB ID: {}", idAbitazione);
                return new Abitazione(
                    idAbitazione,
                    abitazione.getIdUtente(),
                    abitazione.getNome(),
                    abitazione.getIndirizzo(),
                    abitazione.getNumeroLocali(),
                    abitazione.getNumeroPostiLetto(),
                    abitazione.getPiano(),
                    abitazione.getPrezzoPerNotte(),
                    abitazione.getDataInizioDisponibilita(),
                    abitazione.getDataFineDisponibilita(),
                    abitazione.isDisponibile()
                );
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nell'aggiornamento dell'abitazione con ID {} nel DB: {}", idAbitazione, e.getMessage());
            throw new RuntimeException("Errore nell'aggiornamento dell'abitazione", e);
        }
    }
    
    public Abitazione findAbitazionePiuGettonataUltimoMese() {
        String sql = """
            SELECT a.*, COUNT(p.idPrenotazione) as num_prenotazioni
            FROM abitazione a
            LEFT JOIN prenotazione p ON a.idAbitazione = p.idAbitazione
            WHERE p.dataInizioPrenotazione >= CURRENT_DATE - INTERVAL '1 month'
            GROUP BY a.idAbitazione
            ORDER BY num_prenotazioni DESC
            LIMIT 1
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                log.info("Abitazione più gettonata trovata nel DB: ID {}", rs.getLong("idAbitazione"));
                return mapResultSetToAbitazione(rs);
            }
            return null;
            
        } catch (SQLException e) {
            log.error("Errore nel trovare abitazione più gettonata nel DB: {}", e.getMessage());
            throw new RuntimeException("Errore nel trovare abitazione più gettonata", e);
        }
    }

    public double calcolaMediaPostiLetto() {
        String sql = "SELECT AVG(numeroPostiLetto) as media_posti FROM abitazione";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                double media = rs.getDouble("media_posti");
                log.info("Media posti letto calcolata dal DB: {}", media);
                return media;
            }
            return 0.0;
            
        } catch (SQLException e) {
            log.error("Errore nel calcolo media posti letto dal DB: {}", e.getMessage());
            throw new RuntimeException("Errore nel calcolo media posti letto", e);
        }
    }
    
    private Abitazione mapResultSetToAbitazione(ResultSet rs) throws SQLException {
        return new Abitazione(
            rs.getLong("idAbitazione"),
            rs.getLong("idUtente"),
            rs.getString("nome"),
            rs.getString("indirizzo"),
            rs.getInt("numeroLocali"),
            rs.getInt("numeroPostiLetto"),
            rs.getInt("piano"),
            rs.getDouble("prezzoPerNotte"),
            rs.getDate("dataInizioDisponibilita").toLocalDate(),
            rs.getDate("dataFineDisponibilita").toLocalDate(),
            rs.getBoolean("disponibile")
        );
    }
}