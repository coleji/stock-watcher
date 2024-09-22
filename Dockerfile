FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY target/stock-watcher_0.1.0_all.deb /app/
RUN dpkg -i /app/stock-watcher_0.1.0_all.deb
CMD cd /usr/share/stock-watcher && \
mkdir -p conf/private && \
cp /mnt/conf/server-properties conf/private/server-properties && \
touch /usr/share/stock-watcher/RUNNING_PID && \
rm /usr/share/stock-watcher/RUNNING_PID && \
exec stock-watcher \
  -J-Djava.security.egd=file:/dev/./urandom \
  -Dplay.http.secret.key=$PLAY_SECRET \
  -Dconfig.resource=conf/application.conf
