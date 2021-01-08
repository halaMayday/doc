package com.mlcloud.fusioncloud.bean;

import com.mlcloud.common.bean.Pair;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ：hf
 * @date ：Created in 2020/12/7 9:57 下午
 * @modified By：
 * @version: $
 */
@Data
public class InstanceModel {


    private DetailInstanceResponse detail;

    private Map<String, Set<Pair<Long, Integer>>> volDiffBitmap;

    private List<DetailPortResponse> ports;

    private  List<DetailVolumeResponse> volumeDetailList;
}
