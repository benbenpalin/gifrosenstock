FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/gifrosenstock.jar /gifrosenstock/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/gifrosenstock/app.jar"]
