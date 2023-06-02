# define base docker image

FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ./target/bookstore-0.0.1-SNAPSHOT.jar bookstore.jar
ENTRYPOINT ["java","-jar","bookstore.jar"]
CMD java -jar bookstore-0.0.1-SNAPSHOT.jar