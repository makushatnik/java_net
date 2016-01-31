package io;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
public class JabberClient {
    public static void main(String[] args) throws IOException {
        InetAddress addr = InetAddress.getByName("localhost");
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, JabberServer.PORT);
        try {
            System.out.println("socket = " + socket);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //вывод выталкивается из буфера - флажок true
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true);
            out.println("live");
            String str = in.readLine();
            System.out.println("str = " + str);
            out.println("красота");
            str = in.readLine();
            System.out.println("str = " + str);
            out.println("END");
        } finally {
            System.out.println("closing...");
            socket.close();
        }
    }
}
