import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.annotation.Scope;

import java.nio.charset.StandardCharsets;

//@Slf4j
public class ZkClient {

    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();
        System.out.println(curatorFramework);
        System.out.println("===> " + curatorFramework.getState().toString());
        try {
            if (curatorFramework.checkExists().forPath("/node1/001")==null) {
                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/node1/001");
                System.out.println("creat node");
            }
            curatorFramework.setData().forPath("/node1/001", "hello".getBytes(StandardCharsets.UTF_8));
            System.out.println("set node message: hello");
            byte[] bytes = curatorFramework.getData().forPath("/node1/001");
            System.out.println("message get:"+new String(bytes, StandardCharsets.UTF_8));
            //注册监听器
            String path = "/node1";
            PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
            //同步初始化缓存数据
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            //添加监听事件
            childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    System.out.println("监听到子节点变化");
                    byte[] data = pathChildrenCacheEvent.getData().getData();
                    System.out.println("子节点data:" + new String(data, StandardCharsets.UTF_8));
                    System.out.println("===》子节点：" + pathChildrenCacheEvent.getData());
                }
            });
            //更新子节点数据
            System.out.println("===change===");
            curatorFramework.setData().forPath("/node1/001", "message change".getBytes(StandardCharsets.UTF_8));
            System.out.println("===change===");


            Thread.sleep(1000);
            childrenCache.close();
            curatorFramework.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
