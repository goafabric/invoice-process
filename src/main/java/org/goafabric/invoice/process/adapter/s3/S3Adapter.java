package org.goafabric.invoice.process.adapter.s3;

import am.ik.s3.ListBucketResult;
import am.ik.s3.ListBucketsResult;
import am.ik.s3.S3Content;
import am.ik.s3.S3RequestBuilders;
import org.goafabric.invoice.controller.extensions.UserContext;
import org.goafabric.invoice.process.adapter.s3.dto.ObjectEntry;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;

import static am.ik.s3.S3RequestBuilder.s3Request;

@Component
@RegisterReflectionForBinding({ListBucketResult.class, ListBucketsResult.class})
public class S3Adapter {

    private final String     schemaPrefix;
    private final RestClient restClient;
    private final String     endPoint;
    private final String     region;
    private final String     accessKey;
    private final String     secretKey;

    public S3Adapter(@Value("${multi-tenancy.schema-prefix:}") String schemaPrefix,
                     @Value("${spring.cloud.aws.s3.endpoint:}") String endPoint,
                     @Value("${spring.cloud.aws.region.static}") String region,
                     @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
                     @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey) {
        this.schemaPrefix = schemaPrefix;
        this.endPoint = endPoint;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.restClient = RestClient.builder().messageConverters(httpMessageConverters -> httpMessageConverters.add(new MappingJackson2XmlHttpMessageConverter())).build();
    }

    public ObjectEntry getById(String id) {
        var request = s3RequestPath(HttpMethod.GET, id).build();
        var response = restClient.get().uri(request.uri()).headers(request.headers()).retrieve().toEntity(byte[].class);
        if (response.getBody() == null) {
            throw new IllegalStateException("S3 Client Body is null");
        }
        return new ObjectEntry(id, response.getHeaders().getFirst("Content-Type"), (long) response.getBody().length, response.getBody());
    }

    public void save(ObjectEntry objectEntry) {
        createBucketIfNotExists(getBucketName());

        var request = s3RequestPath(HttpMethod.PUT, objectEntry.objectName())
                .content(S3Content.of(objectEntry.data(), MediaType.valueOf(objectEntry.contentType())))
                .build();

        restClient.put().uri(request.uri())
                .headers(request.headers())
                .body(objectEntry.data())
                .retrieve().toBodilessEntity();
    }

    private void createBucketIfNotExists(String bucket) {
        var request = s3Path(HttpMethod.GET).path(b -> b).build();
        var response = restClient.get().uri(request.uri()).headers(request.headers()).retrieve()
                .toEntity(ListBucketsResult.class).getBody();

        if (response != null && response.buckets().stream().noneMatch(b -> b.name().equals(bucket))) { //this could be slow
            var request2 = s3RequestPath(HttpMethod.PUT, null).build();
            restClient.put().uri(request2.uri()).headers(request2.headers()).retrieve().toBodilessEntity();
        }
    }

    private S3RequestBuilders.Optionals s3RequestPath(HttpMethod httpMethod, String key) {
        return s3Path(httpMethod).path(b -> b.bucket(getBucketName()).key(key));
    }

    private S3RequestBuilders.Path s3Path(HttpMethod httpMethod) {
        try {
            return s3Request().endpoint(new URI(endPoint))
                    .region(region)
                    .accessKeyId(accessKey)
                    .secretAccessKey(secretKey)
                    .method(httpMethod);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getBucketName() {
        return schemaPrefix.replace("_", "-") + UserContext.getTenantId();
    }

}

