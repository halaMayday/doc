{
    "backupType": "full",
    "domain": "77fae30c-dac9-4981-be9e-bb1c48326995",
    "logid": "1",
    "nfsIp": "1192.168.14.113",
    "diskList": [
        {
            "diskUuid": "ICP备案查询",
            "diskPath": "/dev/disk/by-path/ip-127.0.0.1:3260-iscsi-iqn.2010-10.org.openstack:volume-3ed42c9a-76f8-42a4-b8a3-b1acca1aa6dc-lun-0",
            "volumeName": "lvm-test-os-02",
            "volumeId": "3ed42c9a-76f8-42a4-b8a3-b1acca1aa6dc"
        }
    ]
}


---
<!-- 常用命令 -->
>> 获取磁盘设备名：

virsh qemu-monitor-command 77fae30c-dac9-4981-be9e-bb1c48326995 --pretty '{ "execute": "query-block" }'

eg：

{
  "return": [
    {
      "io-status": "ok",
      "device": "drive-virtio-disk0",
      "locked": false,
      "removable": false,
      "inserted": {
        "iops_rd": 0,
        "detect_zeroes": "off",
        "image": {
          "virtual-size": 3221225472,
          "filename": "/dev/disk/by-path/ip-127.0.0.1:3260-iscsi-iqn.2010-10.org.openstack:volume-3ed42c9a-76f8-42a4-b8a3-b1acca1aa6dc-lun-0",
          "format": "raw",
          "actual-size": 0,
          "dirty-flag": false
        },
        "iops_wr": 0,
        "ro": false,
        "node-name": "#block147",
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
        "file": "/dev/disk/by-path/ip-127.0.0.1:3260-iscsi-iqn.2010-10.org.openstack:volume-3ed42c9a-76f8-42a4-b8a3-b1acca1aa6dc-lun-0",
        "encryption_key_missing": false
      },
      "qdev": "/machine/peripheral/virtio-disk0/virtio-backend",
      "dirty-bitmaps": [
        {
          "name": "#block147",
          "recording": true,
          "persistent": false,
          "busy": false,
          "status": "active",
          "granularity": 65536,
          "count": 0
        },
        {
          "name": "bitmap0",
          "recording": true,
          "persistent": false,
          "busy": false,
          "status": "active",
          "granularity": 65536,
          "count": 0
        }
      ],
      "type": "unknown"
    }
  ],
  "id": "libvirt-26"
}



---
01:
virsh dumpxml  77fae30c-dac9-4981-be9e-bb1c48326995

<domain type='kvm' id='2'>
  <name>instance-00000114</name>
  <uuid>77fae30c-dac9-4981-be9e-bb1c48326995</uuid>
  <metadata>
    <nova:instance xmlns:nova="http://openstack.org/xmlns/libvirt/nova/1.0">
      <nova:package version="15.1.5-1.el7"/>
      <nova:name>test-dirtybitmap-01</nova:name>
      <nova:creationTime>2021-01-04 15:58:07</nova:creationTime>
      <nova:flavor name="cirros-0.3.0 test">
        <nova:memory>500</nova:memory>
        <nova:disk>1</nova:disk>
        <nova:swap>0</nova:swap>
        <nova:ephemeral>0</nova:ephemeral>
        <nova:vcpus>1</nova:vcpus>
      </nova:flavor>
      <nova:owner>
        <nova:user uuid="95df952831614b6f844ee6cb71f7cef9">admin</nova:user>
        <nova:project uuid="0869f5b3b3d3419fb49275e57002eb92">admin</nova:project>
      </nova:owner>
    </nova:instance>
  </metadata>
  <memory unit='KiB'>512000</memory>
  <currentMemory unit='KiB'>512000</currentMemory>
  <vcpu placement='static'>1</vcpu>
  <cputune>
    <shares>1024</shares>
  </cputune>
  <resource>
    <partition>/machine</partition>
  </resource>
  <sysinfo type='smbios'>
    <system>
      <entry name='manufacturer'>RDO</entry>
      <entry name='product'>OpenStack Compute</entry>
      <entry name='version'>15.1.5-1.el7</entry>
      <entry name='serial'>48c77de7-ed1a-4878-9b09-daf6d61f9eeb</entry>
      <entry name='uuid'>77fae30c-dac9-4981-be9e-bb1c48326995</entry>
      <entry name='family'>Virtual Machine</entry>
    </system>
  </sysinfo>
  <os>
    <type arch='x86_64' machine='pc-i440fx-rhel7.6.0'>hvm</type>
    <boot dev='hd'/>
    <smbios mode='sysinfo'/>
  </os>
  <features>
    <acpi/>
    <apic/>
  </features>
  <cpu mode='custom' match='exact' check='full'>
    <model fallback='forbid'>Haswell-noTSX-IBRS</model>
    <vendor>Intel</vendor>
    <topology sockets='1' cores='1' threads='1'/>
    <feature policy='require' name='vme'/>
    <feature policy='require' name='ss'/>
    <feature policy='require' name='f16c'/>
    <feature policy='require' name='rdrand'/>
    <feature policy='require' name='hypervisor'/>
    <feature policy='require' name='arat'/>
    <feature policy='require' name='tsc_adjust'/>
    <feature policy='require' name='md-clear'/>
    <feature policy='require' name='stibp'/>
    <feature policy='require' name='ssbd'/>
    <feature policy='require' name='xsaveopt'/>
    <feature policy='require' name='pdpe1gb'/>
    <feature policy='require' name='abm'/>
    <feature policy='require' name='ibpb'/>
  </cpu>
  <clock offset='utc'>
    <timer name='pit' tickpolicy='delay'/>
    <timer name='rtc' tickpolicy='catchup'/>
    <timer name='hpet' present='no'/>
  </clock>
  <on_poweroff>destroy</on_poweroff>
  <on_reboot>restart</on_reboot>
  <on_crash>destroy</on_crash>
  <devices>
    <emulator>/usr/libexec/qemu-kvm</emulator>
    <disk type='block' device='disk'>
      <driver name='qemu' type='raw' cache='none' io='native'/>
      <source dev='/dev/disk/by-path/ip-127.0.0.1:3260-iscsi-iqn.2010-10.org.openstack:volume-3ed42c9a-76f8-42a4-b8a3-b1acca1aa6dc-lun-0'/>
      <backingStore/>
      <target dev='vda' bus='virtio'/>
      <serial>3ed42c9a-76f8-42a4-b8a3-b1acca1aa6dc</serial>
      <alias name='virtio-disk0'/>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x04' function='0x0'/>
    </disk>
    <controller type='usb' index='0' model='piix3-uhci'>
      <alias name='usb'/>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x01' function='0x2'/>
    </controller>
    <controller type='pci' index='0' model='pci-root'>
      <alias name='pci.0'/>
    </controller>
    <interface type='bridge'>
      <mac address='fa:16:3e:df:07:80'/>
      <source bridge='brqffb80abc-f8'/>
      <target dev='tap08800dc7-3e'/>
      <model type='virtio'/>
      <alias name='net0'/>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x03' function='0x0'/>
    </interface>
    <serial type='pty'>
      <source path='/dev/pts/0'/>
      <log file='/var/lib/nova/instances/77fae30c-dac9-4981-be9e-bb1c48326995/console.log' append='off'/>
      <target type='isa-serial' port='0'>
        <model name='isa-serial'/>
      </target>
      <alias name='serial0'/>
    </serial>
    <console type='pty' tty='/dev/pts/0'>
      <source path='/dev/pts/0'/>
      <log file='/var/lib/nova/instances/77fae30c-dac9-4981-be9e-bb1c48326995/console.log' append='off'/>
      <target type='serial' port='0'/>
      <alias name='serial0'/>
    </console>
    <input type='tablet' bus='usb'>
      <alias name='input0'/>
      <address type='usb' bus='0' port='1'/>
    </input>
    <input type='mouse' bus='ps2'>
      <alias name='input1'/>
    </input>
    <input type='keyboard' bus='ps2'>
      <alias name='input2'/>
    </input>
    <graphics type='vnc' port='5901' autoport='yes' listen='192.168.15.248' keymap='en-us'>
      <listen type='address' address='192.168.15.248'/>
    </graphics>
    <video>
      <model type='cirrus' vram='16384' heads='1' primary='yes'/>
      <alias name='video0'/>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x02' function='0x0'/>
    </video>
    <memballoon model='virtio'>
      <stats period='10'/>
      <alias name='balloon0'/>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x05' function='0x0'/>
    </memballoon>
  </devices>
  <seclabel type='dynamic' model='selinux' relabel='yes'>
    <label>system_u:system_r:svirt_t:s0:c927,c1010</label>
    <imagelabel>system_u:object_r:svirt_image_t:s0:c927,c1010</imagelabel>
  </seclabel>
  <seclabel type='dynamic' model='dac' relabel='yes'>
    <label>+107:+107</label>
    <imagelabel>+107:+107</imagelabel>
  </seclabel>
</domain>


---
02
# 删除bitmap
virsh qemu-monitor-command 77fae30c-dac9-4981-be9e-bb1c48326995 '{ "execute" : "block-dirty-bitmap-remove", "arguments" : { "node" : "drive-virtio-disk0", "name" : "bitmap0" } }'



./serviceMgr -b -c ../conf/test.conf -m 77fae30c-dac9-4981-be9e-bb1c48326995 -g 1 -logId 0 -H 192.168.8.61


/opt/mulang/vmmgr/vendor/fusioncloudSoft
/opt/mulang/vmmgr/vender/fusioncloudSoft
