package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.api.client.AuthServiceApiClient;
import ge.tbc.testautomation.api.invoker.authservice.ApiClient;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.authservice.AuthenticationRequest;
import ge.tbc.testautomation.data.model.authservice.AuthenticationResponse;
import ge.tbc.testautomation.data.model.authservice.RefreshTokenResponse;
import ge.tbc.testautomation.data.model.authservice.RegisterRequest;
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
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        return apiClient.authentication()
                .register()
                .body(registerRequest)
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Register user and return the raw status code")
    public int registerAndGetStatusCode(RegisterRequest registerRequest) {
        return apiClient.authentication()
                .register()
                .body(registerRequest)
                .execute(Response::getStatusCode);
    }

    @Step("Authenticate user with email")
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        return apiClient.authentication()
                .authenticate()
                .body(authenticationRequest)
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Refresh access token")
    public RefreshTokenResponse refreshToken(String refreshToken) {
        return apiClient.authentication()
                .refreshToken()
                .body(ObjectFactory.refreshTokenRequest(refreshToken))
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
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
