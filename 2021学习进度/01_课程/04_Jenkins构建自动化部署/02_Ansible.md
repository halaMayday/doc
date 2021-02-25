#### 1.Ansible介绍

Ansible是一个开源部署工具

开发语言：Python

特点：SSH协议通讯，全平台，无需编译，模块化部署管理

#### 2.Ansible/Chef/Saltstack的区别？

| 工具      | 说明                                                         |
| --------- | ------------------------------------------------------------ |
| Chef      | Ruby语言编写，C/S架构，配置需要Git依赖，Recipe脚本编写规范，需要编程经验 |
| Saltstack | Python语言编写，C/S架构，模块化配置管理，YMAL脚本编写规范，适合大规模集群部署 |
| Ansible   | Python脚本编写，无Client，模块化配置管理，Playbook脚本编写规范，易于上手，适合中小规模快速部署 |

####3.Ansible的优点和应用场景

①Agentless：轻量级别，无客户端

②free：开源免费，学习成本低

③使用playbook作为核心配置架构，统一的脚本格式批量化部署

④完善的模块化扩展，支持目前主流的开发场景

⑤强大的稳定性和兼容性，不需要安装其他的工具

⑥活跃的社区

#### 3. Ansible配合virtualenv 安装配置

环境：Python3.6+Ansible2.5

##### 3.1 Ansible的两种安装模式（centos7）

```
1.yum包管理安装 如果有安装其他python的话，有冲突
# yum -y install ansible 
2.推荐Git 源代码安装,保证在一个独立的python独立环境中运作
# git clone https://github.com/ansible/ansible.git 
```

##### 3.2 virtualenv 安装配置

```
0.关闭firewalld防火墙，并关闭SELINUX并重启系统
# systemctl stop firewalld
# systemctl disable firewalld
# vi /etc/sysconfig/selinux
设置 SELINUX = disabled
# reboot 
1.安装python3.6 版本
	参照这个：https://www.cnblogs.com/kimyeee/p/7250560.html
	
	yum install -y gcc
	
	wget http://www.python.org/ftp/python/3.6.5/Python-3.6.5.tar.xz
	tar xf Python-3.6.5.tar.xz
  cd Python-3.6.5
  ./configure --prefix=/usr/local --with-ensurepip=install --enable-shared LDFLAGS="-wl,-rpath /usr/loacl/lib"
  ln -s /usr/local/bin/pip3.6 /usr/local/bin/pip
2.安装virtualenv
	# pip3 install virtualenv 
	pip install virtualenv 
3.创建Ansible账户并安装python3.6版本的virtualenv实例
	# useradd deploy && su - deploy
	# virtualenv -p /usr/local/bin/python3.6 .py3-a2.5-env
4.git 源代码安装ansible2.5
	# cd /home/deploy/.py3-a2.5-env
	# git clone https://github.com/ansible/ansible.git
	# cd ansible && git checkout stable-2.5
5.加载python3.6 virtualenv环境
	# source /home/deploy/.py3-a2.5-env/bin/activate
6.安装ansible依赖包
 # sudo pip3 install paramiko PyYAML jinja2
 sudo pip3 install    --target=/usr/local/python-3.6/lib/python3.6/site-packages paramiko PyYAML jinja2  --upgrade
 
  sudo pip3 install    --target=/usr/local/python-3.6/lib/python3.6/site-packages paramiko  paramiko PyYAML jinja2 
  
   sudo mv ansible-stable-2.5 .py3-a2.5-env/
   
   pip  install paramiko PyYAML jinja2
 
 	 mv ansible .py3-a2.5-env/
 	 
 	 
7.在pyhon3.6的环境下加载ansible
 cd .py3-a2.5-env/ansible-stable-2.5
# source /home/deploy/.py3-a2.5-env/ansible/hacking/env-setup -q
 
8 验证ansible2.5
# ansible --version

9.配置ansible主机和目标部署主机SSH免密钥登录
	Ansible服务器端创建SSH本地密钥
	# ssh-keygen -t rsa
  # ssh-copy-id -i /home/deploy/.ssh/id_rsa.pub root@192.168.8.61
  
10. 验证ansible playbook的版本
	# source .py3-a2.5-env/bin/activate
	# ansible-playbook --version
 
 11 
 ssh root@192.168.14.144
 useradd foo
 useradd deploy 
 rpm -Uvh http://nginx.org/packages/centos7/noarch/RPMS/nginx-release-centos-7-0.el7.ngx.noarch.rpm 
 
 12 ansible 的执行
 ansible-playbook -i inventory/testenv ./deploy.yml
 
```

#### 4.Ansible playbooks入门和编写规范

##### 4.1 Playbooks 框架和列表

①以Test Playbooks 举例说明下目录结构

> Inventory/																	:Server的详细清单目录
>
> > testenv														  ：具体清单与变量声明文件
>
> roles/																		   ：roles任务列表
>
> > testbox/							                             ：testbox 详细任务
> >
> > > tasks/											 
> > >
> > > > Main.yml		                    ： testbox主任务文件					
>
> deploy.yml                                                                    ： Playbook任务入口文件

②详细目录 testenv

```
[testservers]         # Server组列表 
192.168.8.61			# 目标部署服务器主机名

[testservers:vars]    # 参数列表
server_name=192.168.8.61
user=root
output=/root/test.txt
```

③主任务文件main.yml

```
- name: Print server name and user to remote testbox                                      # 任务名称
  shell: "echo 'Currently {{ user }} is logining {{ server_name }}' > {{ output }}"				# shell 脚本执行命令
```

④任务入口文件deploy.yml

```
- hosts: "testservers"
  gather_facts: true
  remote_user: root
  roles:
    - testbox
```

⑤ 执行Playbooks

 eg：部署到testenv环境

```
ansible -playbook - i inventory/testenv ./deploy.yml
```

