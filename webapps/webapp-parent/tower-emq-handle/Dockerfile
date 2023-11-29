FROM frolvlad/alpine-java:jre8-slim

#配置时区、JVM参数、启动参数等
ENV TZ=Asia/Shanghai \
        JAVA_OPTS="" \
        PARAMS="" \
        jarName=admin

#配置时区、阿里云下载源、添加时区（apk add 下载失败时 重启 systemctl daemon-reload 和 systemctl restart docker 即可解决）
RUN echo "http://mirrors.aliyun.com/alpine/v3.4/main/" > /etc/apk/repositories \
    && apk add tzdata zeromq \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo '$TZ' > /etc/timezone

#挂载容器目录到宿主机匿名目录下（直接写入宿主机匿名目录下，映射到容器中）
VOLUME /data

#复制jar包
COPY /target/*.jar ${jarName}.jar

#启动参数   -e JAVA_OPTS="" -e PARAMS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Duser.timezone=GMT+8 -Djava.security.egd=file:/dev/./urandom -jar /${jarName}.jar $PARAMS"]