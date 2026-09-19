package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.petstore.Pet;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;

@Epic("Petstore")
@Feature("Pet")
public class PetTests extends BaseTest {

    @Test
    @Story("Add pet")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validations are performed with the rest-assured functional API inside the execute lambda")
    public void addPetAndValidateWithRestAssuredFunctional() {
        Pet pet = ObjectFactory.validPet();

        Response response = petSteps.addPet(pet);

        petSteps.validatePet(response.as(Pet.class), pet);
    }
}
