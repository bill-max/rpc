package com.bill.remoting.transport.socket;

import com.bill.remoting.dto.Request;
import com.bill.remoting.dto.Response;
import com.bill.remoting.handler.RequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable {

    private final Socket socket;

    private final RequestHandler requestHandler;
    public SocketRpcRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
        this.requestHandler = new RequestHandler();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try (
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        ) {
            Request request = (Request) inputStream.readObject();
            Object result = requestHandler.handle(request);
            log.info("<====server start====>");
            log.info("result: " + request);
            outputStream.writeObject(Response.success(result, request.getRequestId()));
            outputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
