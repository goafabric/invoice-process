package org.goafabric.invoice.extensions;

import java.util.Map;

public class TenantContext {
    private static final String JWT_USER1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcjEiLCJpYXQiOjE1MTYyMzkwMjJ9.-JxynqYw5AhNqgwJV8yeKSCOOfYF0oMsO2arF2b4a5E";

    public static String getAuthToken() {
        return JWT_USER1;
    }

    public static String getUserName() {
        return "user3";
    }

    public static String getTenantId() {
        return "0";
    }

    public static String getOrganizationId() {
        return "1";
    }


    public static Map<String, String> getAdapterHeaderMap() { //can be used to simply forward the headers for adapters
        return Map.of("X-TenantId", getTenantId(), "X-OrganizationId", getOrganizationId(), "X-Access-Token", getAuthToken());
    }

}
