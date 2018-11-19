FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS=""
ENV APP_OPTS=""
ENV profile=""
ENV  TIME_ZONE Asiz/Shanghai
RUN \
 && apk add --no-cache tzdata \
 && echo "${TIME_ZONE}" > /etc/timezone \
 && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
 && apk del tzdata \
ENTRYPOINT [ "sh", "-c", "java -Dspring.profiles.active=$profile $JAVA_OPTS $APP_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]