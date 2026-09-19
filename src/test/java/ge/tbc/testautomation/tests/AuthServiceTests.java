package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.authservice.AuthenticationResponse;
import ge.tbc.testautomation.data.model.authservice.RefreshTokenResponse;
import ge.tbc.testautomation.data.model.authservice.RegisterUserRequest;
import ge.tbc.testautomation.data.model.soap.EmployeeInfo;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

@Epic("Auth service")
@Feature("Authentication")
public class AuthServiceTests extends BaseTest {

    private RegisterUserRequest adminUser;
    private String accessToken;
    private String refreshToken;
    private String tokenBeforeRefresh;

    @Test(priority = 1)
    @Story("Registration")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Registration requires the email to already be known to the SOAP employee service")
    public void registerAdminUser() {
        adminUser = ObjectFactory.adminRegisterRequest();

        EmployeeInfo employeeInfo = ObjectFactory.employeeInfo(ObjectFactory.uniqueEmployeeId());
        employeeInfo.setEmail(adminUser.getEmail());
        employeeSoapSteps.addEmployee(employeeInfo);

        AuthenticationResponse response = authSteps.register(adminUser);

        authSteps.validateTokensPresent(response)
                .validateEmail(response, adminUser.getEmail())
                .validateAdminAuthorities(response);

        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
    }

    @Test(priority = 2, dependsOnMethods = "registerAdminUser")
    @Story("Protected resource")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates the exact message returned by the endpoint that requires admin role and read authority")
    public void adminResourceIsAccessibleWithBearerToken() {
        String message = authSteps.getAdminResource(accessToken);

        authSteps.validateAdminMessage(message);
    }

    @Test(priority = 3, dependsOnMethods = "registerAdminUser")
    @Story("Authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void authenticateAndVerifyAuthorities() {
        AuthenticationResponse response = authSteps.authenticate(
                ObjectFactory.authenticationRequest(adminUser.getEmail(), Constants.VALID_PASSWORD));

        authSteps.validateTokensPresent(response)
                .validateEmail(response, adminUser.getEmail())
                .validateAdminAuthorities(response);

        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
    }

    @Test(priority = 4, dependsOnMethods = "authenticateAndVerifyAuthorities")
    @Story("Refresh token")
    @Severity(SeverityLevel.NORMAL)
    public void refreshTokenIssuesNewAccessToken() {
        tokenBeforeRefresh = accessToken;

        RefreshTokenResponse response = authSteps.refreshToken(refreshToken);

        authSteps.validateRefreshedToken(response, refreshToken, adminUser.getEmail());

        accessToken = response.getAccessToken();
    }

    @Test(priority = 5, dependsOnMethods = "refreshTokenIssuesNewAccessToken")
    @Story("Refresh token")
    @Severity(SeverityLevel.NORMAL)
    public void refreshedAccessTokenOpensAdminResource() {
        String message = authSteps.getAdminResource(accessToken);

        authSteps.validateAdminMessage(message);
    }

    @Test(priority = 6, dependsOnMethods = "refreshTokenIssuesNewAccessToken")
    @Story("Refresh token")
    @Severity(SeverityLevel.MINOR)
    @Description("Refreshing does not revoke the previous jwt, it stays usable until its own expiration")
    public void previousAccessTokenStaysValidAfterRefresh() {
        int statusCode = authSteps.getAdminResourceStatusCode(tokenBeforeRefresh);

        authSteps.validateStatusCode(statusCode, Constants.SC_OK,
                "The previous access token is not revoked by a refresh and must still be accepted");
    }
}
