import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket s; // Socket per la connessione al server
    private BufferedReader mexIn; // Flusso di input per ricevere messaggi dal server
    private PrintWriter mexOut; // Flusso di output per inviare messaggi al server
    private String username; // Nome utente del client

    // Costruttore del client, richiede un oggetto Socket come parametro
    public Client(Socket s) {
        Scanner input = new Scanner(System.in);
        try {
            this.s = s; // Associo il socket al client

            // Inizializzo i flussi di input e output
            this.mexIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.mexOut = new PrintWriter(s.getOutputStream(), true); // Auto-flush abilitato

            // Richiedo all'utente di inserire uno username per identificarsi
            System.out.println("Per iniziare, inserisci il tuo username:");
            do {
                this.username = input.nextLine();
                if (this.username == null || this.username.isEmpty()) {
                    System.out.println("L'username è vuoto, riprova");
                }
            } while (this.username == null || this.username.isEmpty());

            // Invio lo username al server
            mexOut.println(this.username);
        } catch (IOException e) {
            System.out.println("Errore durante l'inizializzazione del client: " + e.getMessage());
        }
    }

    // Metodo per inviare messaggi al server
    public void sendMex() {
        Scanner input = new Scanner(System.in);
        try {
            // Continuo a leggere messaggi finché il socket è connesso
            while (s.isConnected()) {
                String mex = input.nextLine(); // Leggo l'input dell'utente

                // Controllo che il messaggio non sia vuoto
                while (mex != null && mex.trim().isEmpty()) {
                    System.out.println("Non puoi inviare un messaggio vuoto, ritenta -> ");
                    mex = input.nextLine();
                }

                // Stampo il messaggio sul client
                System.out.println("Tu: " + mex);

                // Se il messaggio è "exit", chiudo la connessione e notifico il server
                if (mex.equals("exit")) {
                    mexOut.println("[+] " + username + " si è disconnesso!");
                    closeSocket();
                    break;
                }

                // Invio il messaggio al server
                mexOut.println(mex);
            }
        } catch (Exception ex) {
            System.out.println("Errore durante l'invio del messaggio: " + ex.getMessage());
            closeSocket();
        }
    }

    // Metodo per ricevere messaggi dal server (eseguito su un thread separato)
    public void reciveMex() {
        Thread t = new Thread(() -> {
            try {
                String mex;
                // Continuo a leggere messaggi dal server finché il flusso è attivo
                while ((mex = mexIn.readLine()) != null) {
                    System.out.println(mex); // Stampo il messaggio ricevuto
                }
            } catch (IOException e) {
                System.out.println("Errore durante la ricezione dei messaggi: " + e.getMessage());
                closeSocket();
            }
        });
        t.start(); // Avvio il thread
    }

    // Metodo per chiudere il socket del client
    public void closeSocket() {
        if (s != null && !s.isClosed()) {
            try {
                s.close();
                System.out.println("Connessione chiusa correttamente.");
            } catch (IOException e) {
                System.out.println("Errore durante la chiusura del socket: " + e.getMessage());
            }
        }
    }

    // Metodo principale per avviare il client
    public static void main(String[] args) {
        // Connessione al server sulla porta 8080
        try (Socket s = new Socket("localhost", 8080)) { 
            Client client = new Client(s); // Creo un oggetto client

            /*
             * Prima avvio il thread per ricevere messaggi, 
             * poi passo alla fase di invio.
             * Questo ordine evita un blocco nel flusso principale 
             * causato dall'attesa di un input prima di avviare la ricezione.
             */
            client.reciveMex(); // Avvio il thread per ricevere messaggi
            client.sendMex(); // Procedo con l'invio dei messaggi
        } catch (IOException e) {
            System.out.println("Errore di connessione: " + e.getMessage());
        }
    }
}

