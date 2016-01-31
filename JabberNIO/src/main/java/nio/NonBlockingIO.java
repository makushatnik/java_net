package nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
public class NonBlockingIO {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java NonBlockingIO <client_port> <local_server_port");
            System.exit(1);
        }
        int cPort = Integer.parseInt(args[0]);
        int sPort = Integer.parseInt(args[1]);
        SocketChannel ch = SocketChannel.open();
        Selector sel = Selector.open();
        try {
            ch.socket().bind(new InetSocketAddress(cPort));
            ch.configureBlocking(false);
            ch.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
            sel.select();
            Iterator it = sel.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey)it.next();
                it.remove();
                if (key.isConnectable()) {
                    InetAddress ad = InetAddress.getLocalHost();
                    System.out.println("Connect will not block");
                    if (!ch.connect(new InetSocketAddress(ad, sPort)))
                        ch.finishConnect();
                }
                if (key.isReadable())
                    System.out.println("Read will not block");
                if (key.isWritable())
                    System.out.println("Write will not block");
            }
        } finally {
            ch.close();
            sel.close();
        }
    }
}
