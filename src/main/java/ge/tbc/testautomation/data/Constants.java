package ge.tbc.testautomation.data;

import java.util.List;

public final class Constants {

    private Constants() {
    }

    public static final String PETSTORE_BASE_URI = "https://petstore3.swagger.io/api/v3";
    public static final String AUTH_SERVICE_BASE_URI = "http://localhost:8086";
    public static final String SOAP_SERVICE_URL = "http://localhost:8087/ws";
    public static final String CONTINENT_INFO_URL =
            "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso/ListOfContinentsByName";

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BEARER_TOKEN_TYPE = "BEARER";

    public static final String ADMIN_RESOURCE_MESSAGE =
            "Hello, you have access to a protected resource that requires admin role and read authority.";

    public static final List<String> EXPECTED_ADMIN_AUTHORITIES = List.of(
            "READ_PRIVILEGE",
            "WRITE_PRIVILEGE",
            "DELETE_PRIVILEGE",
            "UPDATE_PRIVILEGE",
            "ROLE_ADMIN"
    );

    public static final String VALID_PASSWORD = "Qwerty123!";
    public static final String EMAIL_DOMAIN = "@tbc.ge";

    public static final int SC_OK = 200;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;

    public static final String CONTENT_ADDED_SUCCESS_MESSAGE = "Content Added Successfully";
    public static final String SERVICE_STATUS_SUCCESS = "SUCCESS";
    public static final List<String> EXPECTED_CONTINENTS = List.of(
            "Africa", "Antarctica", "Asia", "Europe", "Ocenania", "The Americas");

    public static final long UNKNOWN_ORDER_ID = 999999L;
    public static final long DEFAULT_PET_ID = 198772L;
    public static final long MAX_RESPONSE_TIME_SECONDS = 15L;
}
