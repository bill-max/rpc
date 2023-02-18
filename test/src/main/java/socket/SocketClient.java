package socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {

    public Object send(Message message, String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            //客户端通过输出流发送消息
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            //通过输入流获取响应
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        Message clientSendMessage = (Message) socketClient.send(new Message("client send message"), "127.0.0.1", 6666);
        System.out.println("back message:"+clientSendMessage.getContent());;
    }
}
