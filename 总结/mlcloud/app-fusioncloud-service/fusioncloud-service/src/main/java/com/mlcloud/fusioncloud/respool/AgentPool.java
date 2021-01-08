package com.mlcloud.fusioncloud.respool;

import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.exception.NoAvailableAgentException;
import com.mlcloud.rpc.client.RemoteAccess;
import com.mlcloud.rpc.client.exception.AgentStatusErrorException;
import com.mlcloud.rpc.client.exception.os.RpcMakeDirException;
import com.mlcloud.rpc.client.exception.os.RpcMountNfsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:14 下午
 * @description：
 * @modified By：
 * @version: $
 */
public class AgentPool {

    private boolean inUse;

    private static Logger logger = LoggerFactory.getLogger(AgentPool.class);

    private List<String> nodes = new ArrayList<>();

    private int nodeIndex = 0;

    public String allocate() {
        String node = nodes.get(nodeIndex);
        nodeIndex = (nodeIndex + 1) % nodes.size();
        return node;
    }

    /**
     * host->mountpoint
     */
    private Map<String, String> mountingTable = new HashMap<>();

    private AgentPool(List<String> probingNodes, String mountTarget, String mountpoint)
            throws NoAvailableAgentException {
        Set<String> probed = new HashSet<>();
        for (String ip : probingNodes) {
            if (probed.contains(ip)) {
                continue;
            }
            probed.add(ip);
            logger.info("AGENT PROBING: {}", ip);
            try {
                if (!RemoteAccess.os(ip).dirExist(mountpoint)) {
                    RemoteAccess.os(ip).makeDir(mountpoint);
                }
                RemoteAccess.os(ip).mountNfs(mountTarget, mountpoint);
                mountingTable.put(ip, mountpoint);
                nodes.add(ip);
                logger.info("AVAILABLE NODE: {}", ip);
            } catch (AgentStatusErrorException | RpcMountNfsException | RpcMakeDirException e) {
                logger.info("UNUSABLE NODE: {}", ip);
            }
        }
        if (nodes.isEmpty()) {
            throw new NoAvailableAgentException("没有查询到可用的备份agent节点");
        }
        this.inUse = true;
    }

    public static AgentPool initAgentPool(List<String> probingNodes, String mountTarget, String mountpoint)
            throws NoAvailableAgentException {
        return new AgentPool(probingNodes, mountTarget, mountpoint);
    }

    public void discharge() {
        for (Map.Entry<String, String> entry: this.mountingTable.entrySet()) {
            try {
                String host = entry.getKey();
                String mountpoint = this.mountingTable.get(host);
                RemoteAccess.os(host).umount(mountpoint);
                RemoteAccess.os(host).delete(mountpoint);
            } catch (BaseException e) {
                logger.error("{}: {}", e.getCode().getCode(), e.getCode().getMessage());
            }
        }
        this.inUse = false;
        logger.info("AGENT POOL DISCHARGED");
    }

    public boolean isInUse() {
        return this.inUse;
    }
}
