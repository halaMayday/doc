#### 问题：
<```
[root@TOS-1517 ~]# virsh qemu-monitor-command GUS26b321e6-b43d-47da-92f4-84b4c82723d9 --pretty '{ "execute": "query-block" }' | jq .return[0] | sed -n '/dirty\-bitmaps/,/]/p'
-bash: jq: command not found

```

#### 解决办法：

参考：
https://www.cnblogs.com/CaptainLin/p/7064520.html
