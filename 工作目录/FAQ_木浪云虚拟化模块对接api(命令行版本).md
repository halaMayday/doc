# 木浪云虚拟化模块对接api(命令行版本)

### 命令返回格式

```json
{
    "code": "0",
    "data": {
        
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```

## vmware

### 连接虚拟化平台

* 例子

> vmmgr -C --vcenterip 192.168.8.85 --vcenteruser administrator@vc.dev --vcenterpass xxxxxxxxxxxxxxxxxxxxxxxx --sdktype 1 --conf /opt/mulang/vmmgr/cfg/192.168.8.85.conf

* 参数

  | arg         | val                   | require | desc     |
  | ----------- | --------------------- | ------- | -------- |
  | -C          |                       |         | 连接命令 |
  | vcenterip   | 192.168.8.85          |         |          |
  | vcenteruser | administrator@vc.dev  |         |          |
  | vcenterpass | password              |         |          |
  | sdktype     | 1                     |         | 平台类型 |
  | conf        | mclient生成的配置文件 |         |          |



* 返回

```json
{
    "code": "0",
    "data": {
        "sdktype": "1"
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```



### 拉取esxi主机列表

* 例子

  > vmmgr -f --vcenterpass WmfY56VK3GpcH1WycHjzbQ==  --sdktype 1 --conf /opt/mulang/vmmgr/cfg/192.168.8.85.conf

* 参数

  | arg     | val                   | require | desc         |
  | ------- | --------------------- | ------- | ------------ |
  | -f      |                       |         | 查询esxi主机 |
  | sdktype | 1                     |         | 平台类型     |
  | conf    | mclient生成的配置文件 |         |              |

* 返回

```json
{
    "code": "0",
    "data": {
        "count": 5,
        "hostlist": [
            {
                "hostStatus": "normal",
                "ip": "192.168.8.44",
                "version": "6.0.0"
            },
            {
                "hostStatus": "normal",
                "ip": "192.168.8.83",
                "version": "6.0.0"
            },
            {
                "hostStatus": "normal",
                "ip": "192.168.8.81",
                "version": "6.0.0"
            },
            {
                "hostStatus": "normal",
                "ip": "192.168.8.82",
                "version": "6.0.0"
            },
            {
                "hostStatus": "normal",
                "ip": "192.168.9.61",
                "version": "6.0.0"
            }
        ],
        "version": "6.0.0"
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}

```

### 拉取虚拟机列表

* 例子

  > vmmgr -l --vcenterpass WmfY56VK3GpcH1WycHjzbQ==  --sdktype 1 --conf /opt/mulang/vmmgr/cfg/192.168.8.85.conf

* 参数

  | arg     | val                   | require | desc     |
  | ------- | --------------------- | ------- | -------- |
  | -l      |                       |         |          |
  | sdktype | 1                     |         | 平台类型 |
  | conf    | mclient生成的配置文件 |         |          |

* 返回

  ```json
  {
      "code": "0",
      "data": {
          "count": 160,
          "vmlist": [
              {
                  "dc": "DB",
                  "folder": "vm>*\u5df2\u53d1\u73b0\u865a\u62df\u673a",
                  "group": "192.168.8.44",
                  "hostStatus": "normal",
                  "moref": "vm-359034",
                  "powerstate": "poweredOff",
                  "size": "50.0",
                  "vm": "yxCentOS-7-Mysql_2_v5.6_8.163_979_191017100205",
                  "vmtools": "1",
                  "vmuuid": "423bcb77-7bf1-761d-2d0f-48da5db2e476"
              }
          ]
      },
      "err": "",
      "errmsg": "",
      "level": "INFO",
      "status": "0"
  }
  ```



### 拉取虚拟机详细信息列表

* 例子

  > vmmgr -i --vcenterpass WmfY56VK3GpcH1WycHjzbQ==  --sdktype 1 --conf /opt/mulang/vmmgr/cfg/192.168.8.85.conf

* 参数

  | arg     | val                   | require | desc     |
  | ------- | --------------------- | ------- | -------- |
  | -i      |                       |         |          |
  | sdktype | 1                     |         | 平台类型 |
  | conf    | mclient生成的配置文件 |         |          |
  
* 返回

  ``` json
  {
      "code": "0",
      "data": {
          "count": 192,
          "vminfolist": [
              {
                  "cpu": "1",
                  "cpuusage": "0",
                  "disk": "1",
                  "disklist": [
                      {
                          "diskuuid": "6000C29a-4fe2-1947-2d7a-accd4fc20f0f",
                          "mode": "persistent",
                          "size": "34359738368"
                      }
                  ],
                  "guestmemusage": "0",
                  "guestos": "Microsoft Windows 10 (64 \u4f4d)",
                  "hostmemusage": "0",
                  "mem": "2048",
                  "moref": "vm-464826",
                  "overall": "1",
                  "vm": "windows-10-foreige",
                  "vmpath": "[datastore-44] windows-10-foreige/windows-10-foreige.vmx",
                  "vmuuid": "423bdd03-1494-2cd4-53de-45ed46db404a"
              }
          ]
      },
      "err": "",
      "errmsg": "",
      "level": "INFO",
      "status": "0"
  }
  ```

### 备份虚拟机

* 例子

> vmmgr --backup --conf /opt/mulang/vmmgr/cfg/192.168.8.85.conf --sdktype 1 --host 192.168.8.56 --moref vm-443981 --backup-type vm -g 348 --log-id 4776 --last-success-gen []

* 参数

  | arg              | val                     | require | desc                            |
  | ---------------- | ----------------------- | ------- | ------------------------------- |
  | --backup         |                         |         |                                 |
  | sdktype          | 1                       |         | 平台类型                        |
  | conf             | mclient生成的配置文件   |         |                                 |
  | host             |                         |         | 备份系统ip                      |
  | moref            |                         |         | 虚拟机moref                     |
  | backup-type      | vm: 虚拟机; volume:云盘 |         | 备份类型                        |
  | g                |                         |         | 备份数据generation              |
  | log-id           |                         |         | 任务id                          |
  | last-success-gen |                         | false   | 上次成功备份数据来源,tstack使用 |

* 返回

```json
{
    "code": "0",
    "data": {
        "avgspeed": "0",
        "progress": "100",
        "read": "0",
        "size": 0,
        "speed": "213",
        "storage": "0",
        "vm": "vm-443981"
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```



### 挂载克隆虚拟机

* 例子

> vmmgr --clone --conf /opt/mulang/vmmgr/cfg/192.168.8.85.conf --sdktype 1 --host 192.168.8.58 --share /media/pool0/snapshots/192.168.8.85/ars --vihost 192.168.8.81 --moref vm-166219 --newvm centos5.3-tyl_4694_200714201339 --generation 333 --poweron --log-id 22  

* 参数


| arg        | val                   | require | desc                          |
| ---------- | --------------------- | ------- | ----------------------------- |
| --clone    |                       |         |                               |
| sdktype    | 1                     |         | 平台类型                      |
| conf       | mclient生成的配置文件 |         |                               |
| host       |                       |         | 备份系统对外ip                |
| share      |                       |         | 用于nfs的共享存储路径         |
| vihost     | 192.168.8.81          |         | esxi 主机ip                   |
| moref      |                       |         | 原虚拟机moref                 |
| newvm      |                       |         | 虚拟机名称                    |
| generation |                       |         | 备份数据generation ,等同于 -g |
| log-id     |                       |         | 任务id                        |
| poweron    | 无参数                | false   | 是否将挂载后的虚拟机开机      |
| network    | 无参数                | false   | 是否禁用挂载后的虚拟机网络    |

* 返回

```json
{
    "code": "0",
    "data": {
        "extrainfo": "192.168.8.58:/media/pool0/snapshots/192.168.8.85/ars",
        "moref": "vm-471341",
        "msg": "succeeded",
        "storage": "nfs",
        "vm": "centos5.3-tyl_4694_200714201339",
        "vm_path_id": "16dbf6dd-5901-403d-8b56-97b17779b579",
        "volume": "mlc-192.168.8.58-192.168.8.85"
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```



### 挂载恢复虚拟机

* 例子

>vmmgr --restore --conf /data/vmmgr/cfg/192.168.8.85.conf --sdktype 1 --host 192.168.12.122 --share /media/pool0/snapshots/192.168.8.85/ars --vihost 192.168.8.83 --moref vm-472168 --newvm centos-2G --generation 0 --log-id 2 

* 参数


| arg       | val                   | require | desc     |
| --------- | --------------------- | ------- | -------- |
| --restore |                       |         |          |
| sdktype   | 1                     |         | 平台类型 |
| conf      | mclient生成的配置文件 |         |          |
| host       |                       |         | 备份系统对外ip                |
| share      |                       |         | 用于nfs的共享存储路径         |
| vihost     | 192.168.8.81          |         | esxi 主机ip                   |
| moref      |                       |         | 原虚拟机moref                 |
| generation |                       |         | 备份数据generation ,等同于 -g |
| log-id     |                       |         | 任务id                        |
| poweron    | 无参数                | false   | 是否将挂载后的虚拟机开机      |
| network    | 无参数 | false | 是否禁用挂载后的虚拟机网络 |


* 返回

```json
{
    "code": "0",
    "data": {
        "extrainfo": "192.168.12.122:/media/pool0/snapshots/192.168.8.85/ars",
        "msg": "succeeded",
        "status": "0",
        "storage": "nfs",
        "vm": "centos-2G",
        "vm_path_id": "5a87d735-1b07-4c47-a303-d1f2bb554243",
        "volume": "mlc-192.168.12.122-192.168.8.85"
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```



### 查询迁移存储

* 例子

> vmmgr --vmotion --lookup-host-storage --conf /opt/mulang/vmmgr/cfg/192.168.8.85.conf --sdktype 1 --host-ip 192.168.8.81 --moref vm-166219 

* 参数


| arg                   | val                   | require | desc        |
| --------------------- | --------------------- | ------- | ----------- |
| --vmotion             |                       |         |             |
| --lookup-host-storage |                       |         |             |
| sdktype               | 1                     |         | 平台类型    |
| conf                  | mclient生成的配置文件 |         |             |
| host-ip               |                       |         | esxi 主机ip |
| moref                 |                       |         | 虚拟机moref |

  

* 返回

```json
{
    "code": "0",
    "data": [
        {
            "storageMoref": "datastore-279",
            "storageName": "vsanDatastore"
        }
    ],
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```



### 迁移虚拟机

* 例子

> vmmgr  --vmotion --migrate --conf /data/vmmgr/cfg/192.168.8.79.conf --sdktype 1 --host 192.168.8.61 --host-ip 192.168.8.12 --moref vm-141566 --datasource-moref datastore-1550 --migrateid vmtask_1595065680 --log-id 24 --new-moref vm-141581 --generation 4

* 参数


| arg              | val                   | require | desc                             |
| ---------------- | --------------------- | ------- | -------------------------------- |
| --vmotion        |                       |         |                                  |
| --migrate        |                       |         |                                  |
| sdktype          | 1                     |         | 平台类型                         |
| conf             | mclient生成的配置文件 |         |                                  |
| host             |                       |         | 备份系统对外ip                   |
| host-ip          |                       |         | esxi主机ip                       |
| moref            |                       |         | 原虚拟机moref                    |
| datasource-moref |                       |         | 迁移的目标存储唯一标识.          |
| migrateid        |                       |         | 发起vmware 迁移任务的id          |
| log-id           |                       |         | 木浪云系统任务id                 |
| new-moref        | 挂载的虚拟机moref.    |         | (恢复迁移中,和原虚拟机moref相同) |
| generation       |                       |         |                                  |

* 返回

```json
{
    "code": "0",
    "data": "execute vMotion success !!!",
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```



### 挂载迁移虚拟机进度

* 例子

> vmmgr --vmotion --progress --moref vm-141566 --new-moref vm-141581 --migrateid vmtask_1595065680 --conf /data/vmmgr/cfg/192.168.8.79.conf --log-id 24 --sdktype 1 

* 参数


| arg        | val                   | require | desc     |
| ---------- | --------------------- | ------- | -------- |
| --vmotion  |                       |         |          |
| --progress |                       |         |          |
| sdktype    | 1                     |         | 平台类型 |
| conf       | mclient生成的配置文件 |         |          |
| moref            |                       |         | 原虚拟机moref                    |
| datasource-moref |                       |         | 迁移的目标存储唯一标识.          |
| migrateid        |                       |         | 发起vmware 迁移任务的id          |
| log-id           |                       |         | 木浪云系统任务id                 |
| new-moref        | 挂载的虚拟机moref.    |         |  |

* 返回

```json
{
    "code": "0",
    "data": {
        "progress": 46,
        "status": "running"
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```

### 恢复迁移虚拟机进度

* 例子

> vmmgr --vmotion --progress --moref vm-141566 --migrateid vmtask_1595065681 --conf /data/vmmgr/cfg/192.168.8.79.conf --log-id 25 --sdktype 1 

* 参数


| arg        | val                   | require | desc     |
| ---------- | --------------------- | ------- | -------- |
| --vmotion  |                       |         |          |
| --progress |                       |         |          |
| sdktype    | 1                     |         | 平台类型 |
| conf       | mclient生成的配置文件 |         |          |
| moref            |                       |         | 原虚拟机moref                    |
| datasource-moref |                       |         | 迁移的目标存储唯一标识.          |
| migrateid        |                       |         | 发起vmware 迁移任务的id          |
| log-id           |                       |         | 木浪云系统任务id                 |

* 返回

```json
{
    "code": "0",
    "data": {
        "progress": 46,
        "status": "running"
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```


### 检查虚拟机电源

* 例子

> vmmgr --check-vm --conf /data/vmmgr/cfg/192.168.8.79.conf --moref vm-141566 --sdktype 1

* 参数


| arg        | val                   | require | desc        |
| ---------- | --------------------- | ------- | ----------- |
| --check-vm |                       |         |             |
| sdktype    | 1                     |         | 平台类型    |
| conf       | mclient生成的配置文件 |         |             |
| moref      |                       |         | 虚拟机moref |

* 返回

```json
{
    "code": "0",
    "data": {
        "vmtools": 0,
        "powerstatus": 1,
        "ipAddress": ""
    },
    "err": "",
    "level": "INFO",
    "errmsg": "",
    "status": "0"
}
```



### 打开虚拟机电源

* 例子

>  vmmgr --poweron-vm --vihost 192.168.8.12  --conf /data/vmmgr/cfg/192.168.8.79.conf --sdktype 1 --moref vm-141566

* 参数
  

  | arg          | val                   | require | desc                    |
  | ------------ | --------------------- | ------- | ----------------------- |
  | --poweron-vm |                       |         |                         |
  | sdktype      | 1                     |         | 平台类型                |
  | conf         | mclient生成的配置文件 |         |                         |
  | vihost       |                       |         | 虚拟机所在esxi 主机的ip |
  | moref        |                       |         | 虚拟机moref             |

* 返回

```json
{
    "code": "0",
    "data": "",
    "err": "poweron success vm vm-141566",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```



### 关闭虚拟机电源

* 例子

> vmmgr --shutdown-vm --conf /data/vmmgr/cfg/192.168.8.79.conf --sdktype 1 --moref vm-141566 

* 参数

  | arg           | val                   | require | desc        |
  | ------------- | --------------------- | ------- | ----------- |
  | --shutdown-vm |                       |         |             |
  | sdktype       | 1                     |         | 平台类型    |
  | conf          | mclient生成的配置文件 |         |             |
  | moref         |                       |         | 虚拟机moref |
  
* 返回

``` json
{
    "code": "",
    "data": "",
    "err": "shutdown success vm vm-141566",
    "errmsg": "",
    "level": "ERROR",
    "status": "0"
}
```

### 卸载虚拟机

* 例子

> vmmgr  --unload --conf /opt/mulang/vmmgr/cfg/192.168.8.85.conf --sdktype 1 --vihost 192.168.8.81 --moref vm-166219 --new-moref vm-471341 --vm-path-id 16dbf6dd-5901-403d-8b56-97b17779b579 --newvm centos5.3-tyl_4694_200714201339 

* 参数


| arg        | val                            | require | desc                             |
| ---------- | ------------------------------ | ------- | -------------------------------- |
| --unload   |                                |         |                                  |
| sdktype    | 1                              |         | 平台类型                         |
| conf       | mclient生成的配置文件          |         |                                  |
| vihost     |                                |         | 虚拟机所在esxi 主机的ip          |
| moref      |                                |         | 原虚拟机moref                    |
| new-moref  | 挂载的虚拟机moref.             |         | (恢复迁移中,和原虚拟机moref相同) |
| newvm      | 挂载的虚拟机名称.              |         | new-moref和newvm需要相对应       |
| vm-path-id | 挂载的虚拟机使用的路径中的uuid |         | 挂载虚拟机成功时,返回.           |

* 返回

```json
{
    "code": "0",
    "data": {
        "moref": "vm-471341",
        "msg": "succeeded."
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```

### 本地分析虚拟机目录-挂载虚拟磁盘

* 例子

> vmmgr --mount --conf /data/vmmgr/cfg/192.168.15.248.conf --sdktype 9 --moref 546e3349-2c03-4def-b58b-38ae928175d7 --generation 2 --mp /data/vmmgr/vm/192.168.15.248/run/root/546e3349-2c03-4def-b58b-38ae928175d7_2

* 参数


| arg        | val                   | require | desc                          |
| ---------- | --------------------- | ------- | ----------------------------- |
| --mount    |                       |         |                               |
| sdktype    | 1                     |         | 平台类型                      |
| conf       | mclient生成的配置文件 |         |                               |
| moref      |                       |         | 原虚拟机moref                 |
| generation |                       |         | 备份数据generation ,等同于 -g |
|            |                       |         | mount point                   |

* 返回

```json
{
    "code": "0",
    "data": {
        "moref": "vm-471341",
        "mp":"/data/vmmgr/vm/192.168.15.248/run/root/546e3349-2c03-4def-b58b-38ae928175d7_2",
        "msg": "succeeded."
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```

### 本地分析虚拟机目录-卸载虚拟磁盘

* 例子

> vmmgr --umount --conf /data/vmmgr/cfg/192.168.15.248.conf --sdktype 9 --moref 546e3349-2c03-4def-b58b-38ae928175d7 --generation 2 --mp /data/vmmgr/vm/192.168.15.248/run/root/546e3349-2c03-4def-b58b-38ae928175d7_2

* 参数


| arg        | val                   | require | desc                          |
| ---------- | --------------------- | ------- | ----------------------------- |
| --mount    |                       |         |                               |
| sdktype    | 1                     |         | 平台类型                      |
| conf       | mclient生成的配置文件 |         |                               |
| moref      |                       |         | 原虚拟机moref                 |
| generation |                       |         | 备份数据generation ,等同于 -g |
|            |                       |         | mount point                   |

* 返回

```json
{
    "code": "0",
    "data": {
        "moref": "vm-471341",
        "mp":"/data/vmmgr/vm/192.168.15.248/run/root/546e3349-2c03-4def-b58b-38ae928175d7_2",
        "msg": "succeeded."
    },
    "err": "",
    "errmsg": "",
    "level": "INFO",
    "status": "0"
}
```



### 恢复虚拟机文件



> vmmgr --restore-file --conf /data/vmmgr/cfg/192.168.15.248.conf --sdktype 9 --moref 546e3349-2c03-4def-b58b-38ae928175d7 --generation 2 --user root --passwd xxxxxx --filepath /root/hui --sshport 22 