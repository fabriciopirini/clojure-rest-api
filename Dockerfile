FROM openjdk:8-jre-alpine
ADD target/uberjar/robot-vs-dino.jar /usr/src/
EXPOSE 3000
CMD java -jar /usr/src/robot-vs-dino.jar
