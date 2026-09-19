package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.steps.AuthSteps;
import ge.tbc.testautomation.steps.PetSteps;
import ge.tbc.testautomation.steps.StoreSteps;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {

    protected StoreSteps storeSteps;
    protected PetSteps petSteps;
    protected AuthSteps authSteps;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        storeSteps = new StoreSteps();
        petSteps = new PetSteps();
        authSteps = new AuthSteps();
    }
}
