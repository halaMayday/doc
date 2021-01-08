

**！！！主要以tstack-client 的api模块为参照进行整理**
auth
compute
monitor
network
role
storage

目前存在问题：
network部分，不太确定自己的找的API是否准确
storage，目前文档都是基于云硬盘的操作。


### auth
####  签名方法 v3
详见：
https://cloud.tencent.com/document/api/213/30654

1. 公共参数
公共参数是用于标识用户和接口签名的参数，如非必要，在每个接口单独的接口文档中不再对这些参数进行说明。
**但每次请求均需要携带这些参数，才能正常发起请求。**  

签名验证V3版本，使用的时候，公共参数需要统一放到HTTP Header请求头部中。

|参数名称 |类型     |必选 	  |描述     |
|:-----  |:-----  |:-----  |:-----  |
|Authorization|String  | 是 |签名方法+签名凭证+SignedHeaders：参与签名计算的头部信息，content-type 和 host 为必选头部+ Signature：签名摘要 |
| X-TC-Action | String | 是 |操作的接口名称，例如 DescribeInstances。|
| X-TC-Region | String | 是 |地域参数，有些接口不需要该参数，会有特殊说明，填了也不会生效。|
|X-TCTimestamp|Integer | 是 |当前unix时间戳。如果与服务器时间超过5分支，会引起签名过期错误。|


---------------------------------------------------------
eg:

Authorization: TC3-HMAC-SHA256 Credential=AKIDz8krbsJ5yKBZQpn74WFkmLPx3EXAMPLE/2018-10-09/c
vm/tc3_request, SignedHeaders=content-type;host, Signature=5da7a33f6993f0614b047e5df4582db9
e9bf4672ba50567dba16c6ccf174c474


---------------------------------------------------------------------------------------------------------------------------------------------------------------------------


### 2.Compute  对应文档CVM_租户端API文档_V3.6.0_zh
#### 2.1查看实例列表DescribeInstances
##### 输入参数：
|参数名称 | 必选 |   类型 | 描述|
|---|--- | ---|---|
|Action  | 是   | String | 接口：DescribeInstances|
|Version | 是   | String | 版本号：2017-03-12|
|Region  |是    | String |  当公共参数 Region| 为金融区地域（例如 ap-shanghai-fsi）时，需要同时指定带金融区地域的域名，最好和 Region 的地域保持一致，例如：cvm.ap-shanghai-fsi.tencentcloudapi.com。|
|InstanceIds.N|否|Aray of String|可以传如一个实例ID的数组，最大上限为100个|
|Filters.N|否|Arry of Filter| 一组筛选条件。每次请求的Filters的上限为10，Filter.Values的上限为5。==参数不支持同时指定InstanceIds和Filters==。具体可选Filter见下面。|
|Offset|否|Integer|偏移量，默认为0|
|Limit|否|Integer|返回数量，默认为20，最大为100|


---
##### 可供选择的Filters
|名称 | 类型|是否必选 | 解释|
|---|---|---|---|
|zone | String|否 | 可用区域|
|project-id | Integer|否 | 项目ID，控制台或者DescribeProject可以查看|
|host-id|String|否|按照CDH ID进行过滤，例如host-xxxx|
|vpc-id|String|否|按照VPC ID 进行过滤。例如vpc-xxxx|
|subnet-id|String|否|按照子网ID进行过滤|
|instance-id|String|否|按照实例id进行过滤|
|uuid|String|否|按照实例uuid进行过滤|
|security-group-id|String|否|按照安全组id进行过滤|
|instance-name|String|否|实例名称|
|instance-charge-type|String|否|实例计费模式|
|instance-state|String|否|实例状态|
|private-ip-address|String|否|按照【实例主网卡的内网IP】进行过滤。|
|public-ip-address|String|否|按照【实例主网卡的公网IP】进行过滤，包含实例创建时自动分配的IP和实例创建后手动绑定的弹性IP。|
|tag-key|String|否|标签键|
|tag-value|String|否|标签值|
|tag-value|String|否|标签值|
##### |tag:tag-key|String|否|按照【标签键值对】|
---
##### 实例状态instance-state实例状态。取值范围：
- PENDING：表示创建中
- LAUNCH_FAILED：表示创建失败
- RUNNING：表示运行中
- STOPPED：表示关机
- STARTING：表示开机中
- STOPPING：表示关机中
- REBOOTING：表示重启中
- SHUTDOWN：表示停止待销毁
- TERMINATING：表示销毁中。

##### 返回参数
|参数名称	|类型	|描述|
|---|---|---|
|TotalCount|Integer|	符合条件的实例数量。|
|InstanceSet	|Array of Instance	实例详细信息列表。|
|RequestId	|String|	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|

---
##### eg
接口请求域名： cvm.api3.yf-1.tcepoc.fsphere.cn

curl命令：

```
curl -X POST https://cvm.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-13/cvm/tc3_request, SignedHeaders=content-type;host, Signature=960c0babe7a50051c0941cdfc5f7bff75c95c17b1ee5c27a892a05ae4e029df3" -H "Content-Type: application/json" -H "Host: cvm.tencentcloudapi.com" -H "X-TC-Action: DescribeInstances" -H "X-TC-Timestamp: 1605238493" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'
```


SDK：
```
com.tencentcloudapi.cvm.v20170312.CvmClient.DescribeInstances()
```
---
response:

```
{
    "Response": {
        "TotalCount": 1,
        "InstanceSet": [
            {
                "Placement": {
                    "Zone": "ap-guangzhou-3",
                    "HostId": null,
                    "ProjectId": 0
                },
                "InstanceId": "ins-cqay0nos",
                "Uuid": "2fe05ad9-e81f-423f-a5e8-b69aace11728",
                "InstanceState": "RUNNING",
                "RestrictState": "NORMAL",
                "InstanceType": "S2.MEDIUM4",
                "CPU": 2,
                "Memory": 4,
                "InstanceName": "halaMayday",
                "InstanceChargeType": "PREPAID",
                "SystemDisk": {
                    "DiskType": "CLOUD_PREMIUM",
                    "DiskId": "disk-hig9cxds",
                    "DiskSize": 50
                },
                "DataDisks": null,
                "PrivateIpAddresses": [
                    "172.16.0.17"
                ],
                "PublicIpAddresses": [
                    "106.52.33.183"
                ],
                "IPv6Addresses": null,
                "InternetAccessible": {
                    "InternetMaxBandwidthOut": 3,
                    "InternetChargeType": "BANDWIDTH_PREPAID"
                },
                "VirtualPrivateCloud": {
                    "VpcId": "vpc-0ukik5wh",
                    "SubnetId": "subnet-0pzs7mj8",
                    "AsVpcGateway": false
                },
                "SecurityGroupIds": [],
                "LoginSettings": {
                    "KeyIds": [
                        "skey-btgwzy1d"
                    ]
                },
                "ImageId": "img-oikl1tzv",
                "OsName": "CentOS 7.5 64bit",
                "RenewFlag": "NOTIFY_AND_MANUAL_RENEW",
                "CreatedTime": "2019-12-01T13:16:14Z",
                "ExpiredTime": "2020-12-01T13:16:14Z",
                "Tags": [],
                "DisasterRecoverGroupId": "",
                "CamRoleName": "",
                "LatestOperation": "ResetInstancesPassword",
                "LatestOperationState": "SUCCESS",
                "LatestOperationRequestId": "5f75ade5-67b9-4dc4-9487-832f215ca523",
                "IsolatedSource": "NOTISOLATED",
                "HpcClusterId": "",
                "RdmaIpAddresses": null,
                "StopChargingMode": "NOT_APPLICABLE"
            }
        ],
        "RequestId": "578aa91b-5ae9-4c17-8021-607b8a2ce372"
    }
}
```

---
#### 2.2 查看实例状态 DescribeInstancesStatus接口
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|||||
|Action	|是	|String	|公共参数，本接口取值：DescribeInstancesStatus。|
|Version|	是	|String	|公共参数，本接口取值：2017-03-12。|
|Region	|是	|String|	公共参数，详见产品支持的 地域列表。|
|InstanceIds.N	|否|	ArrayofString|按照一个或者多个实例ID查询。实例ID形如：ins-11112222。此参数的具体格式可参考API简介的ids.N一节）。每次请求的实例的上限为100。|
|Offset	|否|	Integer	|偏移量，默认为0。关于Offset的更进一步介绍请参考 API 简介中的相关小节。|
|Limit	|否|	Integer	|返回数量，默认为20，最大值为100。关于Limit的更进一步介绍请参考 API| 简介中的相关小节。
##### 输出参数

|参数名称|类型|解释|
|---|---|---|
||||
|TotalCount	|Integer|	符合条件的实例状态数量。|
|InstanceStatusSet	|Array of InstanceStatus|	实例状态 列表。|
|RequestId	|String|	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|



##### eg
crul命令：

```
curl -X POST https://cvm.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-13/cvm/tc3_request, SignedHeaders=content-type;host, Signature=a8af9da6c7f131243929af1f701ddb77c4e23457d9a1787606232b36a2db436c" -H "Content-Type: application/json" -H "Host: cvm.tencentcloudapi.com" -H "X-TC-Action: DescribeInstancesStatus" -H "X-TC-Timestamp: 1605247457" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'
```
SDK：

```
com.tencentcloudapi.cvm.v20170312.CvmClient.DescribeInstancesStatus()
```
返回：

```
{
  "Response": {
    "TotalCount": 1,
    "InstanceStatusSet": [
      {
        "InstanceId": "ins-cqay0nos",
        "InstanceState": "RUNNING"
      }
    ],
    "RequestId": "64924cec-20aa-49d9-a9c2-e777c7cb540f"
  }
}
```

---

#### 2.3创建实例 RunInstance接口

##### 入参

|参数名称|必选|类型|解释|
|-----|----- |-----|-----|
|Action| 是| String |公共参数，本接口取值：RunInstances|
|Version| 是| String |公共参数，本接口取值：2017-03-12|
|Region |是 |String |公共参数，详见产品支持的地域列表。|
Placement |是 |Placement|实例所在的位置。通过该参数可以指定实例所属可用区，所属项目，专用宿主机（对于独享母机付费模式的子机创建）等属性。|
|ImageId| 是| String|指定有效的镜像ID，格式形如 img-xxx 。镜像类型分为四种：公共镜像、自定义镜像、共享镜像、服务市场镜像可通过以下方式获取可用的镜像ID：公共镜像 、 自定义镜像 、 共享镜像 的镜像ID可通过登录控制台查询； 服务镜像市场 的镜像ID可通过云市场查询。通过调用接口 DescribeImages ，取返回信息中的 ImageId 字段。|
|InstanceChargeType|否|String|实例计费类型。PREPAID：预付费，即包年包月，POSTPAID_BY_HOUR：按小时后付费，CDHPAID：独享母机付费（基于专用宿主机创建，宿主机部分的资源不收费）默认值：POSTPAID_BY_HOUR。|
|InstanceChargePrepaid |否| InstanceChargePrepaid|预付费模式，即包年包月相关参数设置。通过该参数可以指定包年包月实例的购买时长、是否设置自动续费等属性。若指定实例的付费模式为预付费则该参数必传。|
|InstanceType| 否| String|实例机型。不同实例机型指定了不同的资源规格。对于付费模式为PREPAID或POSTPAID_BY_HOUR的子机创建，具体取值可通过调用接口DescribeInstanceTypeConfigs来获得最新的规格表或参见实例类型描述。若不指定该参数，则默认机型为S1.SMALL1。对于付费模式为CDHPAID的子机创建，该参数以"CDH_"为前缀，根据cpu和内存配置生成，具体形式为：CDH_XCXG，例如对于创建cpu为1核，内存为1G大小的专用宿主机的子机，该参数应该为CDH_1C1G。|
|SystemDisk |否| SystemDisk| 实例系统盘配置信息。若不指定该参数，则按照系统默认值进行分配。|
|DataDisks |否| Array of DataDisk|实例数据盘配置信息。若不指定该参数，则默认不购买数据盘，当前仅支持购买的时候指定一个数据盘。|
|VirtualPrivateCloud| 否| VirtualPrivateCloud|私有网络相关信息配置。通过该参数可以指定私有网络的ID，子网ID等信息。若不指定该参数，则默认使用基础网络。若在此参数中指定了私有网络ip，那么InstanceCount参数只能为1。


---
==更多的非必选入参不做过多介绍，见 文档 CVM_租户端API文档_V3.6.0_zh.pdf==

---
##### 输出参数
|参数名称|类型|解释|
|---|---|---|
|InstanceIdSet|Array of String|当通过本接口来创建实例时会返回该参数，表示一个或多个实例 ID 。返回实例 ID 列表并不代表实例创建成功，可根据 DescribeInstancesStatus 接口查询返回的InstancesSet中对应实例的 ID 的状态来判断创建是否完成；如果实例状态由“准备中”变为“正在运行”，则为创建成功。|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|

##### eg
todo：

curl命令：
需要替换对应的参数 
```
curl -X POST https://cvm.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-13/cvm/tc3_request, SignedHeaders=content-type;host, Signature=c1efa83e70aafb5e2ab29b0361b83aed1d8bff27e765288c237b472a24cde00d" -H "Content-Type: application/json" -H "Host: cvm.tencentcloudapi.com" -H "X-TC-Action: RunInstances" -H "X-TC-Timestamp: 1605247179" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{"Placement":{"HostIds":["22222"],"HostIps":["11111"],"Zone":"ZoneID","ProjectId":11111,"HostId":"11111"},"ImageId":"11111"}'
```

SDK：RunInstances()

```
com.tencentcloudapi.cvm.v20170312.CvmClient.RunInstances()
```

返回结果：

```
//todo
```
---
#### 2.4 删除实例 TerminateInstances
公有云中只有退还实例 TerminateInstances接口。 与删除
##### 入参
|参数名称|必选|类型|解释|
|---|---|---|---|
|Action	|是	|String|	公共参数，本接口取值：TerminateInstances。|
|Version	|是|	String	公共参数，本接口取值：2017-03-12。|
|Region	|是	|String	公共参数，详见产品支持的 地域列表。|
|InstanceIds.N	|是|	ArrayofString|一个或多个待操作的实例ID。可通过DescribeInstances接口返回值中的InstanceId获取。每次请求批量实例的上限为100。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId|	String	|唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId|
##### eg
crul命令：

```
curl -X POST https://cvm.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=/2020-11-13/cvm/tc3_request, SignedHeaders=content-type;host, Signature=33ff543d7991eca7a9f2729abfae7cdf4e773b9aa61068201a982d921a6aa0cc" -H "Content-Type: application/json" -H "Host: cvm.tencentcloudapi.com" -H "X-TC-Action: TerminateInstances" -H "X-TC-Timestamp: 1605250375" -H "X-TC-Version: 2017-03-12" -H "X-TC-Language: zh-CN" -d '{"InstanceIds":["xxxxx"]}'

```
SDK：

```
com.tencentcloudapi.cvm.v20170312.CvmClient.TerminateInstances()
```
返回：


```
//todo
```

---
#### 2.5 关闭实例 StopInstances接口
本接口 (StopInstances) 用于关闭一个或多个实例。

只有状态为RUNNING的实例才可以进行此操作。
接口调用成功时，实例会进入STOPPING状态；关闭实例成功时，实例会进入STOPPED状态。
支持强制关闭。强制关机的效果等同于关闭物理计算机的电源开关。强制关机可能会导致数据丢失或文件系统损坏，请仅在服务器不能正常关机时使用。
支持批量操作。每次请求批量实例的上限为100。
本接口为异步接口，关闭实例请求发送成功后会返回一个RequestId，此时操作并未立即完成。实例操作结果可以通过调用 DescribeInstances 接口查询，如果实例的最新操作状态(LatestOperationState)为“SUCCESS”，则代表关闭实例操作成功。
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| |是| String| 公共参数，本接口取值：RebootInstances|
|Version |是| String| 公共参数，本接口取值：2017-03-12|
|Region |是 |String| 公共参数，详见产品支持的地域列表。|
InstanceIds |是|Array of String|一个或多个待操作的实例ID。可通过 DescribeInstances 接口返回值中的 InstanceId 获取。每次请求批量实例的上限为100。|
|ForceStop| 否 |Boolean|是否在正常关闭失败后选择强制关闭实例。取值范围：TRUE：表示在正常关闭失败后进行强制关闭FALSE：表示在正常关闭失败后不进行强制关闭默认取值：FALSE。。|

##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
curl -X POST https://cvm.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-13/cvm/tc3_request, SignedHeaders=content-type;host, Signature=bee50ffeffcd3e70c3b53e9c698de709f69ce90fbb214b5381708152ee2f47f8" -H "Content-Type: application/json" -H "Host: cvm.tencentcloudapi.com" -H "X-TC-Action: StopInstances" -H "X-TC-Timestamp: 1605252291" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{"InstanceIds":["ins-cqay0nos1111"]}'

```
SDK：

```
com.tencentcloudapi.cvm.v20170312.CvmClient.StopInstances()
```
返回：

```
//todo
```
---
#### 2.6 重启实例
本接口 (RebootInstances) 用于重启实例。
只有状态为 RUNNING 的实例才可以进行此操作。
接口调用成功时，实例会进入 REBOOTING 状态；重启实例成功时，实例会进入 RUNNING 状态。
支持强制重启。强制重启的效果等同于关闭物理计算机的电源开关再重新启动。强制重启可能会导致数据丢失或文件系统损
坏，请仅在服务器不能正常重启时使用。
支持批量操作，每次请求批量实例的上限为100。

##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| |是| String| 公共参数，本接口取值：RebootInstances|
|Version |是| String| 公共参数，本接口取值：2017-03-12|
|Region |是 |String| 公共参数，详见产品支持的地域列表。|
InstanceIds |是|Array of String|一个或多个待操作的实例ID。可通过 DescribeInstances 接口返回值中的 InstanceId 获取。每次请求批量实例的上限为100。|
|ForceReboot| 否 |Boolean|是否在正常重启失败后选择强制重启实例。取值范围：TRUE：表示在正常重启失败后进行强制重启FALSE：表示在正常重启失败后不进行强制重启默认取值：FALSE。|

##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
curl -X POST https://cvm.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-13/cvm/tc3_request, SignedHeaders=content-type;host, Signature=e62d731f6c2ad593f474176c44e32128b4f39bb147e3902cf72e45b41ee9d18c" -H "Content-Type: application/json" -H "Host: cvm.tencentcloudapi.com" -H "X-TC-Action: RebootInstances" -H "X-TC-Timestamp: 1605252184" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{"InstanceIds":["ins-cqay0nos"]}'
```
SDK：

```
com.tencentcloudapi.cvm.v20170312.CvmClient.RebootInstances()
```
返回：

```
{
  "Response": {
    "RequestId": "463e37bf-52fa-4c5a-a39e-bc157698187b"
  }
}
```   
---
#### 2.7 启动实例StartInstances 接口
本接口 (StartInstances) 用于启动一个或多个实例。
只有状态为 STOPPED 的实例才可以进行此操作。
接口调用成功时，实例会进入 STARTING 状态；启动实例成功时，实例会进入 RUNNING 状态。
支持批量操作。每次请求批量实例的上限为100。
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：StartInstances|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|InstanceIds| 是|Array of String|一个或多个待操作的实例ID。可通过 DescribeInstances 接口返回值中的InstanceId 获取。每次请求批量实例的上限为100。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|

##### eg
crul命令：
```
curl -X POST https://cvm.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-13/cvm/tc3_request, SignedHeaders=content-type;host, Signature=5fe4efe96574d8637385df97f5d33ca23b1910451c0bbfbacc2e448d18cd81e7" -H "Content-Type: application/json" -H "Host: cvm.tencentcloudapi.com" -H "X-TC-Action: StartInstances" -H "X-TC-Timestamp: 1605251633" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{"InstanceIds":["11111"]}'
```
SDK：

```
com.tencentcloudapi.cvm.v20170312.CvmClient.StartInstances()
```
返回：

```
//todo
```   

---
#### 2.7 主机模板  列表 todo
##### 入参
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：StartInstances|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
//todo
```
SDK：

```
//todo
```
返回：

```
//todo
```   

---
#### 2.8 创建主机模板   todo
##### 入参
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：StartInstances|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
//todo
```
SDK：

```
//todo
```
返回：

```
//todo
```   
---
#### 2.8 删除主机模板   todo
##### 入参
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：StartInstances|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
//todo
```
SDK：

```
//todo
```
返回：

```
//todo
```   

---

### 3.Monitor  对应文档CVM_租户端API文档_V3.6.0_zh
####　３.１查看物理机信息。
上同　2.1查看实例列表DescribeInstances　　nstanceIds.N　填入具体的实例ｉｄ即可。
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|--|---|
||||
##### eg
crul命令：

```
curl -X POST https://cvm.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-13/cvm/tc3_request, SignedHeaders=content-type;host, Signature=a04dbb7511ce600ebd528aa9961ccd755e86ddf28c9747f60d1e7a4679d4fc33" -H "Content-Type: application/json" -H "Host: cvm.tencentcloudapi.com" -H "X-TC-Action: DescribeInstances" -H "X-TC-Timestamp: 1605254498" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'

```
SDK：

```
com.tencentcloudapi.cvm.v20170312.CvmClient.DescribeInstances()
```
返回：

```
{
  "Response": {
    "TotalCount": 1,
    "InstanceSet": [
      {
        "Placement": {
          "Zone": "ap-guangzhou-3",
          "HostId": null,
          "ProjectId": 0
        },
        "InstanceId": "ins-cqay0nos",
        "Uuid": "2fe05ad9-e81f-423f-a5e8-b69aace11728",
        "InstanceState": "RUNNING",
        "RestrictState": "NORMAL",
        "InstanceType": "S2.MEDIUM4",
        "CPU": 2,
        "Memory": 4,
        "InstanceName": "halaMayday",
        "InstanceChargeType": "PREPAID",
        "SystemDisk": {
          "DiskType": "CLOUD_PREMIUM",
          "DiskId": "disk-hig9cxds",
          "DiskSize": 50
        },
        "DataDisks": null,
        "PrivateIpAddresses": [
          "172.16.0.17"
        ],
        "PublicIpAddresses": [
          "106.52.33.183"
        ],
        "IPv6Addresses": null,
        "InternetAccessible": {
          "InternetMaxBandwidthOut": 3,
          "InternetChargeType": "BANDWIDTH_PREPAID"
        },
        "VirtualPrivateCloud": {
          "VpcId": "vpc-0ukik5wh",
          "SubnetId": "subnet-0pzs7mj8",
          "AsVpcGateway": false
        },
        "SecurityGroupIds": [],
        "LoginSettings": {
          "KeyIds": [
            "skey-btgwzy1d"
          ]
        },
        "ImageId": "img-oikl1tzv",
        "OsName": "CentOS 7.5 64位",
        "RenewFlag": "NOTIFY_AND_MANUAL_RENEW",
        "CreatedTime": "2019-12-01T13:16:14Z",
        "ExpiredTime": "2020-12-01T13:16:14Z",
        "Tags": [],
        "DisasterRecoverGroupId": "",
        "CamRoleName": "",
        "LatestOperation": "RebootInstances",
        "LatestOperationState": "SUCCESS",
        "LatestOperationRequestId": "463e37bf-52fa-4c5a-a39e-bc157698187b",
        "IsolatedSource": "NOTISOLATED",
        "HpcClusterId": "",
        "RdmaIpAddresses": null,
        "StopChargingMode": "NOT_APPLICABLE"
      }
    ],
    "RequestId": "517cef8a-7904-4fe6-9f75-2c03654146d2"
  }
}
```
---

### ４.Network  对应文档VPC_租户端API文档_V3.6.0_zh
####　４.１查看弹性网卡列表　DescribeNetworkInterfaces
本接口（DescribeNetworkInterfaces）用于查询弹性网卡信息。 接口请求域名： vpc.api.qcloud.com
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|vpcId| 否| String|弹性网卡对应的私有网络ID，例如：vpc-7t9nf3pu|
|networkInterfaceId|否|String|系统分配的弹性网卡ID，例如：eni-m6dyj72l|
|eniName| 否| String| 弹性网卡名称，支持模糊搜索|
|eniDescription| 否| String| 弹性网卡描述，支持模糊搜索|
|instanceId| 否|String|云服务器实例ID，例如：ins-xx44545f|
|offset| 否| Int| 初始行的偏移量，默认为0|
|limit| 否| Int| 每页行数，默认为20，最大支持50|
|orderField |否|String|按某个字段排序。支持字段：eniName,createTime，默认按createTime|
|orderDirection|否|String|升序（asc）或降序（desc），默认：desc|

##### 输出参数
|参数名称|类型|解释|
|---|---|---|
|NetworkInterfaceSet|ArrayofNetworkInterface|实例详细信息列表。|
|TotalCount|	Integer|	符合条件的实例数量。|
|RequestId|	String|唯一请求ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|

---
NetworkInterface 数据结构
https://cloud.tencent.com/document/api/215/15824#NetworkInterface

##### eg
crul命令：

```
curl -X POST https://vpc.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-13/vpc/tc3_request, SignedHeaders=content-type;host, Signature=1039d2b701529939805578762786cd701611e6f94b089c25491a7b14110478f6" -H "Content-Type: application/json" -H "Host: vpc.tencentcloudapi.com" -H "X-TC-Action: DescribeNetworkInterfaces" -H "X-TC-Timestamp: 1605260843" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'
```
SDK：

```
com.tencentcloudapi.vpc.v20170312.VpcClient.DescribeNetworkInterfaces()
```
返回：

```
{
  "Response": {
    "NetworkInterfaceSet": [
      {
        "VpcId": "vpc-0ukik5wh",
        "SubnetId": "subnet-0pzs7mj8",
        "NetworkInterfaceId": "eni-ohslw7d1",
        "NetworkInterfaceName": "ins-cqay0nos Primary ENI",
        "NetworkInterfaceDescription": "",
        "GroupSet": [],
        "Primary": true,
        "MacAddress": "52:54:00:6D:BE:5A",
        "State": "AVAILABLE",
        "CreatedTime": "2019-12-01 21:16:11",
        "PrivateIpAddressSet": [
          {
            "Description": "",
            "Primary": true,
            "PrivateIpAddress": "172.16.0.17",
            "AddressId": "",
            "PublicIpAddress": "106.52.33.183",
            "IsWanIpBlocked": false,
            "State": "AVAILABLE"
          }
        ],
        "Attachment": {
          "InstanceId": "ins-cqay0nos",
          "DeviceIndex": 0,
          "AttachTime": "2019-12-01 21:16:11"
        },
        "EniType": 0,
        "Zone": "ap-guangzhou-3",
        "Ipv6AddressSet": [],
        "InstanceAccountId": "1257215961",
        "TagSet": []
      }
    ],
    "TotalCount": 1,
    "RequestId": "457ec282-28b0-4561-a95e-25753a7b04af"
  }
}
```

---
#### 4.2 创建弹性网卡

本接口(CreateNetworkInterface)用于创建弹性网卡。 接口请求域名：vpc.api.qcloud.com
1) 创建弹性网卡时可以指定内网IP，并且可以指定一个主IP，指定的内网IP必须在弹性网卡所在子网内，而且不能被占用。 2) 创建弹性网卡时可以指定
需要申请的内网IP数量，系统会随机生成内网IP地址。 3) 创建弹性网卡同时可以绑定已有安全组。
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：CreateNetworkInterface|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|VpcId|	是	|String|	VPC实例ID。可通过DescribeVpcs接口返回值中的VpcId获取。|
|NetworkInterfaceName|	是|	String|	弹性网卡名称，最大长度不能超过60个字节。|
|SubnetId|	是|	String|	弹性网卡所在的子网实例ID，例如：subnet-0ap8nwca。|
|NetworkInterfaceDescription|	否|	String|	弹性网卡描述，可任意命名，但不得超过60个字符。|
|SecondaryPrivateIpAddressCount|	否|	Integer|新申请的内网IP地址个数，内网IP地址个数总和不能超过配数。|
|SecurityGroupIds.N|	否|	Array of String|	指定绑定的安全组，例如：['sg-1dd51d']。|
|PrivateIpAddresses.N|	否|	Array of PrivateIpAddressSpecification|	指定的内网IP信息，单次最多指定10个。|
|Tags.N|	否|	Array of Tag|	指定绑定的标签列表，例如：[{"Key": "city", "Value": "shanghai"}]|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|code| Int| 错误码。0: 成功, 其他值: 失败|
|message| String |错误信息|
|taskId| Int| 任务ID，操作结果可以用taskId查询，详见查询任务执行结果接口。|
##### eg
crul命令：

```
curl -X POST https://vpc.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/vpc/tc3_request, SignedHeaders=content-type;host, Signature=2b81ee994bafd8d0a0142fd1e857a4669e959efc6fcdaa4fda7975d788956dc1" -H "Content-Type: application/json" -H "Host: vpc.tencentcloudapi.com" -H "X-TC-Action: CreateNetworkInterface" -H "X-TC-Timestamp: 1605453476" -H "X-TC-Version: 2017-03-12" -H "X-TC-Language: zh-CN" -d '{"VpcId":"your vpcId","NetworkInterfaceName":"your NetworkInterfaceName","SubnetId":"your SubnetId"}'


```
SDK：

```
com.tencentcloudapi.vpc.v20170312.VpcClient.CreateNetworkInterface()
```
返回：

```
//todo
```

---
#### 4.3 修改弹性网卡 ModifyNetworkInterfaceAttribute 接口
本接口（ModifyNetworkInterfaceAttribute）用于修改弹性网卡属性。
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：ModifyNetworkInterfaceAttribute|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|NetworkInterfaceId|	是|	String|	弹性网卡实例ID，例如：eni-pxir56ns。|
|NetworkInterfaceName|	否|	String|	弹性网卡名称，最大长度不能超过60个字节。|
|NetworkInterfaceDescription|	否|	String|	弹性网卡描述，可任意命名，但不得超过60个字符。|
|SecurityGroupIds.N|	否|	Array of String|	指定绑定的安全组，例如:['sg-1dd51d']。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
curl -X POST https://vpc.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/vpc/tc3_request, SignedHeaders=content-type;host, Signature=757c779408ca85d57eb87626c6035c338836648e12217c520af3ab28131ca5e5" -H "Content-Type: application/json" -H "Host: vpc.tencentcloudapi.com" -H "X-TC-Action: ModifyNetworkInterfaceAttribute" -H "X-TC-Timestamp: 1605453611" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{"NetworkInterfaceId":"your NetworkInterfaceId"}'

```
SDK：

```
com.tencentcloudapi.vpc.v20170312.VpcClient.ModifyNetworkInterfaceAttribute()
```
返回：

```
//todo
```

---
#### 4.4 删除弹性网卡 DeleteNetworkInterface

接口请求域名： vpc.tencentcloudapi.com 。
本接口（DeleteNetworkInterface）用于删除弹性网卡。
弹性网卡上绑定了云服务器时，不能被删除。
删除指定弹性网卡，弹性网卡必须先和子机解绑才能删除。删除之后弹性网卡上所有内网IP都将被退还 
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DeleteNetworkInterface|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|NetworkInterfaceId|	是|	String|	弹性网卡实例ID，例如：eni-m6dyj72l。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
curl -X POST https://vpc.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/vpc/tc3_request, SignedHeaders=content-type;host, Signature=9373ef2c571c68ab56c89aa69abe70b709f5bf3cd835472407773d62d6547c6c" -H "Content-Type: application/json" -H "Host: vpc.tencentcloudapi.com" -H "X-TC-Action: DeleteNetworkInterface" -H "X-TC-Timestamp: 1605454074" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'

```
SDK：

```
com.tencentcloudapi.vpc.v20170312.VpcClient.DeleteNetworkInterface()
```
返回：

```
//todo
```

---
#### 4.5 弹性网卡绑定云服务器

接口请求域名： vpc.tencentcloudapi.com 。
本接口（AttachNetworkInterface）用于弹性网卡绑定云服务器。
一个云服务器可以绑定多个弹性网卡，但只能绑定一个主网卡。更多限制信息详见弹性网卡使用限制。
一个弹性网卡只能同时绑定一个云服务器。
只有运行中或者已关机状态的云服务器才能绑定弹性网卡，查看云服务器状态详见腾讯云服务器信息。
弹性网卡绑定的云服务器必须是私有网络的，而且云服务器所在可用区必须和弹性网卡子网的可用区相同。

##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：AttachNetworkInterface|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|NetworkInterfaceId|	是|	String|	弹性网卡实例ID，例如：eni-m6dyj72l。|
|InstanceId|	是	|String|	CVM实例ID。形如：ins-r8hr2upy。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。

##### eg
crul命令：

```
curl -X POST https://vpc.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/vpc/tc3_request, SignedHeaders=content-type;host, Signature=95eda1d9ccfd60e1206744f7d6ee169ddc7e21b9b6006edc88de95b994487692" -H "Content-Type: application/json" -H "Host: vpc.tencentcloudapi.com" -H "X-TC-Action: AttachNetworkInterface" -H "X-TC-Timestamp: 1605454130" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{"InstanceId":"your InstanceId"}'

```
SDK：

```
com.tencentcloudapi.vpc.v20170312.VpcClient.AttachNetworkInterface()
```
返回：

```
//todo
```

---
#### 4.6 弹性网卡解绑云服务器 DetachNetworkInterface
接口请求域名： vpc.tencentcloudapi.com 。
本接口（DetachNetworkInterface）用于弹性网卡解绑云服务器
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DetachNetworkInterface|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|NetworkInterfaceId|	是|	String|	弹性网卡实例ID，例如：eni-m6dyj72l。|
|InstanceId|	是	|String|	CVM实例ID。形如：ins-r8hr2upy。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
##### eg
crul命令：

```
curl -X POST https://vpc.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/vpc/tc3_request, SignedHeaders=content-type;host, Signature=7b20fd038f23748fb307bb43976b4ecfa5007079fc40d782a03442cc92ec3f69" -H "Content-Type: application/json" -H "Host: vpc.tencentcloudapi.com" -H "X-TC-Action: DetachNetworkInterface" -H "X-TC-Timestamp: 1605454996" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'

```
SDK：

```
com.tencentcloudapi.vpc.v20170312.VpcClient.DetachNetworkInterface()
```
返回：

```
//todo
```

---

### 5.RoleImpl  对应文档 在归纳中未找到
腾讯TCE中没有比较明确关于 Projects的概念。

本接口（DescribeProjects）用于查询项目列表
#### 5.1   查询项目列表 DescribeProjects
接口请求域名： dcdb.tencentcloudapi.com 。
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DescribeProjects|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|


###### 项目Project
|参数名称|类型|解释|
|---|---|---|
|ProjectId|	Integer|	项目ID|
|OwnerUin|	Integer|	资源拥有者（主账号）uin|
|AppId|	Integer|	应用Id|
|Name|	String|	项目名称|
|CreatorUin|	Integer|	创建者uin|
|SrcPlat|	String|	来源平台|
|SrcAppId|	Integer|	来源AppId|
|Status|	Integer|	项目状态,0正常，-1关闭。默认项目为3|
|CreateTime|	Timestamp|	创建时间|
|IsDefault|	Integer|	是否默认项目，1 是，0 不是|
|Info|	String|	描述信息|

##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|Projects|	Array of Project|	项目列表|
|RequestId	|String|	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|


##### eg
crul命令：

```
curl -X POST https://dcdb.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/dcdb/tc3_request, SignedHeaders=content-type;host, Signature=2c2d990e503d98a149bc1886b9ddcf650a7e80fc51670aedc1fc3683210e3228" -H "Content-Type: application/json" -H "Host: dcdb.tencentcloudapi.com" -H "X-TC-Action: DescribeProjects" -H "X-TC-Timestamp: 1605455175" -H "X-TC-Version: 2018-04-11" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'

```
SDK：

```
接口请求域名： dcdb.tencentcloudapi.com.DcdbClient.DescribeProjects()
```
返回：

```
{
  "Response": {
    "Projects": [
      {
        "AppId": 0,
        "CreateTime": "0000-00-00 00:00:00",
        "CreatorUin": 0,
        "Info": "默认项目",
        "IsDefault": 1,
        "Name": "默认项目",
        "OwnerUin": 0,
        "ProjectId": 0,
        "SrcAppId": 0,
        "SrcPlat": "qcloud",
        "Status": 3
      }
    ],
    "RequestId": "1b2234f8-ca5a-480e-8906-439df10d4f22"
  }
}
```

### 6.StorageImpl  对应文档 CBS租户文档 
**此部分都是对云盘的操作，没有本地磁盘的**

####  6.1   查看快照列表 DescribeSnapshots
接口请求域名： cbs.api3.yf-1.tcepoc.fsphere.cn 。
本接口（DescribeSnapshots）用于查询快照的详细信息。
根据快照ID、创建快照的云硬盘ID、创建快照的云硬盘类型等对结果进行过滤，不同条件之间为与(AND)的关系，过滤信息详
细请见过滤器 Filter 。
如果参数为空，返回当前用户一定数量（ Limit 所指定的数量，默认为20）的快照列表。
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DescribeSnapshots|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|SnapshotIds| 否|Array of String|要查询快照的ID列表。参数不支持同时指定 SnapshotIds 和 Filters 。|
|Filters| 否|Array of Filter| 过滤条件。参数不支持同时指定 SnapshotIds 和 Filters |
|Offset| 否| Integer| 偏移量，默认为0。关于 Offset 的更进一步介绍请参考API简介中的相关小节。|
|Limit| 否| Integer|返回数量，默认为20，最大值为100。关于 Limit 的更进一步介绍请参考 API 简介中
的相关小节。|
|Order| 否| String|输出云盘列表的排列顺序。取值范围：ASC：升序排列DESC：降序排列。|
|OrderField| 否| String|快照列表排序的依据字段。取值范围：CREATE_TIME：依据快照的创建时间排序默认按创建时间排序。|

---

Filter详解

- snapshot-id - Array of String - 是否必填：否 -（过滤条件）按照快照的ID过滤。快照
- ID形如： snap-11112222 。
- snapshot-name - Array of String - 是否必填：否 -（过滤条件）按照快照名称过滤。
- snapshot-state - Array of String - 是否必填：否 -（过滤条件）按照快照状态过滤。
- (NORMAL：正常 | CREATING：创建中 | ROLLBACKING：回滚中。)
- disk-usage - Array of String - 是否必填：否 -（过滤条件）按创建快照的云盘类型过
- 滤。 (SYSTEM_DISK：代表系统盘 | DATA_DISK：代表数据盘。)
- project-id - Array of String - 是否必填：否 -（过滤条件）按云硬盘所属项目ID过滤。
- disk-id - Array of String - 是否必填：否 -（过滤条件）按照创建快照的云硬盘ID过
- 滤。
- zone - Array of String - 是否必填：否 -（过滤条件）按照可用区过滤。


##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
|TotalCount| Integer| 快照的数量。|
|SnapshotSet| Array of Snapshot| 快照的详情列表。|
##### eg
crul命令：

```
curl -X POST https://cbs.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/cbs/tc3_request, SignedHeaders=content-type;host, Signature=cf01ca5fddb52bfb894657b00f193b53477cf71b75aa6b754b57ee6b2dc2dd25" -H "Content-Type: application/json" -H "Host: cbs.tencentcloudapi.com" -H "X-TC-Action: DescribeSnapshots" -H "X-TC-Timestamp: 1605458614" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'

```
SDK：

```
com.tencentcloudapi.cbs.v20170312.CbsClient.DescribeSnapshots
```
返回：

```
//todo

```

---

####  6.2   创建快照 CreateSnapshot
接口请求域名： cbs.tencentcloudapi.com 。
本接口（CreateSnapshot）用于对指定云盘创建快照。
只有具有快照能力的云硬盘才能创建快照。云硬盘是否具有快照能力可由DescribeDisks接口查询，见SnapshotAbility字段。
可创建快照数量限制见产品使用限制。
##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DescribeSnapshots|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|DiskId	|是|String|	需要创建快照的云硬盘ID，可通过DescribeDisks接口查询。|
|SnapshotName|	否|	String|	快照名称，不传则新快照名称默认为“未命名”。|
|Deadline|	否|	Timestamp ISO8601	|快照的到期时间，到期后该快照将会自动删除|

##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|SnapshotId|	String|	新创建的快照ID。|
|RequestId|	String|	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
curl -X POST https://cbs.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/cbs/tc3_request, SignedHeaders=content-type;host, Signature=56c76140dbcc5667b89b660e93f9ae9ebafed28ef2c65fa2953f907901390539" -H "Content-Type: application/json" -H "Host: cbs.tencentcloudapi.com" -H "X-TC-Action: CreateSnapshot" -H "X-TC-Timestamp: 1605459154" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{"DiskId":"your DiskId"}'


```
SDK：

```
com.tencentcloudapi.cbs.v20170312.CbsClient.CreateSnapshot
```
返回：

```
//todo

```

---
####  6.3   绑定定期快照策略
本接口（BindAutoSnapshotPolicy）用于绑定云硬盘到指定的定期快照策略。

每个地域下的定期快照策略配额限制请参考文档定期快照。
当已绑定定期快照策略的云硬盘处于未使用状态（即弹性云盘未挂载或非弹性云盘的主机处于关机状态）将不会创建定期快照。

##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DescribeSnapshots|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|DiskId	|是|String|	需要创建快照的云硬盘ID，可通过DescribeDisks接口查询。|
|AutoSnapshotPolicyId| 是| String| 要解绑的定期快照策略ID。|

##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId|	String|	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
curl -X POST https://cbs.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/cbs/tc3_request, SignedHeaders=content-type;host, Signature=84eae95ff38fdac953b4dbcb63639bc02baa18c6194ebbfafbc70f59e87ea6e5" -H "Content-Type: application/json" -H "Host: cbs.tencentcloudapi.com" -H "X-TC-Action: BindAutoSnapshotPolicy" -H "X-TC-Timestamp: 1605459375" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{"DiskIds":["yourDiskId"],"AutoSnapshotPolicyId":"your SnapshotPolicyId"}'

```
SDK：

```
com.tencentcloudapi.cbs.v20170312.CbsClient.BindAutoSnapshotPolicy()
```
返回：

```
//todo

```
---
####  6.4   解绑定定期快照策略
本接口（UnbindAutoSnapshotPolicy）用于解除云硬盘绑定的定期快照策略。
支持批量操作，可一次解除多个云盘与同一定期快照策略的绑定。
如果传入的云盘未绑定到当前定期快照策略，接口将自动跳过，仅解绑与当前定期快照策略绑定的云盘。

##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DescribeSnapshots|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|DiskId	|是|String|	需要创建快照的云硬盘ID，可通过DescribeDisks接口查询。|
|AutoSnapshotPolicyId| 是| String| 要解绑的定期快照策略ID。|

##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId|	String|	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
curl -X POST https://cbs.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/cbs/tc3_request, SignedHeaders=content-type;host, Signature=f38b3dc4eb750712b4547bc2d17a2717d28397f368a19e5e97f99ddd813bb530" -H "Content-Type: application/json" -H "Host: cbs.tencentcloudapi.com" -H "X-TC-Action: UnbindAutoSnapshotPolicy" -H "X-TC-Timestamp: 1605459628" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'


```
SDK：

```
com.tencentcloudapi.cbs.v20170312.CbsClient.BindAutoSnapshotPolicy()
```
返回：

```
//todo

```

---
####  6.5   查询云硬盘列表 DescribeDisks

接口请求域名： cbs.api3.yf-1.tcepoc.fsphere.cn 。
本接口（DescribeDisks）用于查询云硬盘列表。
可以根据云硬盘ID、云硬盘类型或者云硬盘状态等信息来查询云硬盘的详细信息，不同条件之间为与(AND)的关系，过滤信息
详细请见过滤器 Filter 。
如果参数为空，返回当前用户一定数量（ Limit 所指定的数量，默认为20）的云硬盘列表。


##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DescribeDisks|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|DiskIds	|否|Array of String|	按照一个或者多个云硬盘ID查询。|
|Filters| 否|Array of Filter| 过滤条件。参数不支持同时指定 DiskIds 和 Filters 。 |
|Offset| 否| Integer| 偏移量，默认为0。关于 Offset 的更进一步介绍请参考API简介中的相关小节。|
|Limit| 否| Integer|返回数量，默认为20，最大值为100。|
|Order| 否| String|输出云盘列表的排列顺序。取值范围：ASC：升序排列DESC：降序排列。|
|OrderField| 否| String|云盘列表排序的依据字段。取值范围：CREATE_TIME：依据云盘的创建时间排序DEADLINE：依据云盘的到期时间排序默认按云盘创建时间排序。|


---
filter 见
https://console.cloud.tencent.com/api/explorer?Product=cbs&Version=2017-03-12&Action=DescribeDisks&SignVersion=


##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId|	String|	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
curl -X POST https://cbs.tencentcloudapi.com -H "Authorization: TC3-HMAC-SHA256 Credential=AKIDKZfPkKyyht5vXwDzSqgXM0ACCK1TpmV1/2020-11-15/cbs/tc3_request, SignedHeaders=content-type;host, Signature=8f007da213456091234c79ad632f3d865d4b7fd65e84d636358d09b478a12e4f" -H "Content-Type: application/json" -H "Host: cbs.tencentcloudapi.com" -H "X-TC-Action: DescribeDisks" -H "X-TC-Timestamp: 1605460305" -H "X-TC-Version: 2017-03-12" -H "X-TC-Region: ap-guangzhou" -H "X-TC-Language: zh-CN" -d '{}'

```
SDK：

```
com.tencentcloudapi.cbs.v20170312.CbsClient.DescribeDisks()
```
返回：

```
//todo

```

---
####  6.5   创建云硬盘 CreateDisks

接口请求域名： cbs.api3.yf-1.tcepoc.fsphere.cn 。
本接口（CreateDisks）用于创建云硬盘。
预付费云盘的购买会预先扣除本次云盘购买所需金额，在调用本接口前请确保账户余额充足。
本接口支持传入数据盘快照来创建云盘，实现将快照数据复制到新购云盘上。
本接口为异步接口，当创建请求下发成功后会返回一个新建的云盘ID列表，此时云盘的创建并未立即完成。可以通过调用
DescribeDisks接口根据DiskId查询对应云盘，如果能查到云盘，且状态为'UNATTACHED'或'ATTACHED'，则表示创建成功。


##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：CreateDisks|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|DiskType| 是| String| 硬盘介质类型。取值范围：CLOUD_BASIC：表示普通云硬盘CLOUD_PREMIUM：表示高性能云硬盘 CLOUD_SSD：表示SSD云硬盘。|
|DiskChargeType| 是 |String|云硬盘计费类型。|
|DiskName| 否| String| 云盘显示名称。不传则默认为“未命名”。最大长度不能超60个字节。|
|DiskCount| 否| Integer| 创建云硬盘数量，不传则默认为1。单次请求最多可创建的云盘数有限制，具体参见云硬盘使用限制。|
|DiskChargePrepaid| 否| DiskChargePrepaid|预付费模式，即包年包月相关参数设置。通过该参数可以指定包年包月云盘的购买时长、是否设置自动续费等属性，创建预付费 云盘该参数必传。|
|DiskSize| 否 |Integer|云硬盘大小，单位为GB。|
|SnapshotId| 否| String|快照ID，如果传入则根据此快照创建云硬盘，快照类型必须为数据盘快照，可通过DescribeSnapshots接口查询快照|
|ClientToken| 否 |String|用于保证请求幂等性的字符串。该字符串由客户生成，需保证不同请求之间唯一，最大值不超过64个ASCII字符。若不指定该参数，则无法保证请求的幂等性。|
|Encrypt| 否| String| 传入该参数用于创建加密云盘，取值固定为ENCRYPT。 Tags 否ArrayofTag云盘绑定的标签。|
|Shareable| 否| Boolean| 可选参数，默认为False。传入True时，云盘将创建为共享型云盘|
|DiskStoragePoolGroup| 否 |String| 存储资源池组|


##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|DiskIdSet| Array of String| 创建的云硬盘ID列表。|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
//todo 
```
SDK：

```
com.tencentcloudapi.cbs.v20170312.CbsClient.CreateDisks()
```
返回：

```
//todo

```
---
####  6.6   退还云硬盘 TerminateDisks

接口请求域名： cbs.api3.yf-1.tcepoc.fsphere.cn 。
本接口（TerminateDisks）用于退还云硬盘。
当前仅支持退还包年包月云盘。
支持批量操作，每次请求批量云硬盘的上限为50。如果批量云盘存在不允许操作的，请求会以特定错误码返回。


##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：TerminateDisks|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|DiskIds| 是| Array of String| 需退还的云盘ID列表。|

##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
//todo 
```
SDK：

```
com.tencentcloudapi.cbs.v20170312.CbsClient.TerminateDisks()
```
返回：

```
//todo

```
---
####  6.7   挂载云硬盘 AttachDisks

接口请求域名： cbs.api3.yf-1.tcepoc.fsphere.cn 。
本接口（AttachDisks）用于挂载云硬盘。
支持批量操作，将多块云盘挂载到同一云主机。如果多个云盘存在不允许挂载的云盘，则操作不执行，以返回特定的错误码返
回。
本接口为异步接口，当挂载云盘的请求成功返回时，表示后台已发起挂载云盘的操作，可通过接口DescribeDisks来查询对应云
盘的状态，如果云盘的状态由“ATTACHING”变为“ATTACHED”，则为挂载成功。


##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：AttachDisks|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|DiskIds| 是| Array of String| 云盘ID列表。|
|InstanceId| 是 |String| 云服务器实例ID。云盘将被挂载到此云服务器上，通过DescribeInstances接口查询。|
|DeleteWithInstance| 否| Boolean|可选参数，不传该参数则仅执行挂载操作。传入 True 时，会在挂载成功后将
云硬盘设置为随云主机销毁模式，仅对按量计费云硬盘有效。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
//todo 
```
SDK：
```
com.tencentcloudapi.cbs.v20170312.CbsClient.AttachDisks()
```
返回：

```
//todo

```
---
####  6.8  解挂云硬盘 DetachDisks

接口请求域名： cbs.api3.yf-1.tcepoc.fsphere.cn 。
本接口（DetachDisks）用于解挂云硬盘。
支持批量操作，解挂挂载在同一主机上的多块云盘。如果多块云盘存在不允许解挂载的云盘，则操作不执行，以返回特定的错
误码返回。
本接口为异步接口，当请求成功返回时，云盘并未立即从主机解挂载，可通过接口DescribeDisks来查询对应云盘的状态，如果
云盘的状态由“ATTACHED”变为“UNATTACHED”，则为解挂载成功。


##### 入参
|参数名称|必选|类型|解释|
|---|--- |---|---|
|Action| 是| String| 公共参数，本接口取值：DetachDisks|
|Version |是 |String| 公共参数，本接口取值：2017-03-12|
|Region |是| String| 公共参数，详见产品支持的地域列表。|
|DiskIds| 是| Array of String| 需退还的云盘ID列表。|
|InstanceId| 是 |String| 云服务器实例ID。云盘将被挂载到此云服务器上，通过DescribeInstances接口查询。|
##### 返回参数
|参数名称|类型|解释|
|---|---|---|
|RequestId| String| 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。|
##### eg
crul命令：

```
//todo 
```
SDK：
```
com.tencentcloudapi.cbs.v20170312.CbsClient.DetachDisks()
```
返回：

```
//todo

```

---


