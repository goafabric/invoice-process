![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

# docker compose
go to /src/deploy/docker and do "./stack up" or "./stack up -native"

# run jvm multi image
docker run --pull always --name invoice-process --rm -p50900:50900 goafabric/invoice-process:$(grep '^version=' gradle.properties | cut -d'=' -f2)

# run native image
docker run --pull always --name invoice-process-native --rm -p50900:50900 goafabric/invoice-process-native:$(grep '^version=' gradle.properties | cut -d'=' -f2) -Xmx32m

# run native image arm
docker run --pull always --name invoice-process-native --rm -p50900:50900 goafabric/invoice-process-native-arm64v8:$(grep '^version=' gradle.properties | cut -d'=' -f2) -Xmx32m
