Jenkins

####  1.什么是jenkins？

​	Jenkins是一个开源持续集成工具

​	开发工具：java

​	功能：提供了软件开发的持续集成服务

​	特点：支持主流软件配置管理，配合实现软件配置管理，持续集成功能。

#### 2. Jenkins的优势和应用场景

​	主流的运维平台，兼容所有的主流开发环境。

​	 插件市场可与海量业内主流开发工具实现集成。

 	 Job为配置单位与日志管理 ，使得运维和开发人员能协同工作

​      权限管理划分不同Job不同角色

​	  强大的负载均衡功能，保证我们项目的可靠性

#### 3. Jenkins安装配置管理 

​		

​	**准备工作**
​	1.添加yum仓库源	

```
# wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
# rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
```

​	2.保证系统java版本为8.0或者8.0以上

```
# yum -y install java
# java -version
```

​	3.关闭系统防火墙

```
	# systemctl stop firewalld
	# systemctl disable firewalld
```

​	4.关闭SELINUX并重启系统

```
# vi /etc/sysconfig/selinux
-- 设置 SELINUX=disabled
# reboot
```

**Jenkins 安装与初始化配置**

​	1.Yum源安装jenkins最新版本

```
yum install jenkins -y
```

​	2.创建Jenkins系统用户

```
useradd deploy
```

​	3.更改jenkins启动用户与端口

```
	# vi /etc/sysconfig/jenkins

​		...
​			JENKINS_USER=deploy
​			JENKINS_PORT=8080
```

​	4.更改jenkins的宿主和宿主权限

​	

```
# chown -R deploy:deploy /var/lib/jenkins
# chown -R deploy:deploy /var/log/jenkins
```

​	5.启动jenkins 

```
systemctl start jenkins
```

6. 确认jenkins服务正常启动

   ```
   lsof -i 8080
   ```

7 jenkins的前台web页面安装

​	登录web页面,解锁jenkins，按照页面的提示复制管理员密码即可

8.安装系统推荐插件

9.创建第一个管理员用户

10. Jenkins Job的构建

    freestyle job：

    ​	页面需要添加模块配置项和参数去完成配置

    ​	每个freestyle job仅能实现一个开发功能

    ​	无法将配置代码化，不利于job配置迁移与版本控制

    ​	逻辑相对简单，无需额外学习成本

    

    pipeline job：

    ​	所有模块，参数配置都可以体现为一个pipeline脚本

    ​	可以定义多个stage构建一个管道工具集

    ​	所有配置代码化，方便job配置迁移与版本控制

    ​	需要pipeline 脚本语法基础

    ​	

11.  jenkins job构建配置

    1. 环境准备

       1. 配置jenkins server本地gitlab DNS

       2. 安装git client，curl工具依赖

          ```
          # yum install git curl -y
          ```

       3. 关闭系统 Git https.sslVerify 安全认证

       4. 添加Jenkins后台 Git client user和email

       5. 添加jenkins后台Git Credential凭据

    2. 

12. xxxx

13. ssss

​	

