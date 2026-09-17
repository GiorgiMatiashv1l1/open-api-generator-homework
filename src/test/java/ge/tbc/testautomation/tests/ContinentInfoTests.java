package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.data.Constants;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Country info SOAP service")
@Feature("List of continents")
public class ContinentInfoTests {

    private XmlPath xmlPath;

    @BeforeClass(alwaysRun = true)
    public void fetchContinents() {
        Response response = given()
                .when()
                .get(Constants.CONTINENT_INFO_URL)
                .then()
                .statusCode(Constants.SC_OK)
                .extract().response();

        xmlPath = response.xmlPath();
    }

    @Test
    @Story("Continent count")
    @Severity(SeverityLevel.NORMAL)
    @Description("There must be exactly six sName nodes")
    public void countOfAllSNameNodesIsSix() {
        List<String> names = xmlPath.getList("ArrayOftContinent.tContinent.sName", String.class);
        assertThat(names.size(), Matchers.equalTo(Constants.EXPECTED_CONTINENTS.size()));
    }

    @Test
    @Story("Continent names")
    @Severity(SeverityLevel.NORMAL)
    @Description("The list of sName values must match the expected continents")
    public void listOfAllSNameValuesMatchesExpected() {
        List<String> names = xmlPath.getList("ArrayOftContinent.tContinent.sName", String.class);
        assertThat(names, Matchers.containsInAnyOrder(Constants.EXPECTED_CONTINENTS.toArray()));
    }

    @Test
    @Story("Continent order")
    @Severity(SeverityLevel.MINOR)
    @Description("The last tContinent node's sName must be The Americas")
    public void lastTContinentSNameValue() {
        List<String> names = xmlPath.getList("ArrayOftContinent.tContinent.sName", String.class);
        assertThat(names.get(names.size() - 1), Matchers.equalTo("The Americas"));
    }

    @Test
    @Story("Continent presence")
    @Severity(SeverityLevel.CRITICAL)
    @Description("All six continents must be present in the response")
    public void allSixContinentsArePresent() {
        List<String> names = xmlPath.getList("ArrayOftContinent.tContinent.sName", String.class);
        assertThat(names, Matchers.hasItems(Constants.EXPECTED_CONTINENTS.toArray(new String[0])));
    }

    @Test
    @Story("Continent names")
    @Severity(SeverityLevel.NORMAL)
    @Description("No sName value may contain numeric characters")
    public void noSNameContainsNumericCharacters() {
        List<String> names = xmlPath.getList("ArrayOftContinent.tContinent.sName", String.class);
        names.forEach(name -> assertThat(name, Matchers.not(Matchers.matchesPattern(".*\\d.*"))));
    }

    @Test
    @Story("Continent names")
    @Severity(SeverityLevel.NORMAL)
    @Description("Every sName value must be unique")
    public void everySNameIsUnique() {
        List<String> names = xmlPath.getList("ArrayOftContinent.tContinent.sName", String.class);
        Set<String> uniqueNames = new HashSet<>(names);
        assertThat(uniqueNames.size(), Matchers.equalTo(names.size()));
    }

    @Test
    @Story("Continent codes")
    @Severity(SeverityLevel.NORMAL)
    @Description("The continent whose sCode starts with O must be Ocenania")
    public void sCodeStartingWithOIsOcenania() {
        String name = xmlPath.getString("ArrayOftContinent.tContinent.find { it.sCode.text().startsWith('O') }.sName");
        assertThat(name, Matchers.equalTo("Ocenania"));
    }

    @Test
    @Story("Continent names")
    @Severity(SeverityLevel.NORMAL)
    @Description("Find all sName values starting with A and ending with ca")
    public void sNamesStartingWithAAndEndingWithCa() {
        List<String> names = xmlPath.getList(
                "ArrayOftContinent.tContinent.findAll { it.sName.text().startsWith('A') && it.sName.text().endsWith('ca') }.sName",
                String.class);
        assertThat(names, Matchers.containsInAnyOrder("Africa", "Antarctica"));
    }

    @Test
    @Story("Continent codes")
    @Severity(SeverityLevel.NORMAL)
    @Description("Every sCode value must be two uppercase letters")
    public void sCodeValuesFollowTwoUppercaseLettersPattern() {
        List<String> codes = xmlPath.getList("ArrayOftContinent.tContinent.sCode", String.class);
        codes.forEach(code -> assertThat(code, Matchers.matchesPattern("[A-Z]{2}")));
    }
}
