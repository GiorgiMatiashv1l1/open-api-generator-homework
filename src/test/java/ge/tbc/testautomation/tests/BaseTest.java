package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.steps.AuthSteps;
import ge.tbc.testautomation.steps.EmployeeDbSteps;
import ge.tbc.testautomation.steps.EmployeeSoapSteps;
import ge.tbc.testautomation.steps.PetSteps;
import ge.tbc.testautomation.steps.StoreSteps;
import ge.tbc.testautomation.steps.UserSteps;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {

    protected StoreSteps storeSteps;
    protected PetSteps petSteps;
    protected AuthSteps authSteps;
    protected EmployeeSoapSteps employeeSoapSteps;
    protected EmployeeDbSteps employeeDbSteps;
    protected UserSteps userSteps;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        storeSteps = new StoreSteps();
        petSteps = new PetSteps();
        authSteps = new AuthSteps();
        employeeSoapSteps = new EmployeeSoapSteps();
        employeeDbSteps = new EmployeeDbSteps();
        userSteps = new UserSteps();
    }
}
