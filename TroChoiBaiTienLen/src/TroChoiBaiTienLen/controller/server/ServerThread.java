package TroChoiBaiTienLen.controller.server;

import TroChoiBaiTienLen.data.Alert;

import java.net.Socket;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ServerThread extends Thread {

    Socket client;
    
    ObjectOutputStream oos;

    public ServerThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            
            String msg = Alert.HELLO;
            oos.writeUTF(msg);
            oos.flush();
            oos.close();
            while(true){
//                System.out.println("...");
            }
            // neu nhan thong bao thoat thi dong client
        } catch (IOException e) {
        }

    }
}
