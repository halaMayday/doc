#!/bin/bash
# deploy directory setting, current directory by default
deploy_dir=/opt/mulang/vmmgr/vendor/fusioncloudSoft;

# maven package
\mvn -Dmaven.test.skip=true clean compile package;
[[ $? -ne 0 ]] && exit 2;

# temporary directory
tmp_dir=$(pwd)/fusioncloudSoft;
[[ ! -d ${tmp_dir} ]] && \mkdir ${tmp_dir};

# prepare package content
jar_name=serviceMgr.jar
service_dir=./fusioncloud-service;
\pushd ${service_dir};
\mv ./target/lib ${tmp_dir}/;
\mv ./target/${jar_name} ${tmp_dir}/;
\cp -r ./src/conf ${tmp_dir}/;
\popd;

# create invocation script
exe_name=serviceMgr
\pushd ${tmp_dir};
\mkdir bin;
\echo java -ea -server -jar -Djava.security.egd=file:/dev/urandom -Dlogback.configurationFile=${deploy_dir}/conf/logback.xml  -Ddefault.config=${deploy_dir}/conf/default.conf ${deploy_dir}/${jar_name} '${1+"$@"}' >./bin/${exe_name};
\chmod 775 ./bin/${exe_name};
\popd;

###压缩包名字
tar_name=${exe_name}-$(date +%Y%m%d%H%M%S)

# package
\tar -c -z -f ${tar_name}.tar.gz fusioncloudSoft;

#clean
\mvn clean;
\rm -r -f ${tmp_dir};

#解压目录
un_tar_name=${deploy_dir}/fusioncloudSoft

if [ ! -f ${un_tar_name} ];then
  echo "文件不存在"
else
  rm -f ${un_tar_name}
fi
tar -zxvf ${tar_name}.tar.gz  -C ${deploy_dir}




