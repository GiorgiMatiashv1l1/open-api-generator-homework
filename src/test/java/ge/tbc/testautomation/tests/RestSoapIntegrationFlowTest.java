package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.authservice.AuthenticationResponse;
import ge.tbc.testautomation.data.model.authservice.RegisterUserRequest;
import ge.tbc.testautomation.data.model.soap.EmployeeInfo;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;

@Epic("REST + SOAP + DB integration flow")
@Feature("End to end registration, authentication and profile update scenario")
public class RestSoapIntegrationFlowTest extends BaseTest {

    private static final String NEW_PASSWORD = "Qwerty456!";

    private EmployeeInfo employeeInfo;
    private long userId;
    private String accessToken;
    private String refreshToken;
    private String currentPassword;
    private String currentEmail;

    @Test(priority = 1)
    @Story("Negative registration")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Registering with an email unknown to the SOAP service must surface a SOAP error")
    public void registerWithEmailUnknownToSoapReturnsSoapError() {
        RegisterUserRequest request = ObjectFactory.registerRequest(
                ObjectFactory.uniqueEmail("unknown-to-soap"), Constants.VALID_PASSWORD, RegisterUserRequest.RoleEnum.ADMIN);

        Response response = authSteps.registerV2AndGetResponse(request);

        response.then().statusCode(Constants.SC_BAD_GATEWAY);
        assertThat("error field must report a SOAP failure",
                response.jsonPath().getString("error"), equalTo(Constants.SOAP_SERVICE_ERROR));
        assertThat("message must mention the SOAP fault",
                response.jsonPath().getString("message"), containsString("SOAP Fault"));
    }

    @Test(priority = 2)
    @Story("Valid registration")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Add the employee via SOAP, then register the same email through the REST v2 endpoint")
    public void registerWithSoapKnownEmail() {
        employeeInfo = ObjectFactory.employeeInfo(ObjectFactory.uniqueEmployeeId());
        employeeSoapSteps.addEmployee(employeeInfo);
        currentEmail = employeeInfo.getEmail();
        currentPassword = Constants.VALID_PASSWORD;

        RegisterUserRequest request = ObjectFactory.registerRequest(
                currentEmail, currentPassword, RegisterUserRequest.RoleEnum.ADMIN);

        AuthenticationResponse response = authSteps.registerV2(request);

        authSteps.validateTokensPresent(response).validateEmail(response, currentEmail);
        userId = response.getId();
        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
    }

    @Test(priority = 3, dependsOnMethods = "registerWithSoapKnownEmail")
    @Story("Protected resource access")
    @Severity(SeverityLevel.CRITICAL)
    public void adminResourceIsAccessible() {
        String message = authSteps.getAdminResource(accessToken);

        authSteps.validateAdminMessage(message);
    }

    @Test(priority = 4, dependsOnMethods = "adminResourceIsAccessible")
    @Story("Change password")
    @Severity(SeverityLevel.CRITICAL)
    public void changePasswordSucceeds() {
        String result = userSteps.changePassword(accessToken, currentPassword, NEW_PASSWORD);

        assertThat(result, containsString("Password changed successfully"));
        currentPassword = NEW_PASSWORD;
    }

    @Test(priority = 5, dependsOnMethods = "changePasswordSucceeds")
    @Story("Logout")
    @Severity(SeverityLevel.NORMAL)
    public void logout() {
        int statusCode = authSteps.logoutV2(refreshToken);

        assertEquals(statusCode, Constants.SC_OK, "logout must succeed");
    }

    @Test(priority = 6, dependsOnMethods = "logout")
    @Story("Re-authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Authenticate again with the new password after logout")
    public void reAuthenticateWithNewPassword() {
        AuthenticationResponse response = authSteps.authenticateV2(
                ObjectFactory.authenticationRequest(currentEmail, currentPassword));

        authSteps.validateTokensPresent(response).validateEmail(response, currentEmail);
        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
    }

    @Test(priority = 7, dependsOnMethods = "reAuthenticateWithNewPassword")
    @Story("Change email")
    @Severity(SeverityLevel.CRITICAL)
    public void changeEmailSucceeds() {
        String newEmail = ObjectFactory.uniqueEmail("changed");

        String result = userSteps.changeEmail(accessToken, newEmail);

        assertThat(result, containsString("Email changed successfully"));
        currentEmail = newEmail;
    }

    @Test(priority = 8, dependsOnMethods = "changeEmailSucceeds")
    @Story("Authenticate with new email")
    @Severity(SeverityLevel.CRITICAL)
    public void authenticateWithNewEmail() {
        AuthenticationResponse response = authSteps.authenticateV2(
                ObjectFactory.authenticationRequest(currentEmail, currentPassword));

        authSteps.validateTokensPresent(response).validateEmail(response, currentEmail);
        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
    }

    @Test(priority = 9, dependsOnMethods = "authenticateWithNewEmail")
    @Story("SOAP reflects the new email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The REST change-email call propagates to the SOAP employee record")
    public void soapReflectsNewEmail() {
        EmployeeInfo actual = employeeSoapSteps.getEmployeeByEmail(currentEmail).getEmployeeInfo();

        assertThat("SOAP must resolve the new email", actual, notNullValue());
        assertEquals(actual.getEmployeeId(), employeeInfo.getEmployeeId(), "SOAP must return the same employee");
        assertEquals(actual.getEmail(), currentEmail, "SOAP employee email must match the new email");
    }

    @Test(priority = 10, dependsOnMethods = "authenticateWithNewEmail")
    @Story("Database reflects the new email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Both the users table and the employee table must reflect the new email")
    public void databaseReflectsNewEmail() {
        String userEmail = employeeDbSteps.getUserEmailById(userId);
        assertEquals(userEmail, currentEmail, "users table must reflect the new email");

        String employeeEmail = employeeDbSteps.getEmployeeById(employeeInfo.getEmployeeId()).getEmail();
        assertEquals(employeeEmail, currentEmail, "employee table must reflect the new email");
    }
}
