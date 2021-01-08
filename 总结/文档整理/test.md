2020-12-12 20:40:11 [http-nio-8598-exec-8] INFO  c.m.r.server.aspect.ControllerAspect -> enterController 29 - 调用: exportImageDiff
2020-12-12 20:40:11 [http-nio-8598-exec-8] INFO  c.m.r.server.aspect.ControllerAspect -> lambda$enterController$1 31 - 请求体: {"configFile":"/etc/ceph/ceph.conf","full":true,"imageSpec":"/dev/vda/volumes-23a0cd4a-5925-4f82-89ac-a3049f81d0f0","snapshot":"snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426","userId":"fusioncloud"}
2020-12-12 20:40:11 [http-nio-8598-exec-8] INFO  com.mlcloud.local.util.CliUtil -> doExcuteCommand 22 - EXECUTE COMMAND [rbd, diff, --id, fusioncloud, /dev/vda/volumes-23a0cd4a-5925-4f82-89ac-a3049f81d0f0@snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426]



2020-12-12 20:40:11 [http-nio-8598-exec-8] INFO  c.m.r.server.aspect.ControllerAspect -> enterController 29 - 调用: exportImageDiff
2020-12-12 20:40:11 [http-nio-8598-exec-8] INFO  c.m.r.server.aspect.ControllerAspect -> lambda$enterController$1 31 - 请求体: {"configFile":"/etc/ceph/ceph.conf","full":true,"imageSpec":"/dev/vda/volumes-23a0cd4a-5925-4f82-89ac-a3049f81d0f0","snapshot":"snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426","userId":"fusioncloud"}
2020-12-12 20:40:11 [http-nio-8598-exec-8] INFO  com.mlcloud.local.util.CliUtil -> doExcuteCommand 22 - EXECUTE COMMAND [rbd, diff, --id, fusioncloud, /dev/vda/volumes-23a0cd4a-5925-4f82-89ac-a3049f81d0f0@snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426]


"imageSpec":"/dev/vda/volumes-23a0cd4a-5925-4f82-89ac-a3049f81d0f0","snapshot":"snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426"

rbd diff --id fusioncloud  /dev/vda/volumes-23a0cd4a-5925-4f82-89ac-a3049f81d0f0snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426


rbd diff --id fusioncloud  【镜像，或者块的名字】


rbd diff volumes/volumes-23a0cd4a-5925-4f82-89ac-a3049f81d0f0snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426

rbd diff  volume-23a0cd4a-5925-4f82-89ac-a3049f81d0f0@snapshot-66f4303b-8dd7-4524-9eb1-58c361d5d426
### 3
aqsd asd
#### 4 sa as
