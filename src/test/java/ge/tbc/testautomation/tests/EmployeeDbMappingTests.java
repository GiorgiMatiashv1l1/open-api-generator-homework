package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.soap.EmployeeInfo;
import ge.tbc.testautomation.data.model.soap.GetEmployeeByIdResponse;
import ge.tbc.testautomation.db.model.EmployeeRow;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Employee SOAP + DB mapping")
@Feature("MyBatis / SOAP object mapping")
public class EmployeeDbMappingTests extends BaseTest {

    @Test
    @Story("Insert via mybatis, read via SOAP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Insert a new employee directly in the database via mybatis and verify SOAP getEmployeeById returns matching details")
    public void insertEmployeeViaMybatisThenGetBySoap() {
        EmployeeRow employeeRow = ObjectFactory.employeeRow(ObjectFactory.uniqueEmployeeId());

        employeeDbSteps.insertEmployee(employeeRow);

        GetEmployeeByIdResponse response = employeeSoapSteps.getEmployeeById(employeeRow.getEmployeeId());
        EmployeeInfo actual = response.getEmployeeInfo();

        assertThat("SOAP response must contain the employee", actual, notNullValue());
        employeeSoapSteps.validateEmployee(actual, ObjectFactory.toEmployeeInfo(employeeRow));
    }

    @Test
    @Story("Update via SOAP, validate via DB")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update an employee through the SOAP updateEmployee operation and verify the row was persisted via mybatis")
    public void updateEmployeeViaSoapThenValidateInDb() {
        EmployeeRow employeeRow = ObjectFactory.employeeRow(ObjectFactory.uniqueEmployeeId());
        employeeDbSteps.insertEmployee(employeeRow);

        EmployeeInfo updatedInfo = ObjectFactory.toEmployeeInfo(employeeRow);
        updatedInfo.setName("Updated " + updatedInfo.getName());
        updatedInfo.setDepartment("Updated Department");
        employeeSoapSteps.updateEmployee(updatedInfo);

        EmployeeRow persistedRow = employeeDbSteps.getEmployeeById(employeeRow.getEmployeeId());

        assertThat("Employee row must exist after the SOAP update", persistedRow, notNullValue());
        assertThat("Name must be updated in the database", persistedRow.getName(), equalTo(updatedInfo.getName()));
        assertThat("Department must be updated in the database", persistedRow.getDepartment(), equalTo(updatedInfo.getDepartment()));
        assertThat("Salary must be unchanged in the database", persistedRow.getSalary(), comparesEqualTo(updatedInfo.getSalary()));
    }

    @Test
    @Story("Update via DB, validate via SOAP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update an employee directly via mybatis and verify SOAP getEmployeeById reflects the change")
    public void updateEmployeeViaDbThenValidateBySoap() {
        EmployeeRow employeeRow = ObjectFactory.employeeRow(ObjectFactory.uniqueEmployeeId());
        employeeDbSteps.insertEmployee(employeeRow);

        employeeRow.setName("DB Updated " + employeeRow.getName());
        employeeRow.setAddress("DB Updated Address");
        employeeDbSteps.updateEmployee(employeeRow);

        GetEmployeeByIdResponse response = employeeSoapSteps.getEmployeeById(employeeRow.getEmployeeId());

        assertThat("SOAP response must contain the employee", response.getEmployeeInfo(), notNullValue());
        employeeSoapSteps.validateEmployee(response.getEmployeeInfo(), ObjectFactory.toEmployeeInfo(employeeRow));
    }

    @Test
    @Story("Delete via SOAP, validate row count in DB")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete an employee via SOAP deleteEmployee and verify the row no longer exists in the database")
    public void deleteEmployeeViaSoapThenValidateDbCount() {
        EmployeeRow employeeRow = ObjectFactory.employeeRow(ObjectFactory.uniqueEmployeeId());
        employeeDbSteps.insertEmployee(employeeRow);
        assertThat("Employee row must exist before delete",
                employeeDbSteps.countByEmployeeId(employeeRow.getEmployeeId()), is(1));

        employeeSoapSteps.deleteEmployee(employeeRow.getEmployeeId());

        assertThat("Employee row must be gone after the SOAP delete",
                employeeDbSteps.countByEmployeeId(employeeRow.getEmployeeId()), is(0));
    }
}
