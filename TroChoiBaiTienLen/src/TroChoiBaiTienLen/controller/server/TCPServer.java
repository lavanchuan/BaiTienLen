package TroChoiBaiTienLen.controller.server;

import TroChoiBaiTienLen.data.Setting;
import TroChoiBaiTienLen.model.Deck;
import TroChoiBaiTienLen.model.Tools;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.ArrayList;

public class TCPServer {

    // net
    ServerSocket server;
    Socket client;
    int port;

    // client
    int quantityPlayer;
    final int MIN_PLAYER = 2;
    final int MAX_PLAYER = 4;
    ArrayList<ServerThread> clients;

    // deck
    Deck deck;

    // message
    String msg;
    
    // output and input
    ObjectOutputStream oos;

    public TCPServer() {
        port = Setting.PORT;
        deck = new Deck();
        clients = new ArrayList<ServerThread>();
    }

    public void run() {
        try {
            server = new ServerSocket(port);
            boolean running = true;
            int counter = 0;
            do {
                System.out.printf("Quantity player: [%d-%d]\t: ", MIN_PLAYER, MAX_PLAYER);
                quantityPlayer = Tools.getInt();
                if (quantityPlayer >= MIN_PLAYER && quantityPlayer <= MAX_PLAYER) {
                    break;
                }
            } while (true);

            while (running) {
                // connect
                client = server.accept();
                if (++counter <= quantityPlayer) {
                    System.out.println("Gui thong bao cho nguoi choi khac ket noi");
                    if (counter < quantityPlayer) {
                        msg = "Dang cho " + (quantityPlayer - counter) + " nguoi choi ket noi";
                    } else {
                        msg = "Bat dau";
                    }

                    try {
                        ServerThread clientItem = new ServerThread(client);
                        clients.add(clientItem);
                        for(ServerThread st : clients){
                            oos = new ObjectOutputStream(st.client.getOutputStream());
                            oos.writeUTF(msg);
                            oos.flush();
                            oos.close();
                        }
                    } catch (Exception e) {
                        System.err.println("ERROR");
                        client.close();
                    }
                }

                if (counter == quantityPlayer) {
                    System.out.println("gui thong bao bat dau cho cac nguoi choi");
                    for (ServerThread st : clients) {
                        st.start();
                    }
                }

                System.out.println("Online\t: " + counter);
                System.out.println("Player\t: " + clients.size());

                if (false) {
                    break;
                }
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        server.run();
    }
}
