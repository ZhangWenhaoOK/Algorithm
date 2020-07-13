package com.zookeeper.lock.impl;

import com.zookeeper.lock.ZooKeeperLock;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class NodeBlocklessLock implements ZooKeeperLock, Watcher {

    /**
     * 尝试获取锁
     */
    public boolean lock(String guidNodeName, String clientGuid) {
        boolean result = false;

        try {
            if (getZooKeeper().exists(guidNodeName, false) == null) {
                getZooKeeper().create(guidNodeName, clientGuid.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                byte[] data = getZooKeeper().getData(guidNodeName, false, null);
                if (data != null && clientGuid.equals(new String(data))) {
                    result = true;
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 释放锁
     */

    public boolean release(String guidNodeName, String clientGuid) {
        boolean result = false;
        Stat stat = new Stat();
        byte[] data = new byte[0];
        try {
            data = getZooKeeper().getData(guidNodeName, false, stat);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (data != null && clientGuid.equals(new String(data))) {
            try {
                getZooKeeper().delete(guidNodeName, stat.getVersion());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
            result = true;
        }
        return result;
    }


    /**
     * 锁是否已经存在
     */
    public boolean exists(String guidNodeName) {
        boolean result = false;
        Stat stat = null;
        try {
            stat = getZooKeeper().exists(guidNodeName, false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = stat != null;
        return result;
    }

    public static CountDownLatch countDownLatch = new CountDownLatch(1);
    ZooKeeper zooKeeper = null;

    private ZooKeeper getZooKeeper(){
        if (zooKeeper == null) {
            try {
                zooKeeper = new ZooKeeper("192.168.60.13:2181", 5000, new NodeBlocklessLock());
                countDownLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return zooKeeper;
    }

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            countDownLatch.countDown();
        }
    }

}
