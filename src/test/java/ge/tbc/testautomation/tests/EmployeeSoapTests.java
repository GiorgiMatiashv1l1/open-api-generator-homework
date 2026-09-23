package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.soap.EmployeeInfo;
import ge.tbc.testautomation.data.model.soap.GetEmployeeByIdResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Employee SOAP service")
@Feature("Employee CRUD")
public class EmployeeSoapTests extends BaseTest {

    @Test
    @Story("Add and get employee")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Add an employee and verify it can be fetched back with matching details")
    public void addEmployeeAndGetEmployeeById() {
        long employeeId = ObjectFactory.uniqueEmployeeId();
        EmployeeInfo employeeInfo = ObjectFactory.employeeInfo(employeeId);

        employeeSoapSteps.addEmployee(employeeInfo);

        GetEmployeeByIdResponse getResponse = employeeSoapSteps.getEmployeeById(employeeId);
        employeeSoapSteps.validateEmployee(getResponse.getEmployeeInfo(), employeeInfo);
    }

    @Test
    @Story("Update employee")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update an existing employee and verify the changes are persisted")
    public void updateEmployeeAndVerifyChanges() {
        long employeeId = ObjectFactory.uniqueEmployeeId();
        EmployeeInfo employeeInfo = ObjectFactory.employeeInfo(employeeId);
        employeeSoapSteps.addEmployee(employeeInfo);

        EmployeeInfo updatedInfo = ObjectFactory.employeeInfo(employeeId);
        updatedInfo.setName("Updated " + updatedInfo.getName());
        updatedInfo.setDepartment("Updated Department");
        employeeSoapSteps.updateEmployee(updatedInfo);

        GetEmployeeByIdResponse getResponse = employeeSoapSteps.getEmployeeById(employeeId);
        employeeSoapSteps.validateEmployee(getResponse.getEmployeeInfo(), updatedInfo);
    }

    @Test
    @Story("Delete employee")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete an employee and verify a subsequent lookup fails with a SOAP fault")
    public void deleteEmployeeAndVerifyItNoLongerExists() {
        long employeeId = ObjectFactory.uniqueEmployeeId();
        EmployeeInfo employeeInfo = ObjectFactory.employeeInfo(employeeId);
        employeeSoapSteps.addEmployee(employeeInfo);

        employeeSoapSteps.deleteEmployee(employeeId);

        Response response = employeeSoapSteps.getEmployeeByIdRaw(employeeId);
        response.then().statusCode(Constants.SC_INTERNAL_SERVER_ERROR);
        assertThat(response.xmlPath().getString("Envelope.Body.Fault.faultstring"), notNullValue());
    }
}
