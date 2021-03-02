Tophc 天融信平台的KVM备份

eg:
instanceId:GUS26b321e6-b43d-47da-92f4-84b4c82723d9
volumeUuid:02a563f0-24a0-4d7c-bee4-bdb9bf047e0f

### 1.显示所有虚拟机，包括正在运行的和没有运行的
<```
[root@TOS-1517 ~]# virsh list --all
 Id    Name                                      State
----------------------------------------------------------
 1     LCM49a5e8e4-d824-4f36-9421-3e839715b094   running
 9     GST76c222f0-6f23-4f4c-b15b-83c94df74143   running
 131   GUS26b321e6-b43d-47da-92f4-84b4c82723d9   running
 132   GUS887ce708-e39f-49c7-903c-8ce096e14d33   running

```

### 2.查看该虚拟机的磁盘位置
<```
[root@TOS-1517 ~]# virsh domblklist GUS26b321e6-b43d-47da-92f4-84b4c82723d9
 Target   Source
---------------------------------------------------------------------------------------
 hdd      -
 fda      -
 vda      iqn.2018-11.topsec.com.cn:eeaddd0b-ded1-4011-a719-1cb7e32fd14did104798037/1

```

###3.获取磁盘设备名

  "node-name": "libvirt-1-format" 就是设备名字

```
# 通过qmp查看
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute": "query-block" }'

# 通过hmp查看
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --hmp 'info block'
```

<```
[root@TOS-1517 ~]# virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute": "query-block" }'
{
  "return": [
    {
      "io-status": "ok",
      "device": "",
      "locked": false,
      "removable": true,
      "qdev": "ide0-1-1",
      "tray_open": false,
      "type": "unknown"
    },
    {
      "device": "",
      "locked": false,
      "removable": true,
      "qdev": "fdc0-0-0",
      "type": "unknown"
    },
    {
      "io-status": "ok",
      "device": "",
      "locked": false,
      "removable": false,
      "inserted": {
        "iops_rd": 0,
        "detect_zeroes": "off",
        "image": {
          "virtual-size": 3221225472,
          "filename": "/dev/sdd",
          "format": "raw",
          "actual-size": 0,
          "dirty-flag": false
        },
        "iops_wr": 0,
        "ro": false,
        "node-name": "libvirt-1-format",
        "backing_file_depth": 0,
        "drv": "raw",
        "iops": 0,
        "bps_wr": 0,
        "write_threshold": 0,
        "encrypted": false,
        "bps": 0,
        "bps_rd": 0,
        "cache": {
          "no-flush": false,
          "direct": true,
          "writeback": false
        },
        "file": "/dev/sdd",
        "encryption_key_missing": false
      },
      "qdev": "/machine/peripheral/virtio-disk0/virtio-backend",
      "type": "unknown"
    }
  ],
  "id": "libvirt-372"
}
```

### 4.bitmap操作
<```
# 创建非持久bitmap（qemu >= 2.4）
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 '{ "execute": "block-dirty-bitmap-add", "arguments": {"node": "ibvirt-1-format","name": "mcl_bitmap0"}}'

# 创建持久bitmap（qemu >= 2.10）
virsh qemu-monitor-command DOMAIN '{ "execute": "block-dirty-bitmap-add", "arguments": {"node": "drive-virtio-disk0","name": "bitmapY", "persistent": true}}'

# 删除bitmap
virsh qemu-monitor-command DOMAIN '{ "execute" : "block-dirty-bitmap-remove", "arguments" : { "node" : "drive-virtio-disk0", "name" : "bitmap0" } }'

# 重置bitmap
virsh qemu-monitor-command DOMAIN '{ "execute": "block-dirty-bitmap-clear", "arguments": {"node": "drive-virtio-disk0","name": "bitmap0"}}'

# 查询虚拟机所有磁盘的块信息，含bitmap
virsh qemu-monitor-command DOMAIN --pretty '{ "execute": "query-block" }'

# 查询虚拟机指定磁盘的bitmap（查询第一块磁盘使用[0]，第二块用[1]，以此类推）
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute": "query-block" }' | jq .return[0] | sed -n '/dirty\-bitmaps/,/]/p'
```

eg：
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute": "query-block" }' | jq .return[0] | sed -n '/dirty\-bitmaps/,/]/p'


创建bitmap
"dirty-bitmaps": [
        {
          "name": "mcl_bitmap0",
          "recording": true,
          "persistent": false,
          "busy": false,
          "status": "active",
          "granularity": 65536,
          "count": 0
        }
      ]


QMP事务
本地第一次全量备份

virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute": "transaction", "arguments": { "actions": [ { "type": "block-dirty-bitmap-add", "data": {"node":"libvirt-1-format", "name":"hufan_fullback"}}, { "type": "drive-backup", "data": {"device": "libvirt-1-format", "target": "/backup/backup-001.img","sync":"top" }} ]} }'

本地第二次增量备份

virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute" : "drive-backup" , "arguments" : { "device" : "libvirt-1-format" , "sync" : "incremental" , "bitmap" : "hufan_fullback" , "target" : "/root/backup/inc.0.qcow2" } }'


QMP事务
本地第一次全量备份
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 '{ "execute": "transaction", "arguments": { "actions": [ { "type": "block-dirty-bitmap-add", "data": {"node":"libvirt-1-format", "name":"mlc_bitmap0"}}, { "type": "drive-backup", "data": {"device": "libvirt-1-format", "target": "/root/backup/top.img","sync":"top" }} ]} }'


# ...运行一段时间...
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute" : "drive-backup" , "arguments" : { "device" : "libvirt-1-format" , "sync" : "incremental" , "bitmap" : "mlc_bitmap0" , "target" : "/root/backup/inc.0.qcow2" } }'

# ...又运行一段时间...
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute" : "drive-backup" , "arguments" : { "device" : "libvirt-1-format" , "sync" : "incremental" , "bitmap" : "mlc_bitmap0" , "target" : "/root/backup/inc.1.qcow2" } }'



问题：
[root@TOS-1517 ~]# virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute": "transaction", "arguments": { "actions": [ { "type": "block-dirty-bitmap-add", "data": {"node":"libvirt-1-format", "name":"mlc_bitmap0"}}, { "type": "drive-backup", "data": {"device": "libvirt-1-format", "target": "/root/backup/top.img","sync":"top" }} ]} }'
{
  "id": "libvirt-372",
  "error": {
    "class": "GenericError",
    "desc": "Invalid job ID ''"
  }
}

执行备份之前 可以另外开一个窗口 监听qmp的事件

<```
# 始终监听事件
virsh qemu-monitor-event GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --timestamp --loop

# 当收到特定事件后停止监听
virsh qemu-monitor-event GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --event BLOCK_JOB_COMPLETED
```


查看备份任务：
# 通过qmp查看
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute": "query-block-jobs" }'

# 通过hmp查看
virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --hmp 'info block-jobs'
