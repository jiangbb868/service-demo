FROM alpine:3.10
ENV PG_HOST "127.0.0.1"
ENV BASE_NAMESPACE "base-service"
ENV PG_USER "root"
ENV PG_PASSWORD "root"

ADD service-a-1.0-SNAPSHOT.jar /opt/app/

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
    && apk update \
    && apk add openjdk8 busybox tzdata curl \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtim \
    && echo Asia/Shanghai > /etc/timezone \
    && apk del tzdata \
    && rm -rf /tmp/* /var/cache/apk/*

CMD ["java", "-jar", "/opt/app/service-a-1.0-SNAPSHOT.jar"]
