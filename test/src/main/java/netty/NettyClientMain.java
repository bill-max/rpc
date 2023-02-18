package netty;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class NettyClientMain {
    public static void main(String[] args) {
        try {
            NettyClient client = null;
            client = new NettyClient(InetAddress.getLocalHost().getHostName(), 8000);
            System.out.println("done");
            NettyResponse response = client.sendMessage(NettyRequest.builder().interfaceName("interface").methodName("get").build());
            System.out.println(response);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
