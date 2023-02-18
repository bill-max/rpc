package allTest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import socket.Message;

import java.io.*;
import java.net.*;

public class TestMainServer {
    public static void main(String[] args) {
        try {
            int port = 9989;
            String path = "/node/test/127.0.0.1:" + port;
            Service service = new Service();
            CuratorFramework zkClient = service.getZkClient();
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostName(), port));
            Socket accept;
            while ((accept = serverSocket.accept()) != null) {
                System.out.println("===client connected===");
                //获取socket的输入流和输出流
                InputStream inputStream = accept.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                OutputStream outputStream = accept.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                //通过输入流读取客户端发送的消息
                Message message = (Message) objectInputStream.readObject();
                System.out.println("server receive message:" + message.getContent());
                //通过输出流向客户端发送响应消息
                message.setContent("hello");
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
