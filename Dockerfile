FROM openjdk:17
WORKDIR /opt/workdir

#This is needed due to ZScaler agent on the workstation
ADD zscaler.cer /opt/custom/certs/zscaler.cer
RUN $JAVA_HOME/bin/keytool -import -file /opt/custom/certs/zscaler.cer -alias zscaler -cacerts -trustcacerts -storepass changeit -noprompt

COPY build/libs/funwithflags-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "./funwithflags-0.0.1-SNAPSHOT.jar"]