FROM registry.steam.crcloud.com/tools/javabase:0.8.1
COPY app.jar /steam-iam.jar
ENTRYPOINT exec java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap $JAVA_OPTS  -jar /steam-iam.jar
