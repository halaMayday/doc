package com.mlcloud.fusioncloud.service;


import com.google.gson.Gson;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.bean.service.GenerationInfoBean;
import com.mlcloud.defination.bean.service.TaskBean;
import com.mlcloud.defination.bean.util.RedisLogBean;
import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.service.SpeedTrackService;
import com.mlcloud.fusioncloud.bean.service.accept.BackupSpeedTrackParam;
import com.mlcloud.fusioncloud.bean.service.accept.RestoreSpeedTrackParam;
import com.mlcloud.fusioncloud.bean.service.ret.BackupSpeedTrackReturn;
import com.mlcloud.fusioncloud.bean.service.ret.RestoreSpeedTrackReturn;
import com.mlcloud.local.LocalAccess;
import com.mlcloud.local.bean.IOProperty;
import com.mlcloud.local.bean.IORecord;
import com.mlcloud.local.exception.os.LocalReadException;

import java.util.HashMap;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:57 下午
 * @description： copy from shenzhouSoft-service
 */
public class FusioncloudSpeedTrackService extends BaseFusioncloudService implements SpeedTrackService {

    public FusioncloudSpeedTrackService(String configFilePath, String instanceId)
            throws LoadConfigurationException, LocalReadException {
        super(configFilePath, instanceId);
    }
    public FusioncloudSpeedTrackService(String configFilePath, String instanceId, String curGeneration)
            throws LoadConfigurationException {
        super(configFilePath, instanceId, curGeneration);
    }

    @Override
    public ServiceReturn trackBackupSpeed(ServiceParam serviceParam) throws BaseException {
        BackupSpeedTrackParam param = (BackupSpeedTrackParam) serviceParam;
        String instanceId = param.getInstanceId();
        IORecord ioRecord = initDefaultRecord();
        String progress = "0";
        try {
            Gson gson = new Gson();
            String generation = gson.fromJson(LocalAccess.os().read(this.getGenerationFile(instanceId)), GenerationInfoBean.class).getCurGen();
            TaskBean taskBean = loadTaskBean(instanceId, generation);
            //TaskBean的endTime字段在任务未结束时会被初始化为INFINATE, 若小于当前时间，则说明是执行其他任务留下的记录
            if(taskBean.getEndTime() > System.currentTimeMillis()){
                progress = taskBean.getProgress();
                ioRecord = loadIORecords(instanceId, taskBean.getTaskUUID());
                this.sendTransferProcess(instanceId, generation, ioRecord);
            }
        } catch (LocalReadException e) {
            logger.info("TASK NOT INITIATED YET");
            logger.error(e.getMessage());
        }
        return this.initBackupSpeedTrackRetBean(instanceId, ioRecord, progress);
    }

    @Override
    public ServiceReturn trackRestoreSpeed(ServiceParam serviceParam) throws BaseException {
        RestoreSpeedTrackParam param = (RestoreSpeedTrackParam) serviceParam;
        String instanceId = param.getInstanceId();
        String generation = param.getGenerationNum();
        IORecord ioRecord = initDefaultRecord();
        String progress = "0";
        try {
            TaskBean taskBean = this.loadTaskBean(instanceId, generation);
            //TaskBean的endTime字段在任务未结束时会被初始化为INFINATE, 若小于当前时间，则说明是执行其他任务留下的记录
            if(taskBean.getEndTime() > System.currentTimeMillis()){
                progress = taskBean.getProgress();
                ioRecord = loadIORecords(instanceId, taskBean.getTaskUUID());
                this.sendTransferProcess(instanceId, generation, ioRecord);
            }
        } catch (LocalReadException e) {
            logger.info("TASK NOT INITIATED YET");
            logger.error(e.getMessage());
        }
        return this.initRestoreSpeedTranckRetBean(instanceId, ioRecord, progress);
    }


    /**
     * 当文件未建立时返回的bean
     */
    private IORecord initDefaultRecord() {
        IORecord ioRecord = new IORecord();
        ioRecord.setIoPropertyTab(new HashMap<>());
        return ioRecord;
    }

    /**
     * 加载task文件
     */
    private TaskBean loadTaskBean(String instanceId, String generation)
            throws LocalReadException {
        Gson gson = new Gson();
        return gson.fromJson(LocalAccess.os().read(this.getTaskFilePath(instanceId, generation)), TaskBean.class);
    }

    /**
     * 加载速度追踪文件
     */
    private IORecord loadIORecords(String instanceId, String taskUUID)
            throws LocalReadException {
        Gson gson = new Gson();
        return gson.fromJson(LocalAccess.os().read(this.getIORecordFile(instanceId, taskUUID)), IORecord.class);
    }

    private void sendTransferProcess(String instanceId, String generation, IORecord ioRecord)
            throws LocalReadException {
        long transfered = ioRecord.getIoPropertyTab().entrySet().stream().mapToLong(entry -> entry.getValue().getTransfered()).sum();
        long total = ioRecord.getIoPropertyTab().entrySet().stream().mapToLong(entry -> entry.getValue().getTotal()).sum();
        float divisor = (float) transfered;
        float dividend = (float) total;
        float process = divisor / dividend * 100;
        String stepInfoFile = this.getStepInfoRecordFile(instanceId, generation);

        Gson gson = new Gson();
        RedisLogBean stepBean = gson.fromJson(LocalAccess.os().read(stepInfoFile), RedisLogBean.class);
        if (process > 0 && process < 100) {
            stepBean.setProgress(String.format("%.2f", process));
        }
        else {
            stepBean.setProgress("");
        }
        this.logStepInfo(stepBean, false);
    }

    /**
     * 初始化T命令的返回bean
     */
    private BackupSpeedTrackReturn initBackupSpeedTrackRetBean(String instanceId, IORecord ioRecord, String progress) {
        long transfered = ioRecord.getIoPropertyTab().values().stream().mapToLong(IOProperty::getTransfered).sum();
        double instanceSpeed = ioRecord.getIoPropertyTab().values().stream().mapToDouble(IOProperty::getInstantSpeed).sum();
        long startpoint = ioRecord.getIoPropertyTab().values().stream().mapToLong(IOProperty::getStartpoint).min().orElse(Long.MIN_VALUE);
        long endpoint = ioRecord.getIoPropertyTab().values().stream().mapToLong(IOProperty::getCheckpoint).max().orElse(System.currentTimeMillis());
        long spend = endpoint - startpoint;
        double averageSpeed = transfered == 0 ? 0 : (double) transfered / spend * Math.pow(10, 3);
        BackupSpeedTrackReturn bean = new BackupSpeedTrackReturn();
        bean.setVm(instanceId);
        long transferedMb = transfered / 1024 / 1024;
        bean.setRead(transferedMb);
        bean.setStorage(Double.toString(transferedMb));
        bean.setSize(transferedMb);
        bean.setSpeed(Double.toString(instanceSpeed));
        bean.setAvgSpeed(Double.toString(averageSpeed));
        bean.setProgress(progress);
        return bean;
    }

    /**
     * 初始化RPT命令的返回bean
     */
    private RestoreSpeedTrackReturn initRestoreSpeedTranckRetBean(String instanceId, IORecord ioRecord, String progress) {
        long transfered = ioRecord.getIoPropertyTab().values().stream().mapToLong(IOProperty::getTransfered).sum();
        double instanceSpeed = ioRecord.getIoPropertyTab().values().stream().mapToDouble(IOProperty::getInstantSpeed).sum();
        long startpoint = ioRecord.getIoPropertyTab().values().stream().mapToLong(IOProperty::getStartpoint).min().orElse(Long.MIN_VALUE);
        long endpoint = ioRecord.getIoPropertyTab().values().stream().mapToLong(IOProperty::getCheckpoint).max().orElse(System.currentTimeMillis());
        long spend = endpoint - startpoint;
        double averageSpeed = transfered == 0 ? 0 : (double) transfered / spend * Math.pow(10, 3);
        RestoreSpeedTrackReturn bean = new RestoreSpeedTrackReturn();
        bean.setVm(instanceId);
        long transferedMb = transfered / 1024 / 1024;
        bean.setWrite(transferedMb);
        bean.setStorage(Long.toString(transferedMb));
        bean.setSize(transferedMb);
        bean.setSpeed(Double.toString(instanceSpeed));
        bean.setAvgSpeed(Double.toString(averageSpeed));
        bean.setProgress(progress);
        return bean;
    }
}
