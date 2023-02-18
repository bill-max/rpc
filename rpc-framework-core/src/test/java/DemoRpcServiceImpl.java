import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemoRpcServiceImpl implements DemoRpcService {

    @Override
    public String hello(Hello message) {
        System.out.println("hello receive: " + message.getMessage());
        return message.getMessage();
    }
}
