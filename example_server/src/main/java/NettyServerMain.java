import com.bill.HelloService;
import com.bill.annotation.RpcScan;
import com.bill.config.ServiceConfig;
import com.bill.remoting.transport.netty.server.NettyServer;
import com.bill.service.serviceImpl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Server: Automatic registration service via @RpcService annotation
 *
 * @author shuang.kou
 * @createTime 2020年05月10日 07:25:00
 */
@RpcScan(basePackage = {"com.bill"})
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyServer nettyServer = (NettyServer) applicationContext.getBean("nettyServer");
        // Register service manually
        HelloService helloService2 = new HelloServiceImpl();
        ServiceConfig rpcServiceConfig = ServiceConfig.builder()
                .group("test2").version("version2").service(helloService2).build();
        nettyServer.registerService(rpcServiceConfig);
        nettyServer.run();
    }
}
