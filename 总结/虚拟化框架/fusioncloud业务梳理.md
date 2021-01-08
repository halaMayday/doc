
### app-funsioncloud-openstack业务梳理归纳总结

#### 0：重构后项目结构以及模块划分简介
参照shenzhouSoft项目的项目结构。

- app-funsioncloud-service项目包含三个module，如下：  

> access-funsioncloud：  
&emsp;这个模块对应虚拟化框架中的access-platform模块部分。   
&emsp;作用是：  
&emsp;&emsp;对openstack4j这个sdk的封装，处理sdk抛出来的异常，也是后续版本优化升级的铺垫，而做的统一规范要求。funsioncloud-service业务逻辑层访问底层的接口都是通过 access-funsioncloud这个模块。 不直接调用sdk，而是通过这个模块。  
&emsp;&emsp;eg：

```
tStackAccess.createEmptyVolume(XXXX)
```
> funsioncloud-service ：  
&emsp;funsioncloud的业务逻辑模块。提供拷贝，克隆，挂载恢复等功能。  
&emsp;&emsp;controller层使用注解@Action接收中台的命令行调用。  
&emsp;&emsp;service层使用 access-funsioncloud模块来调用底层和sdk的api。  
---
> openstack4j：  
    封装的openstack的sdk包。包含三个moudle
    connector：httpclint的封装部分，http交互之类的东西。
    core：api枚举和接口
    openstack-client：类似于openstack中的dashboard，登录后通过client.compute()这种方式调用相应的模块。
---
#### 1：业务梳理以及改动说明
下面通过原来funsioncloud项目的service层去梳理原来项目的业务逻辑。同时会结合现在虚拟化框架的使用，以及shenzhousSoft项目做对比，来列出相应的改动说明。
改动说明部分较为简略。
##### 1.1：ConnService  

认证服务，主要提供auth认证，获取token服务

**fusioncloud项目:**   

    auth():提供登录鉴权  

**新虚拟化框架：** 

ConnectService  

     ServiceReturn connectionQuery(ServiceParam var1) throws BaseException;  

**改动说明**：
    登录鉴权验证的部分，应该挪到openStack4j这个moudle的client模块中。
    
-----------
##### 1.2：HostService
**fusioncloud项目:**

    list()：提供实例信息查询

**新虚拟化框架：**

ListService

```
ServiceReturn getInstancesList(ServiceParam var1) throws BaseException;  
ServiceReturn getInstancesDetailList(ServiceParam var1) throws BaseException  
ServiceReturn getHostList(ServiceParam var1) throws BaseException;
```

**改动说明：**
    改用虚拟化框架新的调用模式来写。不直接调用sdk。而是通过access-funsioncloud这个模块来实现。
-------------
##### 1.3：VersionService

**fusioncloud项目: **
    
查询版本号信息，query()方法提供查询版本信息的功能。
```
public class VersionService {
    public ExecuteResult query() {
        return ExecuteResult.buildSuccess(Version.INFO);
    }
}
/**************************/
public class Version {
    private Version() {
    }

    public static final String INFO = "version : 1.0";
    public static final String COPY_RIGHT = "mulang cloud";
    public static final String SDK_TYPE = "7";
}
```
**==改动==**：
移除这个服务，原项目中仅仅只是返回固定版本信息。
或者以配置文件，或者常量的形式进行读取。
----------
##### 1.4：BackupService
**==改动说明==：**  

    原来fusioncloud项目的备份逻辑较为混乱，准备使用shenzhouSoft项目备份的业务逻辑

**fusioncloud项目:** 略

**shenzhouSoft项目**

备份业务逻辑：

- 01：检查备份前置条件checkPrerequisite
checkPrerequisite的业务逻辑为：
> 获取实例id信息
> 根据实例ID获取备份文件信息
> 设置是否为全量备份。
判断备份世代信息文件是否存在？
    如果不存在，设置为全量备份。
    如果存在，继续判断该文件内容是否为空？
        如果为空，设置为全量备份。
        如果不为空，继续检查判断，最后一次备份成功的目录，是否存在？
            如果不存在，则设置为全量备份。
            
> 判断是否为全量备份？然后设置相应的generationInfo信息
---
- 02：创建工作目录  
固化模块，可以引用shenzhou项目
buildWorkingDir()，创建相应的目录即可。
---
03：查询备份实例详情

由sdk的client的compute提供 实例查询接口的实现 

对应openstack的api为：

```
//todo
```
---
04：建立rbd镜像定义符的索引  
建立卷id（volumeId），和镜像id（imageSpec）的对应关系
---

05：创建快照  
sdk的storage模块提供创建快照的接口实现  
client.storage().createSnapshot
对应openstack的api为：

```
//todo
```
---

06：初始化数据导出类型(全量/增量)

    确定是全量备份还是默认增量备份

---

07：初始化资源导出超时阈值

根据卷的容量大小来确定导出数据的超时阈值

```
int coe = volume.getSize();
int timeout = 10 * coe * coe;
// 最大timeout = 3600 * 24;
```

---

08：创建备份数据环境  
根据备份类型做判断  
如果为全量备份，就需要创建空白文件副本  
如果为增量备份，则需要从持久化的目录中获取mdfs副本。

---

09：导出数据  
从agent pool里获取可用的rpc服务器节点，导出备份。  
记录相应的关系

```
/*
*bitmap:差量位图
*resId：资源Id，host：可用的rpc服务节点
*/
Set<Pair<Long, Integer>> bitmap = exportDiffBitmap(resId, host);
volBitmap.put(resId, bitmap);
```
---
10：持久化镜像数据
```
LocalAccess.os().move(tempFile, persitentFile);
```

---

11：记录备份实例详情 

```
LocalAccess.os().write(JSON.toJSONString(instanceModel), recordFile);
```
---
12：删除过期的快照

```
client.storage().deleteSnapshot(snapshotId);
```
---
13：更新备份generation信息

    generation代表的是备份的世代数，比如第一代备份、第二代备份、第三代备份。。。
    更新备份世代信息
    断开nfs

14:回滚机制

回滚机制是靠异常控制。  
回滚步骤：

    implements Rollbackable，重写rollBack。
    调用tStackAccess.deleteSnapshot(id)删除平台上台快照，
    调用LocalAccess.os().delete(persitentImageFile)删除持久化目录中的文件
    断开NFS
    
##### 1.5：OpenStackVmService

- **fusioncloud：**  
此模块为虚机管理模块，负责虚拟机开关机，虚拟机创建等功能。  

```
//开机
powerOnVmByUuid(String vmUuid)，deleteVm(String vmId)  
//关机
powerOffVmByUuid(String vmUuid)，powerOffVm(String vmId)  
//检查电源状态
checkPowerStateByVmUuid(String vmUuid)，checkPowerStateByVmId(String vmId)
//卸载虚拟机卷
deleteVolume()
//创建虚拟机卷
createVolume()
//挂载虚拟机卷
attacheVolume()
//查询project
list()和listFullInfo()
//查询 vm ip
queryVmIp()
//查询虚拟机详情信息
queryVmDetails
//查询卷信息
queryVmVolumes(NovaServer novaServer)
//查询卷的详细信息
queryVolumeDetail(String volumeId)
//重新认证
reAuthenticate()
//query vm os
queryVmOS
//卸载虚拟机
deleteVm(String vmId)
```
---
- **改动说明 ：**  
不符合单一职责的原则，OpenStackVmService里混杂着太多的功能可以拆分为如下

StatusService模块 提供开关机，电源状态查询功能

```
ServiceReturn powerOnVm(ServiceParam var1) throws BaseException;

ServiceReturn powerOffVm(ServiceParam var1) throws BaseException;

ServiceReturn qeuryStatus(ServiceParam var1) throws BaseException;
```
---
其余的服务属于ComputeService模块
sdk里封装相应的computer模块，实现对应的功能，
---
##### 1.6 CancelService

中断，取消服务。

**fusioncloud项目:**


```
public ExecuteResult cancel(String processId, String vmRootPath, String vmUuid, String computeIp){
    //略
}
```
**新虚拟化框架：**  
**改动说明：**
SignalService

```
//继承SingalService，实现signalCancel方法
```
##### 1.7 RestoreService

恢复，还原模块
**改动说明：**
原来项目逻辑较为混乱，可以采用shenzhouSoft项目中的恢复还原部分业务逻辑。  
这部分模块，暂时不支持回滚，后续可以优化。

&emsp; 1、初始化恢复服务  

&emsp; &emsp; 1.1 读取加载需要恢复的实例模型  
&emsp; &emsp; 1.2 创建临时目录  
&emsp; &emsp; 1.3 挂载nfs

&emsp; 2、查询当前虚拟机信息

&emsp; 3、卸载当前虚拟机的所有硬盘  

&emsp; 4、创建空白硬盘  

&emsp; 5、挂载空白硬盘  

&emsp; 6、建立rbd镜像索引  

&emsp; 7、创建恢复数据环境  

&emsp; &emsp; 创建临时目录，并在临时目录中创建持久化镜像的副本快照

&emsp; 8、初始化资源导入超时阈值

```
//根据卷的大小，确定超时的阈值。0< timeout <=3600 * 24
int coe = volume.getSize();
int timeout = 10 * coe * coe + 600;
```
&emsp; 9、导入数据  
&emsp;&emsp;通过rados，操作ceph，将本地持久化的镜像导入rbd块设备中。

&emsp; 10、删除过期硬盘  

&emsp; 11、恢复备份副本的网络配置  
&emsp;&emsp;11.1 按照备份时候的网卡配置创建新的网卡  
&emsp;&emsp;11.2 绑定新的网卡  
&emsp;&emsp;11.3 删除之前的网卡  

&emsp; 12、初始化虚拟机的电源  
&emsp;&emsp;开机

&emsp; 13、恢复完毕  
&emsp;&emsp;关闭相关资源。


---
##### 1.8 SpeedService
备份，恢复进度
**改动说明：** 
原来项目逻辑较为混乱，可以采用shenzhouSoft项目中的业务逻辑。

```
//继承service-defination的SpeedTrackService，实现
ServiceReturn trackRestoreSpeed(ServiceParam var1) throws BaseException;

ServiceReturn trackBackupSpeed(ServiceParam var1) throws BaseException;
```

##### 1.9 CloneService

这部分原来的funsioncloud项目中没有，可以参照shenzhouSoft项目进行新增。

克隆的业务流程：  
&emsp; 1、初始化克隆服务   
&emsp;&emsp;1.1 克隆机器模型，读取拷贝这些信息  
&emsp;&emsp;1.2 创建临时目录  
&emsp;&emsp;1.3 挂载nfs  

&emsp; 2、创建空白虚拟机   
&emsp;&emsp;2.1 创建虚拟机  
&emsp;&emsp;2.2 删除Flavor  
&emsp;&emsp;2.3建立新旧硬盘的id映射  

&emsp; 3、创建空白卷 

&emsp; 4、挂载空白卷

&emsp; 5、关闭虚拟机

&emsp; 6、建立rbd镜像索引

&emsp; 7、创建克隆数据环境
&emsp;&emsp;创建临时目录，并在临时目录中创建持久化镜像的副本快照

&emsp; 8、初始化资源导入超时阈值  
&emsp;&emsp;根据卷的大小，确定时间阈值，最大为24小时

&emsp; 9、导入数据   
&emsp;&emsp;通过rados，操作ceph，将本地持久化的镜像导入rbd块设备中。

&emsp; 10、配制网络  
&emsp;&emsp;10.1 按照备份时候的网卡配置创建新的网卡    
&emsp;&emsp;10.2 绑定新的网卡    
&emsp;&emsp;10.3 删除之前的网卡     

&emsp; 11、初始化虚拟机电源  
&emsp;&emsp;开机 

&emsp; 12、克隆完毕  
&emsp;&emsp;12.1 删除实例   
&emsp;&emsp;12.2 删除网络端口  
&emsp;&emsp;12.3 删除Flavor  
&emsp;&emsp;12.4 dischargeAgentPool()关闭相关资源。  



