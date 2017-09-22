FROM openjdk:8-jdk-alpine
VOLUME /tmp
# ADD ../../build/libs/*.jar app.jar
ADD ./build/libs/ /app/
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/*.jar" ]
