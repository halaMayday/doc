package com.mlcloud.fusioncloud.service;

import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.exception.NoAvailableAgentException;
import com.mlcloud.defination.service.BaseService;
import com.mlcloud.defination.util.AESUtil;
import com.mlcloud.fusioncloud.FusinonCloudAccess;
import com.mlcloud.fusioncloud.bean.OpenRcBean;
import com.mlcloud.fusioncloud.exception.QueryPhysicalHostException;
import com.mlcloud.fusioncloud.respool.AgentPool;
import com.mlcloud.local.exception.os.LocalReadException;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:45 下午
 * @modified By：
 * @version: $
 */
public abstract class BaseFusioncloudService extends BaseService {
    /**
     *  private static final String SDK_TYPE_HUAWEI_FUSION_CLOUD = "7";
     *  public static final String SDK_TYPE_GENERAL_OPENSTACK = "9";
     */
    private static final String SDK_TYPE = "7";

    private static final String DEFAULT_CEPH_CLIENT_ID = "fusioncloud";

    private static final String DEFAULT_CEPH_CONF = "/etc/ceph/ceph.conf";

    BaseFusioncloudService(String configFilePath)
        throws LoadConfigurationException {
        super(SDK_TYPE, configFilePath);
    }

    BaseFusioncloudService(String configFilePath, String instanceId)
        throws LoadConfigurationException, LocalReadException {
        super(SDK_TYPE, configFilePath, instanceId);
    }

    BaseFusioncloudService(String configFilePath, String instanceId, String curGeneration)
        throws LoadConfigurationException {
        super(SDK_TYPE, configFilePath, instanceId, curGeneration);
    }

    BaseFusioncloudService(String configFilePath, String instanceId, String generation, String localhost, String taskName, String logId)
        throws LoadConfigurationException{
        super(SDK_TYPE, configFilePath, instanceId, generation, localhost, taskName, logId);
    }

    private static final String PLATFORM_SECTION = "fusioncloud";

    private static final String COMMON_SECTION = "global";

    FusinonCloudAccess access;

    String cephConf;

    String radosUserId;

    @Override
    protected void parseConfigurationFile(String filePath) throws LoadConfigurationException {
        try {
            Ini ini = new Ini();
            ini.load(new FileReader(filePath));
            Section platform = ini.get(PLATFORM_SECTION);
            Section common = ini.get(COMMON_SECTION);
            this.mdfsRoot = common.get("mdfsroot");
            this.mdfsRoot = this.mdfsRoot.substring(0, mdfsRoot.length() - 1);
            this.apiHost = platform.get("server");
            this.radosUserId = platform.getOrDefault("rados_user_id", DEFAULT_CEPH_CLIENT_ID);
            this.cephConf = platform.getOrDefault("ceph_conf", DEFAULT_CEPH_CONF);

            String osProjectDomainName = platform.get("projectDomainName");
            String osUserDomainName = platform.get("userDomainName");
            String osProjectId = platform.get("projectId");
            String osUserName = platform.get("userName");
            String osPassword = AESUtil.decode(platform.get("passWord"));
            String serverIp = platform.get("serverIp");
            String port = platform.get("port");
            boolean openstack = "true".equals(platform.get("isOpenstack"));
            OpenRcBean openRcBean = new OpenRcBean(osProjectId,osProjectDomainName,osUserDomainName,osUserName,osPassword,serverIp,port,openstack);
            this.access = new FusinonCloudAccess(openRcBean);

        } catch (Exception e) {
            this.access = null;
            throw new LoadConfigurationException(e.getMessage());
        }
    }

    String getImageSpec(String poolName, String resId){
        return String.format("%s/volume-%s", poolName, resId);
    }

    String getSnapshotName(){
        return String.format("mulangcloud_%s", UUID.randomUUID());
    }

    String getSnapshotNameInCeph(String snapshotId){
        return String.format("snapshot-%s", snapshotId);
    }

    private AgentPool agentPool = null;

    AgentPool initAgentPool() throws NoAvailableAgentException, QueryPhysicalHostException {
        List<String> probingIps = new ArrayList<>();
        //todo ：test case 测试完可以删除
        probingIps.add("192.168.14.114");
        probingIps.add("192.168.14.113");

        access.getPhysicalHostList().forEach(host -> probingIps.add(host.getHostIP()));
        this.agentPool =  AgentPool.initAgentPool(probingIps, this.getNfsDir(), this.getMountpoint());
        return agentPool;
    }

    String getAvailableHost() {
        return this.agentPool.allocate();
    }

    void dischargeAgentPool() {
        if (this.agentPool != null && this.agentPool.isInUse()) {
            agentPool.discharge();
        }
    }
}
