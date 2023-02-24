import com.bill.factory.SingletonFactory;
import com.bill.provider.ServiceProvider;
import com.bill.provider.impl.ServiceProviderImpl;
import com.bill.remoting.transport.netty.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;


public class Test {

    @org.junit.Test
    public void testS() {
        ServiceProvider instance = SingletonFactory.getInstance(ServiceProviderImpl.class);
        System.out.println(instance);
    }
}
