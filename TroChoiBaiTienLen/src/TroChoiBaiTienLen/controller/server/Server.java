package TroChoiBaiTienLen.controller.server;

import java.net.Socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.ArrayList;

public class Server {

    public Server() {

    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket();
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ArrayList<Integer> list = new ArrayList<Integer>();
            oos.writeObject(list);
        } catch (IOException e) {

        }
    }
}
