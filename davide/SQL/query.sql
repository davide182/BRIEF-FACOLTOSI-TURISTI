-- Tabella UTENTE - Contiene tutti gli utenti del sistema
CREATE TABLE IF NOT EXISTS utente (
    idUtente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    indirizzo VARCHAR(100) NOT NULL,
    data_registrazione DATE DEFAULT CURRENT_DATE,
    attivo BOOLEAN DEFAULT TRUE
);

-- Tabella HOST - Estende utente con funzionalità di host
CREATE TABLE IF NOT EXISTS host (
    idUtente INTEGER PRIMARY KEY REFERENCES utente(idUtente) ON DELETE RESTRICT,
    codiceHost VARCHAR(50) UNIQUE NOT NULL,
    isSuperHost BOOLEAN DEFAULT FALSE,
    dataDiventatoSuper DATE,
    totPrenotazioni INTEGER DEFAULT 0
);

-- Tabella ABITAZIONE - Contiene tutte le proprietà disponibili per affitto
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
    -- Vincoli di integrità
    CHECK (dataInizioDisponibilita <= dataFineDisponibilita),
    CHECK (prezzoPerNotte > 0),
    CHECK (numeroPostiLetto > 0)
);

-- Tabella PRENOTAZIONE - Contiene tutte le prenotazioni degli utenti
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
    -- Vincoli di integrità
    CHECK (dataInizioPrenotazione < dataFinePrenotazione),
    CHECK (prezzoTotale > 0)
);

-- Tabella FEEDBACK - Contiene le recensioni degli utenti sulle prenotazioni
CREATE TABLE IF NOT EXISTS feedback (
    idFeedback SERIAL PRIMARY KEY,
    idPrenotazione INTEGER UNIQUE NOT NULL REFERENCES prenotazione(idPrenotazione) ON DELETE RESTRICT,
    titolo VARCHAR(200) NOT NULL,
    testo TEXT NOT NULL,
    punteggio INTEGER NOT NULL CHECK (punteggio BETWEEN 1 AND 5),
    dataCreazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserimento UTENTE
INSERT INTO utente (nome, cognome, email, indirizzo) 
VALUES ('Mario', 'Rossi', 'mario.rossi@email.com', 'Via Roma 1, Milano');

-- Inserimento HOST (promuovere un utente a host)
INSERT INTO host (idUtente, codiceHost) 
VALUES (1, 'HOST-1-1234567890');

-- Inserimento ABITAZIONE
INSERT INTO abitazione (idUtente, nome, indirizzo, numeroLocali, numeroPostiLetto, piano, prezzoPerNotte, dataInizioDisponibilita, dataFineDisponibilita) 
VALUES (1, 'Appartamento Centro Milano', 'Via Torino 10, Milano', 3, 4, 2, 120.50, '2024-01-01', '2024-12-31');

-- Inserimento PRENOTAZIONE
INSERT INTO prenotazione (idAbitazione, idUtente, dataInizioPrenotazione, dataFinePrenotazione, stato, prezzoTotale) 
VALUES (1, 1, '2024-03-01', '2024-03-05', 'CONFERMATA', 482.00);

-- Inserimento FEEDBACK
INSERT INTO feedback (idPrenotazione, titolo, testo, punteggio) 
VALUES (1, 'Ottima esperienza', 'Appartamento pulito e in ottima posizione', 5);

-- Recuperare tutti gli utenti attivi
SELECT * FROM utente WHERE attivo = true ORDER BY idUtente;

-- Recuperare un utente specifico per ID
SELECT * FROM utente WHERE idUtente = 1;

-- Recuperare un utente per email
SELECT * FROM utente WHERE email = 'mario.rossi@email.com';

-- Recuperare tutti gli host
SELECT * FROM host ORDER BY idUtente;

-- Verificare se un utente è host
SELECT COUNT(*) FROM host WHERE idUtente = 1;

-- Recuperare un host per codice
SELECT * FROM host WHERE codiceHost = 'HOST-1-1234567890';

-- Recuperare tutti i super host
SELECT * FROM host WHERE isSuperHost = true ORDER BY idUtente;

-- Recuperare tutte le abitazioni
SELECT * FROM abitazione ORDER BY idAbitazione;

-- Recuperare un'abitazione specifica
SELECT * FROM abitazione WHERE idAbitazione = 1;

-- Recuperare le abitazioni di un utente
SELECT * FROM abitazione WHERE idUtente = 1 ORDER BY idAbitazione;

-- Recuperare le abitazioni per codice host
SELECT a.* 
FROM abitazione a
JOIN host h ON a.idUtente = h.idUtente
WHERE h.codiceHost = 'HOST-1-1234567890'
ORDER BY a.idAbitazione;

-- Recuperare tutte le prenotazioni
SELECT * FROM prenotazione ORDER BY dataPrenotazione DESC;

-- Recuperare una prenotazione specifica
SELECT * FROM prenotazione WHERE idPrenotazione = 1;

-- Recuperare le prenotazioni di un utente
SELECT * FROM prenotazione WHERE idUtente = 1 ORDER BY dataPrenotazione DESC;

-- Recuperare le prenotazioni di un'abitazione
SELECT * FROM prenotazione WHERE idAbitazione = 1 ORDER BY dataPrenotazione DESC;

-- Recuperare l'ultima prenotazione di un utente
SELECT * FROM prenotazione WHERE idUtente = 1 ORDER BY dataPrenotazione DESC LIMIT 1;

-- Recuperare tutti i feedback
SELECT * FROM feedback ORDER BY dataCreazione DESC;

-- Recuperare feedback per prenotazione
SELECT * FROM feedback WHERE idPrenotazione = 1;

-- Recuperare feedback per abitazione
SELECT f.* 
FROM feedback f
JOIN prenotazione p ON f.idPrenotazione = p.idPrenotazione
WHERE p.idAbitazione = 1
ORDER BY f.dataCreazione DESC;

-- Aggiornare dati utente
UPDATE utente 
SET nome = 'Mario', cognome = 'Rossi', email = 'mario.rossi@email.com', indirizzo = 'Via Roma 2, Milano', attivo = true 
WHERE idUtente = 1;

-- Disabilitare un utente (soft delete)
UPDATE utente SET attivo = false WHERE idUtente = 1;

-- Abilitare un utente
UPDATE utente SET attivo = true WHERE idUtente = 1;

-- Aggiornare dati abitazione
UPDATE abitazione 
SET nome = 'Nuovo Nome', indirizzo = 'Nuovo Indirizzo', numeroLocali = 4, numeroPostiLetto = 5, 
    piano = 3, prezzoPerNotte = 150.00, dataInizioDisponibilita = '2024-01-01', 
    dataFineDisponibilita = '2024-12-31', disponibile = true
WHERE idAbitazione = 1;

-- Disabilitare un'abitazione (soft delete)
UPDATE abitazione SET disponibile = false WHERE idAbitazione = 1;

-- Abilitare un'abitazione
UPDATE abitazione SET disponibile = true WHERE idAbitazione = 1;

-- Aggiornare una prenotazione
UPDATE prenotazione 
SET idAbitazione = 1, idUtente = 1, dataInizioPrenotazione = '2024-03-01', 
    dataFinePrenotazione = '2024-03-05', stato = 'CONFERMATA', prezzoTotale = 482.00, 
    cancellataDalUtente = false
WHERE idPrenotazione = 1;

-- Aggiornare lo stato di una prenotazione
UPDATE prenotazione SET stato = 'COMPLETATA' WHERE idPrenotazione = 1;

-- Cancellare una prenotazione (soft delete)
UPDATE prenotazione SET cancellataDalUtente = true, stato = 'CANCELLATA' WHERE idPrenotazione = 1;

-- Aggiornare un feedback
UPDATE feedback SET titolo = 'Nuovo titolo', testo = 'Nuovo testo', punteggio = 4 WHERE idFeedback = 1;

-- Promuovere un host a super host
UPDATE host SET isSuperHost = true, dataDiventatoSuper = CURRENT_DATE, totPrenotazioni = 100 WHERE idUtente = 1;

-- Aggiornare contatore prenotazioni host (automatico nel sistema)
UPDATE host SET totPrenotazioni = (
    SELECT COUNT(*) 
    FROM prenotazione p 
    JOIN abitazione a ON p.idAbitazione = a.idAbitazione 
    WHERE a.idUtente = 1 AND p.stato = 'COMPLETATA'
) WHERE idUtente = 1;

-- Trovare l'abitazione più prenotata nell'ultimo mese
SELECT a.*, COUNT(p.idPrenotazione) as num_prenotazioni
FROM abitazione a
LEFT JOIN prenotazione p ON a.idAbitazione = p.idAbitazione
WHERE p.dataInizioPrenotazione >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY a.idAbitazione
ORDER BY num_prenotazioni DESC
LIMIT 1;

-- Calcolare la media dei posti letto delle abitazioni
SELECT AVG(numeroPostiLetto) as media_posti FROM abitazione;

-- Trovare gli host con più prenotazioni nell'ultimo mese
SELECT h.*, COUNT(p.idPrenotazione) as prenotazioni_ultimo_mese
FROM host h
JOIN abitazione a ON h.idUtente = a.idUtente
JOIN prenotazione p ON a.idAbitazione = p.idAbitazione
WHERE p.dataInizioPrenotazione >= CURRENT_DATE - INTERVAL '1 month'
   AND p.stato = 'COMPLETATA'
GROUP BY h.idUtente, h.codiceHost, h.isSuperHost, h.dataDiventatoSuper, h.totPrenotazioni
ORDER BY prenotazioni_ultimo_mese DESC;

-- Recuperare le prenotazioni dell'ultimo mese
SELECT * FROM prenotazione 
WHERE dataInizioPrenotazione >= CURRENT_DATE - INTERVAL '1 month' 
ORDER BY dataPrenotazione DESC;

-- Calcolare i giorni prenotati da un utente nell'ultimo mese
SELECT SUM(DATE_PART('day', dataFinePrenotazione - dataInizioPrenotazione)) as giorni_totali
FROM prenotazione 
WHERE idUtente = 1 
  AND dataInizioPrenotazione >= CURRENT_DATE - INTERVAL '1 month'
  AND stato != 'CANCELLATA';

-- Trovare i top 5 utenti per giorni prenotati nell'ultimo mese
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
LIMIT 5;

-- Trovare feedback con punteggio alto (>= 4)
SELECT * FROM feedback WHERE punteggio >= 4 ORDER BY dataCreazione DESC;

-- Calcolare la media punteggio di un host
SELECT AVG(f.punteggio) as media_punteggio
FROM feedback f
JOIN prenotazione p ON f.idPrenotazione = p.idPrenotazione
JOIN abitazione a ON p.idAbitazione = a.idAbitazione
WHERE a.idUtente = 1;