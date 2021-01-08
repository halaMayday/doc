package com.mlcloud.fusioncloud.bean;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.openstack4j.model.network.State;
import org.openstack4j.openstack.networking.domain.NeutronIP;

import java.util.Set;

/**
 * @author ：hf
 * @date ：Created in 2020/12/8 10:39 上午
 * @modified By：
 * @version: $
 */
@Data
@Builder
public class DetailPortResponse {

    private String id;

    private String name;

    private String networkId;

    private Set<NeutronIP> fixedIps;

    private State state;

    @Tolerate
    DetailPortResponse(){}
}
