FROM openjdk:21


COPY ./ ./

RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

EXPOSE 8085

CMD ./mvnw spring-boot:run