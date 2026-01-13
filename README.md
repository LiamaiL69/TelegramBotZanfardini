NationQuiz Bot

NationQuiz è un bot Telegram educativo e divertente che ti permette di mettere alla prova le tue conoscenze sulle nazioni del mondo! Il bot offre quiz a scelta multipla basati su indizi, una classifica globale, un NationDex personale e statistiche persistenti salvate nel database.

🔹 Funzionalità principali

Quiz interattivi sulle nazioni del mondo

Classifica globale (Leaderboard) aggiornata

NationDex personale per tracciare le nazioni scoperte

Statistiche persistenti: punti totali, quiz giocati, quiz vinti

Personalizzazione dello username

Limite massimo di 5 indizi per quiz

🔹 Comandi disponibili
Comando	Descrizione
/start	Messaggio di benvenuto e istruzioni iniziali
/help	Mostra tutti i comandi disponibili
/quiz	Inizia un nuovo quiz
/leaderboard	Mostra la classifica globale dei migliori punteggi
/NationDex	Mostra le nazioni già scoperte dall’utente
/setname <nome>	Cambia il tuo username
/quizstats	Mostra le tue statistiche personali (punti, quiz giocati/vinti, nazioni scoperte)
/myrank	Mostra la tua posizione nella classifica globale
/resetdex	Resetta il tuo NationDex, ricominciando da zero
🔹 Come funziona il quiz

Scrivi /quiz per iniziare.

Il bot fornirà un primo indizio sulla nazione (continente, capitale o top-level domain).

Hai massimo 5 indizi per indovinare correttamente la nazione.

Ogni indizio successivo fornisce maggiori informazioni e diminuisce i punti ottenuti:

Indizi usati	Punti ottenuti
1	15
2	12
3	9
4	6
5	3

Se esaurisci tutti gli indizi senza indovinare, il quiz termina e il bot mostra la risposta corretta.

🔹 Database

Il bot utilizza un database SQLite (o qualsiasi altro supportato da JDBC) con due tabelle principali:

users
Colonna	Tipo	Descrizione
chat_id	INTEGER PRIMARY KEY	ID dell’utente Telegram
name	TEXT NOT NULL	Username dell’utente
points	INTEGER DEFAULT 0	Punti totali
quiz_played	INTEGER DEFAULT 0	Quiz giocati
quiz_won	INTEGER DEFAULT 0	Quiz vinti
nationdex
Colonna	Tipo	Descrizione
chat_id	INTEGER	ID dell’utente (foreign key)
nation	TEXT	Nome della nazione scoperta
PRIMARY KEY (chat_id, nation)		
🔹 Tecnologie utilizzate

Java 17+

IntelliJ IDEA

Telegram Bots API (org.telegram.telegrambots)

SQLite o altro DB compatibile JDBC

Gson per il parsing JSON (opzionale, se si vogliono estendere i dati sulle nazioni)

🔹 Esempi d’uso

Avvio: /start

Iniziare un quiz: /quiz

Vedere la classifica: /leaderboard

Vedere le tue statistiche: /quizstats

Cambiare username: /setname Marco

Vedere la tua posizione in classifica: /myrank

Resettare il NationDex: /resetdex

🔹 Suggerimenti

I nomi delle nazioni sono in inglese.

Gioca frequentemente per scalare la leaderboard!

Il NationDex ti permette di vedere tutte le nazioni che hai scoperto durante i quiz.



@Country_Games_For_Free_Bot
