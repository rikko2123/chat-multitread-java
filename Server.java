import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Server {
    // Variabile per il socket del server
    private ServerSocket server;
    // Lista per gestire i client connessi
    private ArrayList<ClientHandler> clientList;

    // Costruttore della classe Server
    public Server(ServerSocket server) {
        this.server = server;
        this.clientList = new ArrayList<>();
    }

    // Metodo per avviare il server
    public void startServer() {
        try {
            System.out.println("[+] Server in ascolto sulla porta: " + server.getLocalPort());
            System.out.println("Utenti online: ");

            // Ciclo infinito per accettare connessioni
            while (!server.isClosed()) {
                // Attendo connessione dal client
                Socket s = server.accept();

                // Creo un gestore per il client connesso
                ClientHandler handler = new ClientHandler(s);

                // Aggiungo il nuovo gestore alla lista dei client
                clientList.add(handler);

                // Stampo il nome utente del nuovo client connesso
                System.out.print(handler.username + " ");

                // Avvio un nuovo thread per gestire il client
                Thread client = new Thread(handler);
                client.start();
            }
        } catch (Exception e) {
            // Chiudo il server in caso di errore
            closeServer(server);
        }
    }

    // Metodo per chiudere il server
    public void closeServer(ServerSocket s) {
        if (s != null && !s.isClosed()) {
            try {
                s.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    // Classe interna per gestire le connessioni dei client
    class ClientHandler implements Runnable {
        private Socket s; // Socket per la connessione con il client
        private BufferedReader mexIn; // Per ricevere messaggi dal client
        private PrintWriter mexOut; // Per inviare messaggi al client
        private String username; // Nome utente del client

        /*
         * dentro al costruttore istanzio gli oggetti mexIn e mexOut
         * mi aspetto che il primo messaggio inviato sia il nik del client
         * uso .readLine() per catturare il messaggio, fatto questo il client potra 
         * iniziare a comunicare
        */
        // Costruttore del gestore del client
        public ClientHandler(Socket s) {
            try {
                this.s = s;

                // Inizializzo i flussi di input e output
                this.mexIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                this.mexOut = new PrintWriter(s.getOutputStream(), true);

                // Ricevo il nome utente del client
                this.username = mexIn.readLine();

                // Comunico a tutti i client che un nuovo utente si è connesso
                broadcast("[+] " + username + " si è connesso!!");
            } catch (Exception e) {
                // Chiudo il socket in caso di errore
                System.out.println(e);
                closeSocket(s);
            }
        }

        // Metodo per inviare un messaggio a tutti i client connessi
        public void broadcast(String mex) {
            for (ClientHandler c : clientList) {
                if (!c.username.equals(this.username)) {
                    // Invio il messaggio agli altri client
                    c.mexOut.println(mex);
                }
            }
        }

        // Metodo per chiudere il socket del client
        public void closeSocket(Socket sock) {
            if (sock != null && !sock.isClosed()) {
                try {
                    sock.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        // Metodo eseguito dal thread per gestire la connessione del client
        public void run() {
            try {
                // Continuo a ricevere messaggi finché il client è connesso
                while (s.isConnected()) {
                    String mexFromClient = mexIn.readLine();

                    if (mexFromClient != null && !mexFromClient.equals("exit")) {
                        // Invio il messaggio agli altri client
                        broadcast(username + ": " + mexFromClient);
                    } else {
                        // Chiudo la connessione se il messaggio è "exit"
                        broadcast("[+] " + username + " si è disconnesso!");
                        closeSocket(s);
                        break;
                    }
                }
            } catch (IOException e) {
                // Chiudo il socket in caso di errore
                closeSocket(s);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // Creo il socket del server sulla porta 8080
        ServerSocket serverSock = new ServerSocket(8080);

        // Creo l'oggetto Server
        Server server = new Server(serverSock);

        // Avvio il server
        server.startServer();
    }
}

