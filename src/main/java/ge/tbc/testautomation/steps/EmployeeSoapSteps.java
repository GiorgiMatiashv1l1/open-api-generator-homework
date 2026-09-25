package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.api.client.EmployeeSoapClient;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.model.soap.AddEmployeeRequest;
import ge.tbc.testautomation.data.model.soap.AddEmployeeResponse;
import ge.tbc.testautomation.data.model.soap.DeleteEmployeeRequest;
import ge.tbc.testautomation.data.model.soap.EmployeeInfo;
import ge.tbc.testautomation.data.model.soap.GetEmployeeByEmailRequest;
import ge.tbc.testautomation.data.model.soap.GetEmployeeByEmailResponse;
import ge.tbc.testautomation.data.model.soap.GetEmployeeByIdRequest;
import ge.tbc.testautomation.data.model.soap.GetEmployeeByIdResponse;
import ge.tbc.testautomation.data.model.soap.UpdateEmployeeRequest;
import ge.tbc.testautomation.data.model.soap.UpdateEmployeeResponse;
import ge.tbc.testautomation.utils.SoapUtils;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.testng.Assert.assertEquals;

public class EmployeeSoapSteps {

    @Step("Add employee via SOAP and validate the response")
    public Response addEmployee(EmployeeInfo employeeInfo) {
        AddEmployeeRequest request = new AddEmployeeRequest();
        request.setEmployeeInfo(employeeInfo);

        Response response = EmployeeSoapClient.send(SoapUtils.marshal(request));
        response.then().statusCode(Constants.SC_OK);

        assertEquals(
                response.xmlPath().getString("Envelope.Body.addEmployeeResponse.serviceStatus.message"),
                Constants.CONTENT_ADDED_SUCCESS_MESSAGE,
                "addEmployee status message mismatch");

        return response;
    }

    @Step("Get employee by id and deserialize the response")
    public GetEmployeeByIdResponse getEmployeeById(long employeeId) {
        GetEmployeeByIdRequest request = new GetEmployeeByIdRequest();
        request.setEmployeeId(employeeId);

        Response response = EmployeeSoapClient.send(SoapUtils.marshal(request));
        response.then().statusCode(Constants.SC_OK);

        return SoapUtils.unmarshal(response.asString(), GetEmployeeByIdResponse.class);
    }

    @Step("Get employee by email and deserialize the response")
    public GetEmployeeByEmailResponse getEmployeeByEmail(String email) {
        GetEmployeeByEmailRequest request = new GetEmployeeByEmailRequest();
        request.setEmail(email);

        Response response = EmployeeSoapClient.send(SoapUtils.marshal(request));
        response.then().statusCode(Constants.SC_OK);

        return SoapUtils.unmarshal(response.asString(), GetEmployeeByEmailResponse.class);
    }

    @Step("Get employee by id and return the raw response")
    public Response getEmployeeByIdRaw(long employeeId) {
        GetEmployeeByIdRequest request = new GetEmployeeByIdRequest();
        request.setEmployeeId(employeeId);

        return EmployeeSoapClient.send(SoapUtils.marshal(request));
    }

    @Step("Update employee and validate the response with deserialization")
    public UpdateEmployeeResponse updateEmployee(EmployeeInfo employeeInfo) {
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setEmployeeInfo(employeeInfo);

        Response response = EmployeeSoapClient.send(SoapUtils.marshal(request));
        response.then().statusCode(Constants.SC_OK);

        UpdateEmployeeResponse updateResponse =
                SoapUtils.unmarshal(response.asString(), UpdateEmployeeResponse.class);
        assertEquals(updateResponse.getServiceStatus().getStatus(), Constants.SERVICE_STATUS_SUCCESS,
                "updateEmployee status mismatch");

        return updateResponse;
    }

    @Step("Delete employee and validate the response with XmlPath")
    public void deleteEmployee(long employeeId) {
        DeleteEmployeeRequest request = new DeleteEmployeeRequest();
        request.setEmployeeId(employeeId);

        Response response = EmployeeSoapClient.send(SoapUtils.marshal(request));
        response.then().statusCode(Constants.SC_OK);

        assertEquals(
                response.xmlPath().getString("Envelope.Body.deleteEmployeeResponse.serviceStatus.status"),
                Constants.SERVICE_STATUS_SUCCESS,
                "deleteEmployee status mismatch");
    }

    @Step("Validate fetched employee matches the expected employee")
    public EmployeeSoapSteps validateEmployee(EmployeeInfo actual, EmployeeInfo expected) {
        assertEquals(actual.getEmployeeId(), expected.getEmployeeId(), "Employee id mismatch");
        assertEquals(actual.getName(), expected.getName(), "Employee name mismatch");
        assertEquals(actual.getDepartment(), expected.getDepartment(), "Employee department mismatch");
        assertEquals(actual.getEmail(), expected.getEmail(), "Employee email mismatch");
        assertEquals(0, actual.getSalary().compareTo(expected.getSalary()), "Employee salary mismatch");
        assertEquals(SoapUtils.toLocalDate(actual.getBirthDate()), SoapUtils.toLocalDate(expected.getBirthDate()),
                "Employee birth date mismatch");
        return this;
    }
}
