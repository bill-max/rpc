package socket;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public void start(int port) {
        //创建serverSocket
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            //通过accept方法监听客户端请求
            Socket accept;
            while ((accept = serverSocket.accept()) != null) {
                System.out.println("===client connected===");
                //获取socket的输入流和输出流
                InputStream inputStream = accept.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                OutputStream outputStream = accept.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                //通过输入流读取客户端发送的消息
                Message message= (Message) objectInputStream.readObject();
                System.out.println("server receive message:" + message.getContent());
                //通过输出流向客户端发送响应消息
                message.setContent("hello");
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        socketServer.start(6666);
    }
}
