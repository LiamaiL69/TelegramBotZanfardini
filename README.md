

🤖 Country_Games_Deluxe_Edition è un bot Telegram educativo e divertente che mette alla prova le tue conoscenze sulle nazioni del mondo. Indovina i paesi utilizzando indizi vari: continente, capitale, dominio, valuta, lingua, bandiera e altri dettagli.

🧩 Funzionalità principali

Quiz interattivi: Rispondi correttamente alle domande sulle nazioni per guadagnare punti.

Indizi multipli: Hai fino a 5 indizi per ogni quiz, tra cui:

Continente 🌍

Capitale 🏙️

Top Level Domain 🌐

Valuta 💰

Lingua principale 🗣️

Bandiera 🚩 (visualizzabile se il client supporta immagini)

Numero di lettere del nome e prima lettera 🔤

Numero di parole 📝

Nome della nazione mescolato 🔀

NationDex: Traccia tutte le nazioni scoperte.

Leaderboard: Classifica globale dei migliori giocatori 🏆.

Statistiche personali: Punti totali, quiz giocati, quiz vinti, nazioni scoperte 📊.

Gestione username: Cambia il tuo nickname con /setname <nome>.

Reset NationDex: Resetta le nazioni scoperte con /resetdex.

💻 Architettura del progetto

Main.java: Avvio del bot e registrazione dei servizi.

MyTelegramBot.java: Gestisce gli aggiornamenti da Telegram e passa comandi/risposte al servizio.

CommandHandler.java: Gestisce tutti i comandi /start, /help, /setname, /leaderboard, /NationDex, /quizstats, /myrank, /resetdex.

QuizService.java: Logica dei quiz, gestione indizi e punteggi, invio di bandiere se disponibili.

NationService.java: Gestione dei dati delle nazioni usando l'API RESTCountries 3.1:

Estrae nome, capitale, continente, TLD, valuta, lingua e URL della bandiera PNG.

UserService.java: Gestione degli utenti, statistiche, leaderboard, NationDex.

User.java: Modello dati utente con campi:

Chat ID

Nome

Punti totali

Nazioni scoperte

Quiz giocati e vinti

DatabaseManager.java: Gestione del database SQLite.

ConfigurationSingleton.java: Caricamento delle configurazioni da config.properties.

QuizSession.java: Rappresenta una sessione di quiz per un utente.

🗄️ Database

SQLite con due tabelle principali:

users

chat_id (PRIMARY KEY)

name

points

quiz_played

quiz_won

nationdex

chat_id

nation (nome della nazione scoperta)

⚡ Tecnologie utilizzate

Java 17+

Telegram Bots Java Library

Gson
 per parsing JSON

SQLite per la persistenza dei dati

RESTCountries 3.1 API per dati aggiornati sulle nazioni

🔧 Come funziona il quiz

L’utente avvia un quiz con /quiz.

Il bot seleziona una nazione casuale.

Vengono generati fino a 5 indizi casuali e uno speciale per la bandiera.

L’utente risponde al quiz:

✅ Risposta corretta → punti assegnati in base al numero di indizi usati.

❌ Risposta errata → nuovo indizio fino a esaurimento.

Alla fine del quiz, il bot aggiorna le statistiche e il NationDex.

🏅 Sistema di punteggio
Indizi usati	Punti assegnati
1	15
2	12
3	9
4	6
5	3
oltre 5	1
📬 Comandi disponibili
Comando	Descrizione
/start	Messaggio di benvenuto e istruzioni
/help	Lista completa dei comandi
/quiz	Avvia un nuovo quiz
/leaderboard	Mostra la classifica globale
/NationDex	Visualizza le nazioni scoperte
/setname <nome>	Cambia il tuo username
/quizstats	Mostra le tue statistiche personali
/myrank	Mostra la tua posizione nella leaderboard
/resetdex	Resetta le nazioni scoperte
🌐 API integrata

RESTCountries 3.1: dati nazionali aggiornati, inclusi:

Nome ufficiale

Capitale

Continente

TLD

Valute

Lingue

URL bandiera PNG

Il bot gestisce correttamente le eccezioni per valute o lingue complesse e fallback su "Unknown".

🔒 Note importanti

Il bot invia immagini solo se l’utente e il client supportano le anteprime URL.

Le statistiche (quiz giocati, vinti, punti) sono persistenti nel database.

I dati delle nazioni sono caricati una sola volta all’avvio per ottimizzare le performance.


@Country_Games_For_Free_Bot
