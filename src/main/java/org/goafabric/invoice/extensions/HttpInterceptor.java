package org.goafabric.invoice.extensions;

public class HttpInterceptor {
    private static final String JWT_USER1 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcjEiLCJpYXQiOjE1MTYyMzkwMjJ9.-JxynqYw5AhNqgwJV8yeKSCOOfYF0oMsO2arF2b4a5E";

    public static String getToken() {
        return JWT_USER1;
    }

    public static String getUserName() {
        return "user1";
    }

    public static String getTenantId() {
        return "0";
    }

    public static String getOrganizationId() {
        return "1";
    }

}
