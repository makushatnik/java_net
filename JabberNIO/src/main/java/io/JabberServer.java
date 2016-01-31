package io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
public class JabberServer {
    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Started: " + s);
        try {
            Socket socket = s.accept();
            try {
                System.out.println("Connection accepted: " + socket);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //вывод выталкивается из буфера - флажок true
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                        true);
                while (true) {
                    String str = in.readLine();
                    if (str.equals("END")) break;
                    str = inverseString(str);
                    System.out.println("Echoing: " + str);
                    out.println(str);
                }
            } finally {
                System.out.println("closing...");
                socket.close();
            }
        } finally {
            s.close();
        }
    }

    public static String inverseString(String str) {
        String tmp = "";
        for (int i=str.length()-1; i >= 0; i--) {
            tmp += str.charAt(i);
        }

        return tmp;
    }
}
