### Mac下安装使用Jmeter

#### 1 安装

>>>1.1 安装包地址

推荐官网下载：http://jmeter.apache.org/download_jmeter.cgi

  ·Binaries：二进制版本，可直接使用；  
  ·ource：源代码版，好像需要自己编译。  
  我使用的Binaries版本

>>>1.2 终端操作  
  解压    

  ```
  tar zxvf apache-jmeter-5.2.1.tgz
  ```

  配置环境变量

  ```
  vi ~/.bash_profile
  //设置环境变量
  export JMETER_HOME=/Users/nuc/Downloads/apache-jmeter-5.4

  PATH=${PATH}:${JAVA_HOME}/bin:${MAVEN_HOME}/bin:$JMETER_HOME/bin
  export PATH

  //配置生效
  source ~/.bash_profile

  //修改中文
  vi /Users/nuc/Downloads/apache-jmeter-5.4/bin/jmeter.properties
  language=zh_CN
  ```
