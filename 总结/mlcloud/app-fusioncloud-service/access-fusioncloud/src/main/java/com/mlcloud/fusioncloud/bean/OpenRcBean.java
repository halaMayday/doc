package com.mlcloud.fusioncloud.bean;

import com.mlcloud.defination.util.AESUtil;
import com.mlcloud.fusioncloud.ws.WsURLStreamHandlerFactorySpiImpl;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.client.IOSClientBuilder;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.client.OSClientBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author ：hf
 * @date ：Created in 2020/12/8 5:39 下午
 * @modified By：
 * @version: $
 */
public class OpenRcBean {

    private static final String OS_IDENTITY_API_VERSION = "3";

    private String osProjectDomainName;

    private String osUserDomainName;

    private String osProjectId;

    private String osUserName;

    private String osPassword;


    private String serverIp;

    private String port;

    private boolean ssl;

    private boolean isOpenstack;

    static {
        URL.setURLStreamHandlerFactory(new WsURLStreamHandlerFactorySpiImpl());
    }

    public OpenRcBean(String osProjectId, String osProjectDomainName, String osUserDomainName,
                      String osUserName, String osPassword, String serverIp, String port, boolean isOpenstack) {
        this.osProjectDomainName = osProjectDomainName;
        this.osUserDomainName = osUserDomainName;
        this.osUserName = osUserName;
        this.osPassword = osPassword;
        this.osProjectId = osProjectId;
        this.serverIp = serverIp;
        this.port = port;
        //TODO 兼容fusionclod. 通用openstack可以使用http
        this.ssl = "443".equals(port);
        this.isOpenstack = isOpenstack;
    }

    public static String getOsIdentityApiVersion() {
        return OS_IDENTITY_API_VERSION;
    }
    public String getOsProjectDomainName() {
        return osProjectDomainName;
    }

    public String getOsUserDomainName() {
        return osUserDomainName;
    }

    public String getOsProjectId() {
        return osProjectId;
    }

    public String getOsUserName() {
        return osUserName;
    }

    public String getOsPassword() {
        return osPassword;
    }
    public boolean isOpenstack() {
        return isOpenstack;
    }


    public String getOSAUTHURL() {
        if (isOpenstack) {
            return buildOpenstack();
        }
        return buildHuaWeiFusionCloud();
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getPort() {
        return port;
    }


    private String buildHuaWeiFusionCloud() {
        if (isSsl()) {
            return "https://" + this.serverIp + ":" + this.port + "/identity-admin/v3";
        } else {
            return "http://" + this.serverIp + ":" + this.port + "/identity-admin/v3";
        }
    }

    private String buildOpenstack() {
        if (isSsl()) {
            return "https://" + this.serverIp + ":" + this.port + "/v3";
        } else {
            return "http://" + this.serverIp + ":" + this.port + "/v3";
        }
    }

    public boolean isSsl() {
        return ssl;
    }

    public OSClient.OSClientV3 getOpenstackClient() {
        return useNonStrictSSLClient()
                .endpoint(getOSAUTHURL())
                .credentials(getOsUserName(),
                        getOsPassword(),
                        Identifier.byName(getOsUserDomainName()))
                .scopeToProject(Identifier.byId(getOsProjectId()))
                .authenticate();
    }

    public OSClient.OSClientV3 getOpenstackClientWithoutProjectId()
            throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        if ("admin".equals(getOsUserName())) {
            return useNonStrictSSLClient()
                    .endpoint(getOSAUTHURL())
                    .credentials(getOsUserName(),
                            AESUtil.decode(getOsPassword()),
                            Identifier.byName(getOsUserDomainName()))
                    .scopeToProject(Identifier.byName(getOsUserName()),
                            Identifier.byName(getOsUserDomainName()))
                    .authenticate();
        }

        return useNonStrictSSLClient()
                .endpoint(getOSAUTHURL())
                .credentials(getOsUserName(),
                        AESUtil.decode(getOsPassword()),
                        Identifier.byName(getOsUserDomainName()))
                .authenticate();
    }

    private IOSClientBuilder.V3 useNonStrictSSLClient() {
        OSClientBuilder.ClientV3 v3 = new OSClientBuilder.ClientV3();
        v3.useNonStrictSSLClient(true);
        return v3;
    }

    @Override
    public String toString() {
        return "OpenRcBean{" +
                "ssl=" + ssl +
                ", osProjectDomainName='" + osProjectDomainName + '\'' +
                ", osUserDomainName='" + osUserDomainName + '\'' +
                ", osProjectId='" + osProjectId + '\'' +
                ", osUserName='" + osUserName + '\'' +
                ", osPassword='mulang-log-password''" +
                ", serverIp='" + serverIp + '\'' +
                ", port='" + port + '\'' +
                ", isOpenstack=" + isOpenstack +
                '}';
    }


}
