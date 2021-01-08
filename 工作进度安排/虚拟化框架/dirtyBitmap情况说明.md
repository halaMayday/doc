### 1:
todo ：
检查备份的服务器是不是上次的。如果不是，则设置为全量备份。

这块的检查需要优化，加上：

在服务端接收备份请求之后，会比对本地备份服务器的IP与上一次是否一致，如果不一致则进行全量备份。

这个检查主要是为了防止两个及以上备份服务器同时对一台KVM主机进行备份的造成数据问题的发生。

checkBackupServerConsistence


这块的domainIp是不是可以记录到世代信息中？或者同个目录的其他文件中？



### 2:
String instanceId = param.getInstanceId();
//        domainState = LocalAccess.virsh().getDomainState(instanceId);
//        logger.info("domain {} state :{}", instanceId, domainState.toString())


开始写这里：
driveBackupService.backupDrive

选择备份的方式



kvm备份编写工作
1:feat：增加同时支持备份运行状态下的机器，和关闭状态下的机器。
2:fix：修改了查询备份任务是否结束的实现。
  备份任务超时等待时间现在有两种设置方式：
    原来dirtybitmap设置方式为 虚拟磁盘大小/预估备份速度。
    神软项目采用经验公示，根据磁盘大小，设置最大超时时间。
这块准备在后面的测试中，多次验证下，选择更合适的方式。

3:feat：增加了 开启监听备份任务，停止监听备份任务
4:feat：kvm备份恢复（尚未全部完成）
5:feat：增加失败回滚的逻辑，删除bitmap，卸除挂载的nfs。


1:继续完成kvm恢复的编写。
2:通过恢复，去验证备份的数据的有效性。



* get domain disk info, only can be used when vm is running
    * virsh qemu-monitor-command domain --pretty '{ "execute": "query-block" }'
