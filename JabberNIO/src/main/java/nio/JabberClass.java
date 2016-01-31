package nio;

import io.JabberServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
public class JabberClass {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java JabberClass <client_port>");
            System.exit(1);
        }
        int clPrt = Integer.parseInt(args[0]);
        SocketChannel sc = SocketChannel.open();
        Selector sel = Selector.open();
        try {
            sc.configureBlocking(false);
            sc.socket().bind(new InetSocketAddress(clPrt));
            sc.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
            int i = 0;
            boolean written = false, done = false;
            String encoding = System.getProperty("file.encoding");
            Charset cs = Charset.forName(encoding);
            ByteBuffer buf = ByteBuffer.allocate(16);
            while (!done) {
                sel.select();
                Iterator it = sel.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey)it.next();
                    it.remove();
                    sc = (SocketChannel)key.channel();
                    if (key.isConnectable() && !sc.isConnected()) {
                        InetAddress addr = InetAddress.getByName(null);
                        boolean success = sc.connect(new InetSocketAddress(addr, JabberServer.PORT));
                        if (!success)
                            sc.finishConnect();
                    }
                    if (key.isReadable() && written) {
                        if (sc.read((ByteBuffer)buf.clear()) > 0) {
                            written = false;
                            String resp = cs.decode((ByteBuffer)buf.flip()).toString();
                            System.out.println("resp = " + resp);
                            if (resp.indexOf("END") != -1)
                                done = true;
                        }
                    }
                    if (key.isWritable() && !written) {
                        if (i < 10)
                            sc.write(ByteBuffer.wrap(new String("howdy" + i + 'n').getBytes()));
                        else if (i == 10)
                            sc.write(ByteBuffer.wrap(new String("ENDn").getBytes()));
                        written = true;
                        i++;
                    }
                }
            }
        } finally {
            sc.close();
            sel.close();
        }
    }
}
