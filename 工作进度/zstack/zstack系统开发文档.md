zstack系统开发文档

### 1.编写目的

指导对接新平台zstack的开发工作。

### 2.环境

​	本套方案编写基于 zstack的X86公有版本，SDK版本：sdk-3.10.0.jar 。

### 3.主要目标和内容

本系统需要实现的功能参照mlc-openstack-api(命令行版本).md。（http://dev1949.lab.dbfen.com/wjh/virtual-docs/-/blob/master/FAQ/mlc-openstack-api(%E5%91%BD%E4%BB%A4%E8%A1%8C%E7%89%88%E6%9C%AC).md）

根据实现的逻辑难易程度划分为**一般功能**和**核心功能**。

一般功能的实现较为简单，没有繁杂的流程，通过调用SDK几个简单的方法即可实现，所以在后续细节实现的章节中，如无特殊情况，对于一般功能的实现我会省略流程图等细节说明。

#### 2.1系统所要实现的功能 以及实现细节

**一般功能：**

- **验证平台账号密码**

  zstack平台登录方式支持：

  - ​	账户登录：需输入账户名和账户密码

  - ​	用户登录：需输入用户名和密码

  - ​	AD/LDAP登录：需输入登录属性名AD/LDAP密码。AD/LDAP需提前设置AD/LDAP服务器和AD/LDAP账号绑定。

  - ​	项目登录：需添加企业管理模块License，输入用户名和密码登录。

  

  考虑到

  - 用户功能将在zstack的未来版本中停止维护。

  - 木浪云平台接入新的云平台默认使用管理员权限

    

  **计划使用账户登录的方式获取认证的token。**

  实现方式：

  - ​	API请求：

  ​		URL：

  ​	

  ```
  GET zstack/v1/accounts/login?accountName=xxxx&password=xxxx
  ```

  ​	注：password需要SHA-512加密后的

  ​	实例返回值：

  ​	

  ```
  {
      "schema": {
          "inventory": "org.zstack.header.identity.SessionInventory"
      },
      "inventory": {
          "uuid": "eae8f664a88f4ab1a831345d59de4ae9",
          "accountUuid": "36c27e8ff05c4780bf6d2fa65700f22e",
          "userUuid": "36c27e8ff05c4780bf6d2fa65700f22e",
          "expiredDate": "Mar 2, 2021 6:32:47 PM",
          "createDate": "Mar 2, 2021 4:32:47 PM",
          "noSessionEvaluation": false
      }
  }
  ```

   	inventory.uuid 即为认证sessionId。其他除了登录以外的接口需要在 Headers里添加 

  ```
  Authorization: OAuth the-session-uuid
  ```

  ​	session的超时时间**默认为2个小时**，考虑到备份/恢复这些耗时较长的功能可能会超过2个小时，可以通过比较expiredDate和当前时间，如果当前时间大于等于expiredDate，进行reAuth。

  - ​	SDK调用

    ```
    LogInByAccountAction action = new LogInByAccountAction();
    action.accountName = accountName;
    action.password = encryptToSHA512(password);
    LogInByAccountAction.Result result = action.call();
    ```

    

- **拉取主机列表**

  ​	拉取主机列表的功能可以通过zstack平台物理机相关接口中的查询物理机（QueryHost）来获取我们所需的参数

  ​	实现方式：

  - API请求

    URL：

    ```
    GET zstack/v1/hosts
    ```

    Headers

    ```
    Authorization: OAuth the-session-uuid
    ```

  - SDK示例

    ```
    QueryHostAction action = new QueryHostAction();
    action.conditions = asList("uuid=fb0510cd5bf04565be44c43da20169c7");
    action.sessionId = "d69767ca64854cfb99960282fcdb8aed";
    QueryHostAction.Result res = action.call();
    ```

    

- **拉取虚拟机列表**

  ​	拉取虚拟机列表、拉取虚拟机详细信息列表这两个功能均可以通过云主机接口：查询云主机（QueryVmInstance）获取到我们所需的参数，然后进行参数的组装。

  ​	实现方式：

  - API请求：

    URL：

    ```
    GET zstack/v1/vm-instances
    ```

    Headers

    ```
    Authorization: OAuth the-session-uuid
    ```

  - SDK示例

    ```
    QueryVmInstanceAction action = new QueryVmInstanceAction();
    action.sessionId = sessionId;
    QueryVmInstanceAction.Result result = action.call();
    result.throwExceptionIfError();
    List<VmInstanceInventory> vmList = result.value.getInventories();
    ```

    

- **拉取虚拟机详细信息列表**

  见上 拉取虚拟机列表

- **检查虚拟机状态**

  可以通过云主机接口：查询云主机（QueryVmInstance）获取到主机状态state这个字段。

- **打开/关闭虚拟机**

  可以通过zstack云主机接口：启动云主机（StartVmInstace）和停止云主机（StopVmInstace）来实现启动和停止云主机的功能。

  **实现方式：**

  ​	API：

  ​		URL

  ```
  //启动云主机
  PUT zstack/v1/vm-instances/{uuid}/actions
  //停止云主机
  PUT zstack/v1/vm-instances/{uuid}/actions
  ```

  ​	Headers：

  ```
  Authorization: OAuth the-session-uuid
  ```

  ​	body：

  ```
  //启动云主机的body参数
  {
   "startVmInstance": {},
   "systemTags": [],
   "userTags": []
  }
  //停止云主机的body参数
  {
   "stopVmInstance": {
   "type": "grace"
   },
   "systemTags": [],
   "userTags": []
  }
  ```

  - SDK

    ```
    //启动云主机
    StartVmInstanceAction action = new StartVmInstanceAction();
    action.uuid = "22771459f2184f2f96c8da84b7e660d8";
    action.sessionId = "a6beddb0aa754b4b992d92dc13ff0c2c";
    StartVmInstanceAction.Result res = action.call();
    
    //停止云主机
    StopVmInstanceAction action = new StopVmInstanceAction();
    action.uuid = "b3edccc737664a24b6b8be3a249b87fb";
    action.type = "grace";
    action.sessionId = "7f36a5bff7ae49c185302dca91470133";
    StopVmInstanceAction.Result res = action.call();
    ```

**核心功能：**

- **备份虚拟机**

  注：本地文档编写内容基于ceph作为zstack主储存。ceph版本为开源版本。后续zstack会提供他们专门支持ceph商业版本到我们。使用都是基于ceph的rbd的客户端，预计不会有太大的差异。

  **备份流程：**

  ![image-20210302175544686](C:\Users\EDZ\AppData\Roaming\Typora\typora-user-images\image-20210302175544686.png)

  ![ZSTACK备份流程](/Users/nuc/Downloads/ZSTACK备份流程.jpg)


根据现有的zstack提供的开发文档，在灾备服务中**仅有数据库备份数据的导出接口，没有提供卷备份数据导出的接口**。所以目前来看zstack平台本身不支持数据导出（不排除他们商业版本的ceph会提供有关的接口。）

以下关于导出备份数据的实现都需要借助第三方工具，在宿主机上安装agent调用ceph客户端。

**导出差异位图**：

```
基本语法如下：
rbd diff --from-snap {fromSnap} snapshotSpecification
实例说明如下：
创建第一次快照：
rbd snap create volumes/volume-c18b9782-dc71-4ddc-bb7f-bc0037105ac3@v1
第一次全量备份，下面命令是导出了从开始创建image到快照v1那个时间点的差异数据导出来了fullBackupData_v1，导出成本地文件testimage_v1
rbd export-diff volumes/volume-c18b9782-dc71-4ddc-bb7f-bc0037105ac3@v1 fullBackupData_v1
第二次快照：
rbd snap create volumes/volume-c18b9782-dc71-4ddc-bb7f-bc0037105ac3@v2
第二次增量备份：(命令是导出了从v1快照时间点到v2快照时间点的差异数据)
rbd export-diff volumes/volume-c18b9782-dc71-4ddc-bb7f-bc0037105ac3@v2 --from-snap v1 incrBackupData_v2
```

虚拟化框架中RadosAccess.RadosAccess()已提供相关实现。

**根据差量位图导入数据**：

​	上面导出差量位图得到的结果是包含偏移量和长度的集合（Set<Pair<Long, Integer>> bitmap）。

通过java提供的RandomAccessFile可以实现将数据写入到指定文件中。

​	虚拟化框架中OsAccess.write(long pos, byte[] bytes, int len, String filePath)方法已实现随机读写。

```
/**
     * random access write
     */
    public void write(long pos, byte[] bytes, int len, String filePath)
        throws LocalWriteException {
        try (RandomAccessFile access = new RandomAccessFile(filePath, "rw")) {
            access.seek(pos);
            access.write(bytes, 0, len);
        } catch (IOException e) {
            throw new LocalWriteException(e.getMessage(), filePath);
        }
    }
```

**数据持久化**：

​	将备份所得数据从临时目录移动到持久化目录。

- **恢复迁移虚拟机**

  恢复的流程如下：

  ![zstack数据恢复功能](D:\googleDownload\zstack数据恢复功能.jpg)

  **恢复前准备工作**：准备工作包括创建临时目录、加载待恢复实例模型、挂载NFS等。

  考虑到恢复的时候存在主机当前的磁盘跟备份时间点的磁盘数量不匹配的情况。例如备份时有ABC三个磁盘，但是恢复的时候该主机存在ADE三个磁盘。故此采取了先卸载所有虚拟机硬盘，然后创建同等规格的磁盘，再挂载，最后倒入待恢复数据的方案。

  **导入数据**：

  ​	往磁盘中写入数据的实现采用rados中RbdImage.write(byte[] data, long offset, int length)即可。

  ​	虚拟化框架中已有实现，如下:

  ```
  private void rbdTransfer(String fromFile, RbdImage toImage, long position, int length)
          throws LocalReadException, RbdException {
          byte[] content = OsAccess.access.read(position, length, fromFile);
          toImage.write(content, position, length);
      }
  ```

- **克隆迁移虚拟机**

  ​	（未完成，待补充）

  - 不支持快速挂载。只有通过，创建新的虚拟机，创建新的空白磁盘，挂载，写入数据这样来实现。随着备份数据量的增大，功能耗时越来越长。
  - 支持快速挂载。是否可以将ceph备份的数据，当做NFS共享磁盘，直接挂载到新的虚拟机上启动（待测试验证）

- **查询备份虚拟机进度**

  采用统计io的方式，待补充

- **克隆或者恢复迁移进度**

  同上待补充

