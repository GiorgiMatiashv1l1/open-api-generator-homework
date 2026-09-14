package ge.tbc.testautomation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class JwtUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final char JWT_SEPARATOR = '.';

    private JwtUtils() {
    }

    public static JsonNode payload(String jwt) {
        int payloadStart = jwt.indexOf(JWT_SEPARATOR) + 1;
        int payloadEnd = jwt.lastIndexOf(JWT_SEPARATOR);
        if (payloadStart <= 0 || payloadEnd <= payloadStart) {
            throw new IllegalArgumentException("Value is not a JWT: " + jwt);
        }
        byte[] decoded = Base64.getUrlDecoder().decode(jwt.substring(payloadStart, payloadEnd));
        try {
            return MAPPER.readTree(new String(decoded, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse JWT payload", e);
        }
    }

    public static String subject(String jwt) {
        return payload(jwt).get("sub").asText();
    }
}
