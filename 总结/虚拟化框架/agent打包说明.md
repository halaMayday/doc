### 01
在linux环境下执行common-virtualization项目根目录下的打包脚本

```
#!/bin/bash

agent_jar=backup-agent.jar
tar_name=backup-agent-$(date +%Y%m%d%H%M%S).tar.gz
src_dir=$(pwd)/access-remote/rpc-server/target
dest_dir=$(pwd)/tmp

\mvn -Dmaven.test.skip=true clean compile package

\mkdir ${dest_dir}

\mv ${src_dir}/${agent_jar} ${dest_dir}
\mv ${src_dir}/lib ${dest_dir}

\pushd ${dest_dir}
\tar -czf ${tar_name} lib ${agent_jar}
\popd

\mv ${dest_dir}/${tar_name} .
\rm -rf ${dest_dir}
```

### 02
![](assets/agent打包说明-441b1046.gz)

然后把common-virtualization里打出来的包解压得到的东西 放去这个deploy解压出来的根目录下 然后执行里面的package.sh

### 03：

把这个压缩包放去你要部署的节点上，解压 然后执行install.sh。



```

#!/bin/sh

PACKAGE_DIR="backup-agent"
AGENt_DIR="mlcloud"
PACKAGE_TIME=$(date +%Y%m%d%H%M%S)

rm -rf *.tar.gz
rm -rf ${PACKAGE_DIR}
mkdir -p ${PACKAGE_DIR}/${AGENt_DIR}

cp -r lib ${PACKAGE_DIR}/${AGENt_DIR}
cp -f backup-agent.jar ${PACKAGE_DIR}/${AGENt_DIR}
(cd ${PACKAGE_DIR}/${AGENt_DIR} && ln -s backup-agent.jar backupmgr.jar)

cp -r script-and-jre/* ${PACKAGE_DIR}/${AGENt_DIR}
cp -f *install.sh ${PACKAGE_DIR}

tar czf ${PACKAGE_DIR}-${PACKAGE_TIME}.tar.gz ${PACKAGE_DIR}
rm -rf ${PACKAGE_DIR}


```
