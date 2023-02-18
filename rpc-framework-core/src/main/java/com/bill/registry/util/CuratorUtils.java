package com.bill.registry.util;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CuratorUtils {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static CuratorFramework zkClient;
//    private static String parentNodePath;
    private static PathChildrenCache childrenCache;

    //服务注册表
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    /**
     * 获取 客户端
     */
    public static CuratorFramework getZkClient() {
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        RetryPolicy retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(retry)
                .build();
        zkClient.start();
//        getWatcher(parentNodePath);
        return zkClient;
    }

    private static void getWatcher(String path) {
        //注册监听器
        if (childrenCache!=null) return;
        childrenCache = new PathChildrenCache(zkClient, path, true);
        //同步初始化缓存数据
        try {
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            //添加监听事件
            childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    System.out.println("监听到子节点变化");
                    List<String> children = curatorFramework.getChildren().forPath(path);
                    String[] split = path.split("/");
                    String serviceName = split[split.length - 1];
                    SERVICE_ADDRESS_MAP.put(serviceName, children);
                }
            });
//            childrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建持久结点
     *
     * @param zkClient
     * @param servicePath
     */
    public static void createPersistentNode(CuratorFramework zkClient, String servicePath) {
        try {
            if (zkClient.checkExists().forPath(servicePath) != null) {
                return;
            }
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注册子节点
     *
     * @param zkClient
     * @param serviceName
     * @return
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String serviceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(serviceName)) {
            return SERVICE_ADDRESS_MAP.get(serviceName);
        }
        List<String> childrenNodes = new ArrayList<>();
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        try {
            //注册子节点
            childrenNodes = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName, childrenNodes);
            getWatcher(servicePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return childrenNodes;
    }


    public void closeAll() {
        try {
            childrenCache.close();
            zkClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearAll() {
        childrenCache = null;
        zkClient = null;
    }

}
