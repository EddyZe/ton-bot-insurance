
FROM circleci/node

FROM openjdk:21

COPY ./ ./

RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

EXPOSE 8086

CMD ./mvnw spring-boot:run