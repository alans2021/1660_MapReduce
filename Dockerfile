FROM maven:3.6.3-jdk-8
COPY src /home/app/src
COPY Data /home/app/data
COPY pom.xml /home/app
COPY HadoopMapReduceProject-f80320d9587b.json /home/app
ENV GOOGLE_APPLICATION_CREDENTIALS /home/app/HadoopMapReduceProject-f80320d9587b.json
RUN apt-get update && apt-get install libxtst6 -y && apt-get install libfontconfig1 libxrender1 -y
WORKDIR /home/app

CMD ["mvn", "package", "exec:java"]