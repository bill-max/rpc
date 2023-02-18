package com.bill.remoting.transport.socket;

import com.bill.loadBanlance.LoadBalance;
import com.bill.loadBanlance.impl.RandomLoadBalance;
import com.bill.registry.Service;
import com.bill.registry.impl.ServiceImpl;
import com.bill.remoting.dto.Request;
import com.bill.remoting.transport.RequestTransport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient implements RequestTransport {


    /**
     * 消息传输实现类
     *
     * @param request 请求
     * @return 服务端返回消息
     */
    @Override
    public Object sendRequest(Request request) {
        Service service = new ServiceImpl();
        LoadBalance loadBalance = new RandomLoadBalance();
        InetSocketAddress inetSocketAddress = service.lookupService(request, loadBalance);
        try (
                Socket socket = new Socket();
        ) {
            socket.connect(inetSocketAddress);
            //发送消息
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(request);
            outputStream.flush();
            //接收回显
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
