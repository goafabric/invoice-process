![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

# docker compose
go to /src/deploy/docker and do "./stack up" or "./stack up -native"

# run jvm multi image
docker run --pull always --name invoice-process --rm -p50900:50900 goafabric/invoice-process:1.0.0-SNAPSHOT

# run native image
docker run --pull always --name invoice-process-native --rm -p50900:50900 goafabric/invoice-process-native:1.0.0-SNAPSHOT -Xmx32m

# run native image arm
docker run --pull always --name invoice-process-native --rm -p50900:50900 goafabric/invoice-process-native-arm64v8:1.0.0-SNAPSHOT -Xmx32m

# loki logger
docker run --pull always --name invoice-process --rm -p50900:50900 --log-driver=loki --log-opt loki-url="http://host.docker.internal:3100/loki/api/v1/push" goafabric/invoice-process:1.0.0-SNAPSHOT

