package netty;

public class NettyServerMain {
    public static void main(String[] args) {
        NettyServer server = new NettyServer(8000);
        server.run();

    }
}
