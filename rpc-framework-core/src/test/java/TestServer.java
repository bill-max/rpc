import com.bill.config.ServiceConfig;
import com.bill.proxy.ClientProxy;
import com.bill.remoting.transport.socket.SocketClient;
import com.bill.remoting.transport.socket.SocketServer;
import org.junit.jupiter.api.Test;

public class TestServer {
    private ServiceConfig serviceConfig;

    @Test
    void testServer() {
        DemoRpcService service1 = new DemoRpcServiceImpl();
        serviceConfig = ServiceConfig
                .builder()
                .group("")
                .version("")
                .service(service1)
                .build();
        SocketServer socketServer = new SocketServer();
        socketServer.registryServer(serviceConfig);
        socketServer.start();

    }

    @Test
    void testClient() {
//        RequestTransport requestTransport = new SocketClient();
//        DemoRpcService service1 = new DemoRpcServiceImpl();
//        serviceConfig = ServiceConfig
//                .builder()
//                .group("")
//                .version("")
//                .service(service1)
//                .build();
//        Request request = new Request();
//        request.setGroup("test");
//        request.setVersion("1");
//        request.setInterfaceName(serviceConfig.getServiceName());
//        ClientProxy clientProxy = new ClientProxy(requestTransport, serviceConfig);
        ClientProxy clientProxy = new ClientProxy(new SocketClient(), serviceConfig);
        DemoRpcService proxy = clientProxy.getProxy(DemoRpcService.class);
        String qqq = proxy.hello(new Hello("qqq"));
        System.out.println(qqq);
//        DemoRpcService proxy = (DemoRpcService) clientProxy.getProxy(new DemoRpcServiceImpl());
//        String hh = proxy.hello(new Hello("hh"));
//        System.out.println("result:===>" );
//        System.out.println(hh);
//        request.setParamTypes(new Class[]{DemoRpcService.class});
//        Response response = (Response) requestTransport.sendRequest(request);
//        System.out.println(response.getData().toString());

    }
}
