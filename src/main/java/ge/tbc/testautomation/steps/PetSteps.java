package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.api.client.PetStoreApiClient;
import ge.tbc.testautomation.api.invoker.petstore.ApiClient;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.model.petstore.Pet;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static ge.tbc.testautomation.api.invoker.petstore.ResponseSpecBuilders.shouldBeCode;
import static ge.tbc.testautomation.api.invoker.petstore.ResponseSpecBuilders.validatedWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;

public class PetSteps {

    private final ApiClient apiClient = PetStoreApiClient.create();

    @Step("Add pet and get the raw response")
    public Response addPet(Pet pet) {
        return apiClient.pet()
                .addPet()
                .body(pet)
                .execute(response -> response.then()
                        .statusCode(Constants.SC_OK)
                        .contentType("application/json")
                        .body("id", notNullValue())
                        .body("name", equalTo(pet.getName()))
                        .body("status", equalTo(pet.getStatus().getValue()))
                        .body("photoUrls", hasSize(pet.getPhotoUrls().size()))
                        .body("category.name", equalTo(pet.getCategory().getName()))
                        .body("tags[0].name", equalTo(pet.getTags().get(0).getName()))
                        .extract().response());
    }

    @Step("Get pet by id")
    public Pet getPetById(Long petId) {
        return apiClient.pet()
                .getPetById()
                .petIdPath(petId)
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Validate created pet")
    public PetSteps validatePet(Pet actual, Pet expected) {
        assertEquals(actual.getId(), expected.getId(), "Pet id mismatch");
        assertEquals(actual.getName(), expected.getName(), "Pet name mismatch");
        assertEquals(actual.getStatus(), expected.getStatus(), "Pet status mismatch");
        assertEquals(actual.getCategory().getName(), expected.getCategory().getName(), "Pet category mismatch");
        return this;
    }
}
