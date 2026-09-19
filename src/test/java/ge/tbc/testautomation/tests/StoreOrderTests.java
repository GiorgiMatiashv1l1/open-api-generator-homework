package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.ObjectFactory;
import ge.tbc.testautomation.data.model.petstore.Order;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.not;

@Epic("Petstore")
@Feature("Store")
public class StoreOrderTests extends BaseTest {

    @Test
    @Story("Place order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Sends an order body with the generated client and deserializes the response with executeAs")
    public void placeOrderReturnsCreatedOrderPojo() {
        Order request = ObjectFactory.validOrder();

        Order created = storeSteps.placeOrder(request);

        storeSteps.validateOrder(created, request);
    }

    @Test
    @Story("Place order")
    @Severity(SeverityLevel.NORMAL)
    public void placeOrderWithAndThenChaining() {
        Order request = ObjectFactory.validOrder();

        Order created = storeSteps.placeOrderWithAndThen(request);

        storeSteps.validateOrder(created, request);
    }

    @Test
    @Story("Get order by id")
    @Severity(SeverityLevel.NORMAL)
    public void placeAndFetchOrderById() {
        Order created = storeSteps.placeOrder(ObjectFactory.validOrder());

        Order fetched = storeSteps.getOrderById(created.getId());

        storeSteps.validateSameOrder(fetched, created);
    }

    @Test
    @Story("Get order by id")
    @Severity(SeverityLevel.MINOR)
    public void getUnknownOrderReturnsNotFound() {
        storeSteps.verifyOrderNotFound(Constants.UNKNOWN_ORDER_ID);
    }

    @Test
    @Story("Inventory")
    @Severity(SeverityLevel.MINOR)
    public void getInventoryReturnsNonEmptyMap() {
        Map<String, Integer> inventory = storeSteps.getInventory();

        assertThat("Inventory must not be empty", inventory, not(anEmptyMap()));
    }
}
