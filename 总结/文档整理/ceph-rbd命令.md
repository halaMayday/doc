#### 1.列出有哪些池子
```
[root@node1 ceph]# ceph osd lspools
0 rbd,5 images,6 vms,7 volumes,9 backups,12 testpool,13 hftest,

```
#### 2.列出池子下面的镜像
```
[root@node1 ceph]# rbd ls -l -p volumes
```

#### 3.建立一个镜像

```
# rbd create  <poolname/imagename> --size 10
```

```
-s <镜像大小> #镜像大小的单位是MB,–image-format 一般用2，否则这个镜像不能克隆
```
eg:
```
rbd create hftest/test4image  --size 10
```
创建成功

<img alt="ceph-rbd命令-c23a6015.png" src="assets/ceph-rbd命令-c23a6015.png" width="" height="" >

#### 4. 查看一个镜像的信息
```
rbd info hftest/test4image
```
<img alt="ceph-rbd命令-1fc6788a.png" src="assets/ceph-rbd命令-1fc6788a.png" width="" height="" >

#### 5.修改一个镜像的大小

```
rbd resize <poolname/imagename> -s <镜像大小>
rbd resize hftest/test4image -s 20
```
重新查看该镜像的信息，发现已经修改成功，大小为20m
<img alt="ceph-rbd命令-ce63d839.png" src="assets/ceph-rbd命令-ce63d839.png" width="" height="" >


#### 6.复制一个镜像

```
rbd cp <srcpoolname/srcimagename> <destpoolname/destimagename>

rbd cp hftest/test4image  rbd/copy4test
```
复制成功后然后查看结果  
<img alt="ceph-rbd命令-a1719e5a.png" src="assets/ceph-rbd命令-a1719e5a.png" width="" height="" >

#### 7.删除一个镜像
```
rbd rm <poolname/imagename>

rbd rm rbd/copy4test
```

#### 8. 导入一个镜像

```
这个命令可以用来将保存在本地硬盘里的一个文件导入到 ceph 中
rbd import –image-format 2 <srcpath> <destpoolname/destimagename>

rbd import -image-format 2  todo

```

#### 9. 导出一个镜像
这个命令可以用来将保存在ceph中的镜像导出到一个本地文件
```
rbd export <srcpoolname/srcimagename> <destpath>
```

#### 10.克隆硬盘

```
rbd clone <poolname/imagename@snapshotname> <newpoolname/imagename>
```
#### 11. 建立快照
如 rbd snap create testpool/testimg@snap1 表示给 testpool/testimg 这个镜像建立一个名叫 snap1 的快照

rbd snap create <poolname/imagename@snapshotname>

#### 12.快照 保护/去掉快照

```
# rbd snap protect <poolname/imagename@snapshotname>
保护快照，只有在保护状态下的快照才可以用来克隆出新的镜像

# rbd snap unprotect <poolname/imagename@snapshotname>

rbd snap unprotect volumes/volume-23a0cd4a-5925-4f82-89ac-a3049f81d0f0@snapshot-9f1bf2c1-d0fd-464a-a7ad-d3c752461b6f
```

[root@node1 ~]# rbd snap ls volumes/volume-23a0cd4a-5925-4f82-89ac-a3049f81d0f0
SNAPID NAME                                             SIZE
  1842 snapshot-9f1bf2c1-d0fd-464a-a7ad-d3c752461b6f 1024 MB
  1844 snapshot-ec6873de-02ad-45fe-9aff-903203295625 1024 MB
  1846 snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426 1024 MB
  1848 snapshot-f99950aa-a926-484b-a1ff-131e44d007ef 1024 MB
  1850 snapshot-1e971be8-6881-4e42-ab87-e792b6b5907c 1024 MB


#### 13.删除一个快照

```
# rbd snap rm <poolname/imagename@snapshotname>

rbd snap rm volumes/volume-23a0cd4a-5925-4f82-89ac-a3049f81d0f0@snapshot-9f1bf2c1-d0fd-464a-a7ad-d3c752461b6f
```

#### 14.回滚一个镜像
rbd snap rollback <poolname/imagename@snapshotname>


#### 15.将一个镜像的全部快照删除

```
rbd snap purge <poolname/imagename>
rbd snap purge volumes/volume-23a0cd4a-5925-4f82-89ac-a3049f81d0f0
```
哦豁，失败了，原因参照12条  
<img alt="ceph-rbd命令-327f9e0d.png" src="assets/ceph-rbd命令-327f9e0d.png" width="" height="" >

#### 16.列出某个镜像有哪些快照
```
# rbd snap ls <poolname/imagename>
rbd snap ls volumes/volume-23a0cd4a-5925-4f82-89ac-a3049f81d0f0
```
<img alt="ceph-rbd命令-79744b51.png" src="assets/ceph-rbd命令-79744b51.png" width="" height="" >

#### 17.格式转换
```
qemu-img convert -f vpc tedt.vhd -O raw rbd:<VM名字>/disk1
```


#### 18.拍平克隆硬盘，重新copy
```
# rbd flatten
```

#### 查看rbd实际占用大小
```
rbd diff poolname/rbdimg | awk '{ SUM += $2 } END { print SUM/1024/1024 " MB" }'
```
