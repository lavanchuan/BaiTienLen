package TroChoiBaiTienLen.controller.client;

import TroChoiBaiTienLen.data.Setting;

import java.net.Socket;

import java.io.IOException;
import java.io.ObjectInputStream;

public class TCPClient {
    
    Socket client;
    int port;
    String serverName;
    
    ObjectInputStream ois;
    
    // message
    String msg;
    
    public TCPClient(){
        port = Setting.PORT;
        serverName = Setting.SERVER_NAME;
    }
    
    public void run(){
        try{
            client = new Socket(serverName, port);
            ois = new ObjectInputStream(client.getInputStream());
            
            while(true){
                msg = ois.readUTF();
                System.out.println(msg);
                if(false){
                    break;
                }
            }
            ois.close();
            
            client.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        TCPClient client = new TCPClient();
        client.run();
    }
}
