package allTest;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Service {
    private static CuratorFramework zkClient;

    private static PathChildrenCache childrenCache;

    private String parentNodePath;

    private final Map<String, List<String>> SERVICE_MAP = null;

    /**
     * 获取zkClient
     *
     * @return
     */
    public CuratorFramework getZkClient() {
        if (zkClient != null) {
            return zkClient;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework zkClient = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        getWatcher(parentNodePath);

        return zkClient;
    }

    public void getWatcher(String path) {
        //注册监听器
        childrenCache = new PathChildrenCache(zkClient, path, true);
        //同步初始化缓存数据
        try {
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            //添加监听事件
            childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    System.out.println("监听到子节点变化");
                    List<String> children = curatorFramework.getChildren().forPath(parentNodePath);
                    String[] split = parentNodePath.split("/");
                    String serviceName = split[split.length - 1];
                    SERVICE_MAP.put(serviceName, children);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeAll() {
        try {
            childrenCache.close();
            zkClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
