package io;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
class JabberClientThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static int counter = 0;
    private int id = counter++;
    private static int threadCnt = 0;

    public static int getThreadCnt() {
        return threadCnt;
    }

    public JabberClientThread(InetAddress addr) {
        System.out.println("Making client - " + id);
        threadCnt++;
        try {
            socket = new Socket(addr, MultiJabberServer.PORT);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //вывод выталкивается из буфера - флажок true
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true);
            start();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.err.println("Socket not closed");
            }
        }
    }

    public void run() {
        try {
            for (int i=0; i < 25; i++) {
                out.println("Client" + id + ":" + i);
                String str = in.readLine();
                System.out.println("str = " + str);
            }
        } catch (IOException e) {
            System.err.println("IOException");
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.err.println("Socket not closed");
            }
            threadCnt--;
        }
    }
}

public class MultiJabberClient {
    static final int MAX_THREADS = 40;

    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress addr = InetAddress.getByName(null);
        while (true) {
            if (JabberClientThread.getThreadCnt() < MAX_THREADS)
                new JabberClientThread(addr);
            Thread.currentThread().sleep(100);
        }
    }
}
