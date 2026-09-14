package ge.tbc.testautomation.data;

import com.github.javafaker.Faker;
import org.testng.annotations.DataProvider;

public final class DataProviders {

    private static final Faker FAKER = new Faker();

    private DataProviders() {
    }

    @DataProvider(name = "invalidPasswords")
    public static Object[][] invalidPasswords() {
        return new Object[][]{
                {"valid combination but only 6 characters", "Ab1!" + FAKER.letterify("??")},
                {"valid combination but only 7 characters", "Ab1!" + FAKER.letterify("???")},
                {"no uppercase letter", FAKER.letterify("??????").toLowerCase() + "1!"},
                {"no lowercase letter", FAKER.letterify("??????").toUpperCase() + "1!"},
                {"no digit", "Ab" + FAKER.letterify("?????") + "!"},
                {"no special character", "Ab1" + FAKER.numerify("#####")},
                {"only lowercase letters", FAKER.letterify("??????????").toLowerCase()},
                {"only uppercase letters", FAKER.letterify("??????????").toUpperCase()},
                {"only digits", FAKER.numerify("##########")},
                {"only special characters", "!@#$%^&*()"},
                {"whitespace only", "          "},
                {"single character", "A"},
                {"empty password", ""},
        };
    }
}
