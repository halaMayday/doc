### 验证平台账号密码

> fusioncloudMgr  -auth -ip 192.168.15.248 -port 5000 -user admin -pass xxxxxxx -c /data/vmmgr/cfg/192.168.15.248.conf --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| auth    |                       | 连接命令                           |
| ip      | 192.168.15.248        | api ip                             |
| port    | 5000                  | api平台端口                        |
| user    | admin                 |                                    |
| pass    | 1                     | 加密后密码                         |
| c    | /data/vmmgr/cfg/192.168.15.248.conf | mclient生成的配置文件 |
| sdktype | 9                     | 平台类型,讯飞云                    |

``` json
[root@mulangcloud ~]# echo $?
0
```



## 拉取主机列表

fusioncloudMgr  -f -c /data/vmmgr/cfg/192.168.15.248.conf --sdktype 9


| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| f    |                       | 拉取主机命令                           |
| c       | /data/vmmgr/cfg/192.168.15.248.conf | mclient生成的配置文件 |
| sdktype | 9                     | 平台类型,讯飞云                    |




```json
{
    "count": 1,
    "hostlist": [
        {
            "hostStatus": "normal",
            "hostuuid": "localhost.localdomain",
            "ip": "192.168.15.248",
            "projectdomain": "default",
            "projectid": "0869f5b3b3d3419fb49275e57002eb92",
            "version": "V3"
        }
    ],
    "version": "V3"
}
```



### 拉取虚拟机列表

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| l    |                       | 拉取虚拟机命令                           |
| c       | /data/vmmgr/cfg/192.168.15.248.conf | mclient生成的配置文件 |
| sdktype | 9                     | 平台类型,讯飞云                    |

> fusioncloudMgr  -l -c /data/vmmgr/cfg/192.168.15.248.conf --sdktype 9

``` json
{"count":4,"vmlist":[{"vmuuid":"c38982ba-3899-45f4-bf69-57778ce1cebd","vmtools":"0","folder":"","size":"35.0","moref":"c38982ba-3899-45f4-bf69-57778ce1cebd","vm":"test_vm_228_11_200927235207","powerstate":"poweredOn","dc":"0869f5b3b3d3419fb49275e57002eb92","group":"0869f5b3b3d3419fb49275e57002eb92"},{"vmuuid":"65d030cf-ed44-403c-8276-5da3c60fe8ae","vmtools":"0","folder":"","size":"1.0","moref":"65d030cf-ed44-403c-8276-5da3c60fe8ae","vm":"hui-1_12_200927235057","powerstate":"poweredOn","dc":"0869f5b3b3d3419fb49275e57002eb92","group":"0869f5b3b3d3419fb49275e57002eb92"},{"vmuuid":"efd59822-7656-453c-9131-d9dbd4e34528","vmtools":"0","folder":"","size":"1.0","moref":"efd59822-7656-453c-9131-d9dbd4e34528","vm":"hui-1","powerstate":"poweredOn","dc":"0869f5b3b3d3419fb49275e57002eb92","group":"0869f5b3b3d3419fb49275e57002eb92"},{"vmuuid":"546e3349-2c03-4def-b58b-38ae928175d7","vmtools":"0","folder":"","size":"35.0","moref":"546e3349-2c03-4def-b58b-38ae928175d7","vm":"test_vm_228","powerstate":"poweredOn","dc":"0869f5b3b3d3419fb49275e57002eb92","group":"0869f5b3b3d3419fb49275e57002eb92"}]}
```



### 拉取虚拟机详细信息列表

> fusioncloudMgr  -i -c /data/vmmgr/cfg/192.168.15.248.conf --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| i    |                       | 拉取虚拟机详细信息列表命令                           |
| c       | /data/vmmgr/cfg/192.168.15.248.conf | mclient生成的配置文件      |
| sdktype | 9                     | 平台类型,讯飞云                    |

```json
{"vminfolist":[{"vmuuid":"3d451ef3-195f-4c95-8670-5d9da7c22b28","cpuusage":"","hostmemusage":"","moref":"3d451ef3-195f-4c95-8670-5d9da7c22b28","projectdomain":"nova","hostuuid":"localhost.localdomain","ip":"","cpu":"2","hostid":"edd4ff9dd1fc7ab1d28aa89e6302a2f4ec255286a6cc689712eae93f","vmpath":"default/0869f5b3b3d3419fb49275e57002eb92/localhost.localdomain","disk":"24","guestos":"","hostname":"localhost.localdomain","mem":"2048","vm":"test_vm_228_17_200928163821","overall":"","guestmemusage":"","projectid":"0869f5b3b3d3419fb49275e57002eb92"},{"vmuuid":"efd59822-7656-453c-9131-d9dbd4e34528","cpuusage":"","hostmemusage":"","moref":"efd59822-7656-453c-9131-d9dbd4e34528","projectdomain":"nova","hostuuid":"localhost.localdomain","ip":"","cpu":"1","hostid":"edd4ff9dd1fc7ab1d28aa89e6302a2f4ec255286a6cc689712eae93f","vmpath":"default/0869f5b3b3d3419fb49275e57002eb92/localhost.localdomain","disk":"1","guestos":"","hostname":"localhost.localdomain","mem":"64","vm":"hui-1","overall":"","guestmemusage":"","projectid":"0869f5b3b3d3419fb49275e57002eb92"},{"vmuuid":"546e3349-2c03-4def-b58b-38ae928175d7","cpuusage":"","hostmemusage":"","moref":"546e3349-2c03-4def-b58b-38ae928175d7","projectdomain":"nova","hostuuid":"localhost.localdomain","ip":"","cpu":"2","hostid":"edd4ff9dd1fc7ab1d28aa89e6302a2f4ec255286a6cc689712eae93f","vmpath":"default/0869f5b3b3d3419fb49275e57002eb92/localhost.localdomain","disk":"24","guestos":"","hostname":"localhost.localdomain","mem":"2048","vm":"test_vm_228","overall":"","guestmemusage":"","projectid":"0869f5b3b3d3419fb49275e57002eb92"}],"count":3}
```



### 备份虚拟机

> fusioncloudMgr -b -c /data/vmmgr/cfg/192.168.15.248.conf -vu 546e3349-2c03-4def-b58b-38ae928175d7 -bi 5774d12f-ac91-4cf8-8eec-5731ef68b851 -bt incr --log-id 14 --host","192.168.11.92 --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| b   |                       | 备份虚拟机命令                   |
| c       | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件 |
| vu | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid |
| bi | 5774d12f-ac91-4cf8-8eec-5731ef68b851 | 备份id |
| bt | incr,full | 备份模式 |
| log-id | 14 | 备份log-id |
| host | 192.168.11.92 | 备份节点ip |
| sdktype | 9 | 平台类型,讯飞云 |


``` json
{"generation":"4","disk":[{"mode":"incr","elapsedtimems":"1601257293603","size":"1073741824","rdiffsize":"1073741824","diskid":"0","speed":"","status":"0"},{"mode":"incr","elapsedtimems":"1601257293620","size":"21474836480","rdiffsize":"21474836480","diskid":"1","speed":"","status":"0"},{"mode":"incr","elapsedtimems":"1601257293630","size":"8589934592","rdiffsize":"8589934592","diskid":"2","speed":"","status":"0"},{"mode":"incr","elapsedtimems":"1601257293659","size":"5368709120","rdiffsize":"5368709120","diskid":"3","speed":"","status":"0"},{"mode":"incr","elapsedtimems":"1601257293669","size":"1073741824","rdiffsize":"1073741824","diskid":"4","speed":"","status":"0"}],"totaltime":"12880","moref":"546e3349-2c03-4def-b58b-38ae928175d7"}
```



### 查询虚拟机备份进度

> /opt/mulang/vmmgr/vendor/fusioncloud/bin/fusioncloudMgr -T -c /data/vmmgr/cfg/192.168.15.248.conf -vu 546e3349-2c03-4def-b58b-38ae928175d7 -O 1601257257 --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| T   |                       | 查询虚拟机备份进度                  |
| c       | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件   |
| vu      | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid |
| O | 1601257257 | 自19700101 到现在的秒数 |
| sdktype | 9 | 平台类型,讯飞云 |

```json
{"read":1,"size":1,"vm":"546e3349-2c03-4def-b58b-38ae928175d7 avgspeed":"203392 progress":"100 storage":"1 speed":"325427"}
```



### 检查虚拟机状态

> fusioncloudMgr  -cv -c /data/vmmgr/cfg/192.168.15.248.conf -vu 546e3349-2c03-4def-b58b-38ae928175d7 --sdktype 9 

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| cv  |                       | 检查虚拟机状态                    |
| c       | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件 |
| vu | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid |
| sdktype | 9                     | 平台类型,讯飞云                    |

### 关闭虚拟机

> fusioncloudMgr  -poff -c /data/vmmgr/cfg/192.168.15.248.conf -vu 546e3349-2c03-4def-b58b-38ae928175d7 --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| poff |                       | 关闭虚拟机命令                      |
| c       | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件 |
| vu | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid |
| sdktype | 9                     | 平台类型,讯飞云                    |


### 克隆迁移虚拟机

> fusioncloudMgr -r --clone -c /data/vmmgr/cfg/192.168.15.248.conf -vu efd59822-7656-453c-9131-d9dbd4e34528 --generation 4 -nv 'hui-1_15_200928094211' -po --log-id 9 --host 192.168.11.92 --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| r   |                       | 克隆迁移虚拟机命令    |
| c          | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件 |
| vu | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid |
| generation | 3 | 备份副本记录 |
| nv | hui-1_15_200928094211 | 新虚拟机名称 |
| po |  | 开机 |
| log-id | 9 | 克隆log-id |
| host | 192.168.11.92 | 备份节点ip |
| sdktype | 9                     | 平台类型,讯飞云                    |

```json
{"msg":"succeeded","volume":"","moref":"33948840-35ba-40ff-bab0-53a49193bce7","vm":"hui-1_15_200928094211","storage":"ceph","vm_path_id":"","extrainfo":""}
```



### 恢复迁移虚拟机
> fusioncloudMgr  -r -c /data/vmmgr/cfg/192.168.15.248.conf -vu 546e3349-2c03-4def-b58b-38ae928175d7 -generation 6 -po --log-id 11 --host 192.168.11.92 --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| r   |                       | 拉取虚拟机详细信息列表命令                           |
| c          | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件      |
| vu         | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid                 |
| generation | 6 | 备份副本记录 |
| log-id | 11 | 恢复log-id |
| host | 192.168.11.92 | 备份节点ip |
| sdktype | 9 | 平台类型,讯飞云 |

```json
{"msg":"succeeded","volume":"","moref":"833e2ac7-fad8-49c8-8efc-1b2bf8f1b520","vm":"hui-1_9_200928170218","storage":"ceph","vm_path_id":"","extrainfo":""}
```

### 克隆或者恢复迁移进度

> fusioncloudMgr -RT -c /data/vmmgr/cfg/192.168.15.248.conf -vu 546e3349-2c03-4def-b58b-38ae928175d7 --log-id 14 --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| RT |                       | 克隆或者恢复迁移进度命令               |
| c       | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件    |
| vu | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid |
| log-id | 14 | 克隆或者恢复的log-id |
| sdktype | 9                     | 平台类型,讯飞云                    |

```json
{"avgspeed":"0","progress":"100","size":0,"speed":"0","storage":"0","vm":"546e3349-2c03-4def-b58b-38ae928175d7","write":0}
```



### 本地挂载虚拟磁盘
>  fusioncloudMgr  --mount -c /data/vmmgr/cfg/192.168.15.248.conf -vu 546e3349-2c03-4def-b58b-38ae928175d7 --generation 3 -mp /data/vmmgr/vm/192.168.15.248/run/root/546e3349-2c03-4def-b58b-38ae928175d7_3 --sdktype 9

| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| mount |                       | 本地挂载虚拟磁盘命令  |
| c          | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件 |
| vu | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid |
| generation | 3 | 备份副本记录 |
| mp | mountpoint | 临时挂载目录 |
| sdktype | 9                     | 平台类型,讯飞云                    |


### 本地卸载虚拟磁盘

>  fusioncloudMgr  --umount -c /data/vmmgr/cfg/192.168.15.248.conf -vu 546e3349-2c03-4def-b58b-38ae928175d7 --generation 3 -mp /data/vmmgr/vm/192.168.15.248/run/root/546e3349-2c03-4def-b58b-38ae928175d7_3 --sdktype 9


| arg     | val                   | desc                               |
| ------- | --------------------- | ---------------------------------- |
| umount |                       | 本地卸载虚拟磁盘命令                   |
| c          | /data/vmmgr/cfg/192.168.15.248.conf  | mclient生成的配置文件 |
| vu | 546e3349-2c03-4def-b58b-38ae928175d7 | 虚拟机uuid |
| generation | 3 | 备份副本记录 |
| mp | mountpoint | 临时挂载目录 |
| sdktype | 9                     | 平台类型,讯飞云                    |
