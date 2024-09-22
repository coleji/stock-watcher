sbt debian:packageBin
docker build -t docker-snapshots.madison.lan:5504/stock-watcher .
docker push docker-snapshots.madison.lan:5504/stock-watcher