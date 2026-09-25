package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.api.client.AuthServiceApiClient;
import ge.tbc.testautomation.api.invoker.authservice.ApiClient;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.authservice.LoginRequest;
import ge.tbc.testautomation.data.model.authservice.AuthenticationResponse;
import ge.tbc.testautomation.data.model.authservice.RefreshTokenResponse;
import ge.tbc.testautomation.data.model.authservice.RegisterUserRequest;
import ge.tbc.testautomation.utils.JwtUtils;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static ge.tbc.testautomation.api.invoker.authservice.ResponseSpecBuilders.shouldBeCode;
import static ge.tbc.testautomation.api.invoker.authservice.ResponseSpecBuilders.validatedWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AuthSteps {

    private final ApiClient apiClient = AuthServiceApiClient.create();

    @Step("Register user with email and role")
    public AuthenticationResponse register(RegisterUserRequest registerRequest) {
        return apiClient.authenticationV1CookiesBased()
                .register1()
                .body(registerRequest)
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Register user and return the raw status code")
    public int registerAndGetStatusCode(RegisterUserRequest registerRequest) {
        return apiClient.authenticationV1CookiesBased()
                .register1()
                .body(registerRequest)
                .execute(Response::getStatusCode);
    }

    @Step("Authenticate user with email")
    public AuthenticationResponse authenticate(LoginRequest loginRequest) {
        return apiClient.authenticationV1CookiesBased()
                .authenticate1()
                .body(loginRequest)
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Refresh access token")
    public RefreshTokenResponse refreshToken(String refreshToken) {
        return apiClient.authenticationV1CookiesBased()
                .refreshToken1()
                .body(ObjectFactory.refreshTokenRequest(refreshToken))
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Register user via v2 (JWT based) endpoint")
    public AuthenticationResponse registerV2(RegisterUserRequest registerRequest) {
        return apiClient.authenticationV2()
                .register()
                .body(registerRequest)
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Register user via v2 endpoint and return the raw response")
    public Response registerV2AndGetResponse(RegisterUserRequest registerRequest) {
        return apiClient.authenticationV2()
                .register()
                .body(registerRequest)
                .execute(response -> response);
    }

    @Step("Authenticate user via v2 (JWT based) endpoint")
    public AuthenticationResponse authenticateV2(LoginRequest loginRequest) {
        return apiClient.authenticationV2()
                .authenticate()
                .body(loginRequest)
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Logout via v2 endpoint using the refresh token")
    public int logoutV2(String refreshToken) {
        return apiClient.authenticationV2()
                .revokeToken()
                .body(ObjectFactory.refreshTokenRequest(refreshToken))
                .execute(Response::getStatusCode);
    }

    @Step("Request the admin protected resource with a bearer token")
    public String getAdminResource(String accessToken) {
        return apiClient.authorization()
                .sayHelloWithRoleAdminAndReadAuthority()
                .reqSpec(spec -> spec.addHeader(Constants.AUTHORIZATION_HEADER, bearer(accessToken)))
                .execute(validatedWith(shouldBeCode(Constants.SC_OK))
                        .andThen(Response::asString));
    }

    @Step("Request the admin protected resource and return the raw status code")
    public int getAdminResourceStatusCode(String accessToken) {
        return apiClient.authorization()
                .sayHelloWithRoleAdminAndReadAuthority()
                .reqSpec(spec -> spec.addHeader(Constants.AUTHORIZATION_HEADER, bearer(accessToken)))
                .execute(Response::getStatusCode);
    }

    @Step("Validate both tokens are present in the response")
    public AuthSteps validateTokensPresent(AuthenticationResponse response) {
        assertNotNull(response, "AuthenticationResponse must not be null");
        assertNotNull(response.getId(), "User id must be returned");
        assertNotNull(response.getAccessToken(), "accessToken must be returned");
        assertNotNull(response.getRefreshToken(), "refreshToken must be returned");
        assertEquals(response.getTokenType(), Constants.BEARER_TOKEN_TYPE, "token type mismatch");
        return this;
    }

    @Step("Validate registered email")
    public AuthSteps validateEmail(AuthenticationResponse response, String expectedEmail) {
        assertEquals(response.getEmail(), expectedEmail, "Registered email mismatch");
        assertEquals(JwtUtils.subject(response.getAccessToken()), expectedEmail, "JWT subject mismatch");
        return this;
    }

    @Step("Validate the admin protected resource message")
    public AuthSteps validateAdminMessage(String actualMessage) {
        assertEquals(actualMessage.trim(), Constants.ADMIN_RESOURCE_MESSAGE, "Admin resource message mismatch");
        return this;
    }

    @Step("Validate the user owns every expected admin authority")
    public AuthSteps validateAdminAuthorities(AuthenticationResponse response) {
        assertNotNull(response.getRoles(), "Authorities must be returned");
        assertThat("User must own every expected admin authority",
                response.getRoles(), hasItems(Constants.EXPECTED_ADMIN_AUTHORITIES.toArray(new String[0])));
        assertThat("User must own exactly the expected admin authorities",
                response.getRoles(), containsInAnyOrder(Constants.EXPECTED_ADMIN_AUTHORITIES.toArray(new String[0])));
        return this;
    }

    @Step("Validate the refreshed token response")
    public AuthSteps validateRefreshedToken(RefreshTokenResponse response, String refreshToken, String expectedEmail) {
        assertNotNull(response.getAccessToken(), "New accessToken must be returned");
        assertEquals(JwtUtils.subject(response.getAccessToken()), expectedEmail, "JWT subject mismatch");
        assertEquals(response.getRefreshToken(), refreshToken, "Refresh token must stay the same");
        assertEquals(response.getTokenType(), Constants.BEARER_TOKEN_TYPE, "token type mismatch");
        return this;
    }

    @Step("Validate status code")
    public AuthSteps validateStatusCode(int actualStatusCode, int expectedStatusCode, String message) {
        assertEquals(actualStatusCode, expectedStatusCode, message);
        return this;
    }

    private String bearer(String token) {
        return Constants.BEARER_PREFIX + token;
    }
}
