package allTest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import socket.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class TestMainClient {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket();
        ) {
            Service service = new Service();
            CuratorFramework zkClient = service.getZkClient();
            String path = "/node/test";
            List<String> strings = zkClient.getChildren().forPath(path);
            String host = InetAddress.getLocalHost().getHostName();
            int port = Integer.parseInt(strings.get(0).split(":")[1].toString());
            socket.connect(new InetSocketAddress(host, port));
            //客户端通过输出流发送消息
            Message message = new Message("hello <==> test Client main");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            //通过输入流获取响应
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println(objectInputStream.readObject());

            service.closeAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
