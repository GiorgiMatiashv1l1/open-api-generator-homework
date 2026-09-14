package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.api.client.PetStoreApiClient;
import ge.tbc.testautomation.api.invoker.petstore.ApiClient;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.model.petstore.Order;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ge.tbc.testautomation.api.invoker.petstore.ResponseSpecBuilders.shouldBeCode;
import static ge.tbc.testautomation.api.invoker.petstore.ResponseSpecBuilders.validatedWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class StoreSteps {

    private final ApiClient apiClient = PetStoreApiClient.create();

    @Step("Place order and deserialize the response")
    public Order placeOrder(Order order) {
        return apiClient.store()
                .placeOrder()
                .body(order)
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Place order and map the response")
    public Order placeOrderWithAndThen(Order order) {
        return apiClient.store()
                .placeOrder()
                .body(order)
                .execute(validatedWith(shouldBeCode(Constants.SC_OK)
                        .expectContentType(ContentType.JSON)
                        .expectResponseTime(lessThan(Constants.MAX_RESPONSE_TIME_SECONDS), TimeUnit.SECONDS)
                        .expectBody("id", notNullValue())
                        .expectBody("petId", equalTo(order.getPetId().intValue())))
                        .andThen(response -> response.as(Order.class)));
    }

    @Step("Get order by id")
    public Order getOrderById(Long orderId) {
        return apiClient.store()
                .getOrderById()
                .orderIdPath(orderId)
                .reqSpec(spec -> spec.addHeader("X-Test-Suite", "open-api-generator-homework"))
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Verify order is not found")
    public StoreSteps verifyOrderNotFound(Long orderId) {
        apiClient.store()
                .getOrderById()
                .orderIdPath(orderId)
                .execute(validatedWith(shouldBeCode(Constants.SC_NOT_FOUND)));
        return this;
    }

    @Step("Get store inventory")
    public Map<String, Integer> getInventory() {
        return apiClient.store()
                .getInventory()
                .executeAs(validatedWith(shouldBeCode(Constants.SC_OK)));
    }

    @Step("Validate created order matches the request")
    public StoreSteps validateOrder(Order actual, Order expected) {
        assertNotNull(actual, "Response POJO must not be null");
        assertEquals(actual.getId(), expected.getId(), "Order id mismatch");
        assertEquals(actual.getPetId(), expected.getPetId(), "Order petId mismatch");
        assertEquals(actual.getQuantity(), expected.getQuantity(), "Order quantity mismatch");
        assertEquals(actual.getStatus(), expected.getStatus(), "Order status mismatch");
        assertTrue(Boolean.TRUE.equals(actual.getComplete()), "Order complete flag must be true");
        return this;
    }

    @Step("Validate fetched order matches the created one")
    public StoreSteps validateSameOrder(Order fetched, Order created) {
        assertEquals(fetched.getId(), created.getId(), "Fetched order id mismatch");
        assertEquals(fetched.getPetId(), created.getPetId(), "Fetched order petId mismatch");
        assertEquals(fetched.getQuantity(), created.getQuantity(), "Fetched order quantity mismatch");
        return this;
    }
}
