package io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
class ServeOneJabber extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServeOneJabber(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //вывод выталкивается из буфера - флажок true
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                true);

        start();
    }

    public static String inverseString(String str) {
        String tmp = "";
        for (int i=str.length()-1; i >= 0; i--) {
            tmp += str.charAt(i);
        }

        return tmp;
    }

    public void run() {
        try {
            while (true) {
                String str = in.readLine();
                if (str.equals("END")) break;
                str = inverseString(str);
                System.out.println("Echoing: " + str);
                out.println(str);
            }
            System.out.println("closing...");
        } catch (IOException e) {
            System.err.println("IOException");
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.err.println("Socket not closed");
            }
        }
    }
}

public class MultiJabberServer {
    static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server Started");
        try {
            while (true) {
                Socket socket = s.accept();
                try {
                    new ServeOneJabber(socket);
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            s.close();
        }
    }
}
