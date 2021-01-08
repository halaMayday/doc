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
\echo java -ea -server -jar  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 -Djava.security.egd=file:/dev/urandom -Dlogback.configurationFile=${deploy_dir}/conf/logback.xml  -Ddefault.config=${deploy_dir}/conf/default.conf ${deploy_dir}/${jar_name} '${1+"$@"}' >./bin/${exe_name};
\chmod 775 ./bin/${exe_name};
\popd;

# package
\tar -c -z -f ${exe_name}-$(date +%Y%m%d%H%M%S).tar.gz fusioncloudSoft;

#clean
\mvn clean;
\rm -r -f ${tmp_dir};
