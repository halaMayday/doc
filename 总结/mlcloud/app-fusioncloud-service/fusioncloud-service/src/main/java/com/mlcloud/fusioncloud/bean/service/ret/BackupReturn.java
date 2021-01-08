package com.mlcloud.fusioncloud.bean.service.ret;

import com.google.gson.annotations.SerializedName;
import com.mlcloud.defination.bean.ServiceReturn;
import lombok.Data;

import java.util.List;


/**
 * @author halaHuFan
 */
@Data
public final class BackupReturn implements ServiceReturn {
    private String moref;

    private String generation;

    @SerializedName("totaltime")
    private String totalTime;

    private List<BackupInfoBean> disk;

    @Data
    public static class BackupInfoBean{
        private String diskid;

        private String status;

        private String size;

        @SerializedName("rdiffsize")
        private String rDiffSize;

        @SerializedName("elapsedtimems")
        private String elapsedTimeMs;

        private String speed;

        private String mode;
    }
}
