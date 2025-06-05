# docker compose
go to /src/deploy/docker and do "./stack up" or "./stack up -native"

# run jvm multi image
docker run --pull always --name invoice-process --rm -p50500:50500 goafabric/invoice-process:$(grep '^version=' gradle.properties | cut -d'=' -f2)

# run native image
docker run --pull always --name invoice-process-native --rm -p50500:50500 goafabric/invoice-process-native:$(grep '^version=' gradle.properties | cut -d'=' -f2) -Xmx32m
