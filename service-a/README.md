#远端启动容器postgresql
docker pull docker.io/postgres:9.6
docker pull docker.io/ssuareza/postgresql-client

mkdir dockerAPP 
mkdir dockerDB
chmod 777 dockerAPP -R
chmod 777 dockerDB -R

docker run -d \
    --name pg-master \
    -e POSTGRES_PASSWORD=123456 \
    -e POSTGRESQL_USER=dbuser -e POSTGRESQL_PASSWORD=123456 -e POSTGRESQL_DATABASE=demo -p 5432:5432 -p 7777:7777 -p 7654:7654 \
    -e PGDATA=/var/lib/postgresql/data/pgdata \
    -v /home/dockerDB:/var/lib/postgresql/data \
    docker.io/centos/postgresql-96-centos7

#运行程序
java -jar service-a\target\service-a-1.0-SNAPSHOT.jar --spring.profiles.active=dev

http://localhost:8090/user/list

# k8s环境内测试
chmod +x sealos && mv sealos /usr/bin

sealos init --passwd 'qwe123ASD#@!' \
    --master 172.17.162.235  \
    --node 172.17.162.234 \
    --pkg-url /root/kube1.17.7.tar.gz \
    --version v1.17.7

#docker build
将service-a/Dockerfile上传至服务器/root/docker/app目录下
将service-a-1.0-SNAPSHOT.jar上传至/root/docker/app目录下

docker build -t riil/service-a:1.0 /root/docker/app

#编译过程中出现如下问题的解决方案
ERROR: http://dl-cdn.alpinelinux.org/alpine/v3.10/main: temporary error (try again later)
WARNING: Ignoring APKINDEX.00740ba1.tar.gz: No such file or directory
ERROR: http://dl-cdn.alpinelinux.org/alpine/v3.10/community: temporary error (try again later)
WARNING: Ignoring APKINDEX.d8b2a6f4.tar.gz: No such file or directory

vi /etc/default/docker
DOCKER_OPTS="--dns 114.114.114.114"
systemctl restart docker

#运行容器
docker run --name service-a -d -e PG_HOST=212.64.90.199 \
 -e PG_USER=dbuser \
 -e PG_PASSWORD=123456 \
 -e "SPRING_PROFILES_ACTIVE=docker" \
 -p 8090:8090 \
 riil/service-a:1.0
 
http://172.17.162.235:8080/user/list

#导出镜像
docker save -o service-a-1.0.tar riil/service-a
docker save -o postgres.tar docker.io/postgres
docker save -o postgresql-client.tar docker.io/ssuareza/postgresql-client

# 将镜像传输到worker节点
scp postgres.tar root@172.17.162.234:/root/postgres.tar
scp service-a-1.0.tar root@172.17.162.234:/root/service-a-1.0.tar
scp postgresql-client.tar root@172.17.162.234:/root/postgresql-client.tar

#导入镜像
ssh 172.17.162.234
docker load -i service-a-1.0.tar
docker load -i postgres.tar
docker load -i postgresql-client.tar

#helm
mkdir /root/chart/service-a
#将charts目录的内容拷贝到master上
tar -zxvf helm-v3.4.2-linux-amd64.tar.gz
mv linux-amd64/helm /usr/local/bin/helm
#check语法
helm lint service-a
helm lint postgres

helm install postgres postgres 
    
helm install service-a service-a --set PG_USER=dbuser

helm upgrade postgres postgres 
helm upgrade service-a service-a

helm uninstall service-a

#数据初始化
#将db目录拷贝到master上，并拷贝到pg-master容器中
kubectl get po -A
kubectl cp /root/db pg-master-78b8887d46-7p5bc:/tmp/ -n base-service
kubectl exec -ti pg-master-78b8887d46-7p5bc -n base-service bash

chmod a+x /tmp/db/*.sh
./tmp/db/init_db.sh

#访问web服务
curl http://172.17.162.235:30101/user/list


