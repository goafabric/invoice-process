package org.goafabric.invoice.extensions;

import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jwt.JWTParser;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

public class TenantContext {
    public static Map<String, String> getAdapterHeaderMap() { //can be used to simply forward the headers for adapters
        return Map.of("X-TenantId", getTenantId(), "X-OrganizationId", getOrganizationId(), "X-Access-Token", getAuthToken());
    }

    private static final String JWT_USER1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcjEiLCJpYXQiOjE1MTYyMzkwMjJ9.-JxynqYw5AhNqgwJV8yeKSCOOfYF0oMsO2arF2b4a5E";
    private static final String JTW_NOONE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoibm9vbmUiLCJpYXQiOjE1MTYyMzkwMjJ9.L69_aXYnGVMpPMoX-vQFflqYUm_qHn2cQOIzCKygCJc";


    public static String getUserName() {
        return getUserNameFromToken(getAuthToken());
    }

    public static String getAuthToken() {
        return JWT_USER1;
    }

    public static String getTenantId() {
        return "0";
    }

    public static String getOrganizationId() {
        return "1";
    }

    static String getUserNameFromToken(String authToken) {
        if (authToken != null) {
            var payload = decodeJwt(authToken);
            Objects.requireNonNull(payload.get("preferred_username"), "preferred_username in JWT is null");
            return payload.get("preferred_username").toString();
        }
        return "";
    }

    private static Map<String, Object> decodeJwt(String token) {
        try {
            return ((JOSEObject) JWTParser.parse(token)).getPayload().toJSONObject();
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }



}
