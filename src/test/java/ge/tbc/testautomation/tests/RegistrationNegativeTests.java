package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.DataProviders;
import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.authservice.RegisterUserRequest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

@Epic("Auth service")
@Feature("Authentication")
public class RegistrationNegativeTests extends BaseTest {

    @Test(dataProvider = "invalidPasswords", dataProviderClass = DataProviders.class)
    @Story("Registration validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("A password must be 8 characters long and combine uppercase letters, lowercase letters, numbers and special characters")
    public void registerWithInvalidPassword(String caseName, String password) {
        RegisterUserRequest request = ObjectFactory.registerRequest(
                ObjectFactory.uniqueEmail("negative"), password, RegisterUserRequest.RoleEnum.USER);

        int statusCode = authSteps.registerAndGetStatusCode(request);

        authSteps.validateStatusCode(statusCode, Constants.SC_BAD_REQUEST,
                "Case [" + caseName + "] with password <" + password + "> must be rejected");
    }
}
