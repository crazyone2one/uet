FROM openjdk:8-jdk-alpine
ENV TZ Asia/Shanghai
VOLUME /tmp
EXPOSE 8088
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]