package org.goafabric.eventdispatcher.service.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public class UserContext {

    public static final String X_TENANT_ID = "X-TenantId";
    public static final String X_ORGANIZATION_ID = "X-OrganizationId";
    public static final String X_AUTH_REQUEST_PREFERRED_USERNAME = "X-Auth-Request-Preferred-Username";
    public static final String X_USER_INFO = "X-UserInfo";

    record TenantContextRecord(String tenantId, String organizationId, String userName) {
        public Map<String, String> toAdapterHeaderMap() {
            return Map.of(X_TENANT_ID, tenantId, X_ORGANIZATION_ID, organizationId, X_AUTH_REQUEST_PREFERRED_USERNAME, userName);
        }
    }

    private static final ThreadLocal<TenantContextRecord> CONTEXT =
            ThreadLocal.withInitial(() -> new TenantContextRecord("0", "0", "anonymous"));

    public static void setContext(HttpServletRequest request) {
        setContext(request.getHeader(X_TENANT_ID), request.getHeader(X_ORGANIZATION_ID),
                request.getHeader(X_AUTH_REQUEST_PREFERRED_USERNAME), request.getHeader(X_USER_INFO));
    }

    public static void setContext(Map<String, String> tenantHeaderMap) {
        setContext(tenantHeaderMap.get(X_TENANT_ID), tenantHeaderMap.get(X_ORGANIZATION_ID),
                tenantHeaderMap.get(X_AUTH_REQUEST_PREFERRED_USERNAME), tenantHeaderMap.get(X_USER_INFO));
    }

    static void setContext(String tenantId, String organizationId, String userName, String userInfo) {
        CONTEXT.set(new TenantContextRecord(
                getValue(tenantId, "0"),
                getValue(organizationId, "0"),
                getValue(getUserNameFromUserInfo(userInfo), getValue(userName, "anonymous"))
        ));
    }

    public static void removeContext() {
        CONTEXT.remove();
    }

    private static String getValue(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static String getTenantId() {
        return CONTEXT.get().tenantId();
    }

    public static String getOrganizationId() {
        return CONTEXT.get().organizationId();
    }

    public static String getUserName() {
        return CONTEXT.get().userName();
    }

    public static Map<String, String> getAdapterHeaderMap() {
        return CONTEXT.get().toAdapterHeaderMap();
    }

    public static void setTenantId(String tenant) {
        CONTEXT.set(new TenantContextRecord(tenant, CONTEXT.get().organizationId, CONTEXT.get().userName));
    }

    private static String getUserNameFromUserInfo(String userInfo) {
        try {
            return userInfo != null ? (String) new ObjectMapper().readValue(Base64.getUrlDecoder().decode(userInfo), Map.class).get("preferred_username") : null;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}