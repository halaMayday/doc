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

  注：本地文档编写内容基于ceph作为zstack主储存。ceph版本为公有版本。后续zstack会提供他们专门支持ceph版本到我们。使用的都是基于ceph的rbd客户端，预计不会有很大的差异。

  **备份流程：**

  ![image-20210302175544686](C:\Users\EDZ\AppData\Roaming\Typora\typora-user-images\image-20210302175544686.png)

  

- **恢复迁移虚拟机**

- **克隆迁移虚拟机**

- **查询备份虚拟机进度**

- **克隆或者恢复迁移进度**

