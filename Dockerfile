FROM gradle:jdk17 as builder

COPY . /opt/build/
WORKDIR /opt/build/

RUN mkdir -p /opt/app/ \
 && ./gradlew clean bootJar --rerun-tasks \
 && mv /opt/build/build/libs/*.jar /opt/app/app.jar


FROM openjdk:17-oracle

COPY --from=builder /opt/app /opt/app

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
