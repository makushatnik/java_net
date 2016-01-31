package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
class ServeOneJabber implements Runnable {
    private SocketChannel channel;
    private Selector sel;

    public ServeOneJabber(SocketChannel ch) throws IOException {
        channel = ch;
        sel = Selector.open();
    }

    public void run() {
        ByteBuffer buf = ByteBuffer.allocate(16);
        boolean read = false, done = false;
        String resp = null;
        try {
            channel.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            while (!done) {
                sel.select();
                Iterator it = sel.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey)it.next();
                    it.remove();
                    if (key.isReadable() && !read) {
                        if (channel.read(buf) > 0)
                            read = true;
                        CharBuffer cb = MultiJabberServer2.CS.decode((ByteBuffer)buf.flip());
                        resp = cb.toString();
                    }
                    if (key.isWritable() && read) {
                        System.out.println("Echoing : " + resp);
                        channel.write((ByteBuffer)buf.rewind());
                        if (resp.indexOf("END") != -1)
                            done = true;
                        buf.clear();
                        read = false;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                System.out.println("Channel not closed");
                throw new RuntimeException(e);
            }
        }

    }
}

public class MultiJabberServer2 {
    public static final int PORT = 8080;
    private static String encoding = System.getProperty("file.encoding");
    public static Charset CS = Charset.forName(encoding);
    private static ThreadPool pool = new ThreadPool(20);

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        Selector sel = Selector.open();
        try {
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(PORT));
            SelectionKey key = ssc.register(sel, SelectionKey.OP_ACCEPT);
            System.out.println("Server on port: " + PORT);
            while (true) {
                sel.select();
                Iterator it = sel.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey skey = (SelectionKey)it.next();
                    it.remove();
                    if (skey.isAcceptable()) {
                        SocketChannel channel = ssc.accept();
                        System.out.println("Accepted connection from: " + channel.socket());
                        channel.configureBlocking(false);
                        pool.addTask(new ServeOneJabber(channel));
                    }
                }
            }
        } finally {
            ssc.close();
            sel.close();
        }
    }
}
