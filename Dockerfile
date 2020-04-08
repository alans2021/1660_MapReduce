FROM openjdk:7
COPY ./src /src
COPY ./Data /data
WORKDIR /src
RUN javac gui_driver.java
CMD ["java", "gui_driver"]