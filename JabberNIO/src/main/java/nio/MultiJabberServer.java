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
public class MultiJabberServer {
    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        String encoding = System.getProperty("file.encoding");
        Charset cs = Charset.forName(encoding);
        ByteBuffer buf = ByteBuffer.allocate(16);
        SocketChannel ch = null;
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
                        ch = ssc.accept();
                        System.out.println("Accepted connection from: " + ch.socket());
                        ch.configureBlocking(false);
                        ch.register(sel, SelectionKey.OP_READ);
                    }
                    else {

                        ch = (SocketChannel)skey.channel();
                        ch.read(buf);
                        CharBuffer cb = cs.decode((ByteBuffer)buf.flip());
                        String resp = cb.toString();
                        System.out.println("Echoing: " + resp);
                        ch.write((ByteBuffer)buf.rewind());
                        if (resp.indexOf("END") != -1)
                            ch.close();
                        buf.clear();
                    }
                }
            }
        } finally {
            if (ch != null)
                ch.close();
            ssc.close();
            sel.close();
        }
    }
}
