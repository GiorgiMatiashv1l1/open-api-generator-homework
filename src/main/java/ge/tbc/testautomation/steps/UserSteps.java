package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.api.client.AuthServiceApiClient;
import ge.tbc.testautomation.api.invoker.authservice.ApiClient;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.model.authservice.ChangeEmailRequest;
import ge.tbc.testautomation.data.model.authservice.ChangePasswordRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static ge.tbc.testautomation.api.invoker.authservice.ResponseSpecBuilders.shouldBeCode;
import static ge.tbc.testautomation.api.invoker.authservice.ResponseSpecBuilders.validatedWith;

public class UserSteps {

    private final ApiClient apiClient = AuthServiceApiClient.create();

    @Step("Change password using a bearer token")
    public String changePassword(String accessToken, String oldPassword, String newPassword) {
        ChangePasswordRequest request = new ChangePasswordRequest()
                .oldPassword(oldPassword)
                .newPassword(newPassword);

        return apiClient.userManagement()
                .changePassword()
                .body(request)
                .reqSpec(spec -> spec.addHeader(Constants.AUTHORIZATION_HEADER, bearer(accessToken)))
                .execute(validatedWith(shouldBeCode(Constants.SC_OK))
                        .andThen(Response::asString));
    }

    @Step("Change email using a bearer token")
    public String changeEmail(String accessToken, String newEmail) {
        ChangeEmailRequest request = new ChangeEmailRequest().newEmail(newEmail);

        return apiClient.userManagement()
                .changeEmail()
                .body(request)
                .reqSpec(spec -> spec.addHeader(Constants.AUTHORIZATION_HEADER, bearer(accessToken)))
                .execute(validatedWith(shouldBeCode(Constants.SC_OK))
                        .andThen(Response::asString));
    }

    private String bearer(String token) {
        return Constants.BEARER_PREFIX + token;
    }
}
