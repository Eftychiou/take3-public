FROM openjdk:11

COPY target/rt-server.jar /rt-server.jar

ENTRYPOINT ["java","-jar","rt-server.jar"]



