# Chat Server-Client Multi-threaded in Java

Questo progetto implementa un sistema di chat server-client in Java utilizzando socket e multi-threading. Il server gestisce più connessioni simultanee dei client, consentendo loro di comunicare tra loro in tempo reale.

## Caratteristiche principali

- **Comunicazione Multi-client**: Il server supporta connessioni simultanee con più client.
- **Broadcast dei messaggi**: I messaggi inviati da un client vengono inoltrati a tutti gli altri client connessi.
- **Thread separati per ogni client**: Ogni connessione client è gestita in modo indipendente, utilizzando thread.
- **Username univoco**: Ogni client si registra con un username per essere identificato.
- **Comando di disconnessione**: I client possono disconnettersi digitando `exit`.

## Struttura del progetto

- **`Server`**: 
  - Avvia il server sulla porta specificata.
  - Accetta connessioni dei client.
  - Gestisce la comunicazione tra i client utilizzando una classe `ClientHandler` per ogni connessione.
  - Implementa una funzione di broadcast per inoltrare i messaggi a tutti i client connessi.

- **`Client`**:
  - Si connette al server specificando un username.
  - Consente di inviare messaggi e ricevere i messaggi degli altri client in tempo reale.
  - Implementa un thread per la ricezione dei messaggi per evitare blocchi.

## Come eseguire il progetto

1. **Compilazione del codice**
   - Assicurati di avere il file `Server.java` e `Client.java` nella stessa directory.
   - Compila i file utilizzando il comando:
     ```bash
     javac Server.java Client.java
     ```

2. **Avvio del server**
   - Esegui il server specificando una porta (es. 8080):
     ```bash
     java Server
     ```
   - Il server sarà in ascolto sulla porta `8080` (o quella specificata nel codice).

3. **Connessione dei client**
   - Esegui il file `Client.java` per connettere un client al server:
     ```bash
     java Client
     ```
   - Inserisci uno username per identificarti nella chat.

4. **Invio e ricezione di messaggi**
   - Ogni client può inviare messaggi, che verranno ricevuti dagli altri client connessi.
   - Per uscire, digita `exit`.
