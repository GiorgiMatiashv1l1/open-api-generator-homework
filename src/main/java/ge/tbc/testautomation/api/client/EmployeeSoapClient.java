package ge.tbc.testautomation.api.client;

import ge.tbc.testautomation.data.Constants;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class EmployeeSoapClient {

    private EmployeeSoapClient() {
    }

    public static Response send(String soapEnvelopeBody) {
        return given()
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SoapAction", "")
                .filter(new AllureRestAssured())
                .filter(new ResponseLoggingFilter())
                .log().ifValidationFails(LogDetail.ALL)
                .body(soapEnvelopeBody)
                .when()
                .post(Constants.SOAP_SERVICE_URL);
    }
}
