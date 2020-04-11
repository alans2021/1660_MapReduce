FROM maven:3.6.3-jdk-8
COPY . .
WORKDIR /
CMD ["mvn", "package", "exec:java"]