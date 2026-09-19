package ge.tbc.testautomation.api.client;

import ge.tbc.testautomation.api.invoker.petstore.ApiClient;
import ge.tbc.testautomation.api.invoker.petstore.JacksonObjectMapper;
import ge.tbc.testautomation.data.Constants;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

public final class PetStoreApiClient {

    private PetStoreApiClient() {
    }

    public static ApiClient create() {
        ApiClient.Config apiConfig = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .setConfig(config().objectMapperConfig(
                                objectMapperConfig().defaultObjectMapper(JacksonObjectMapper.jackson())))
                        .setBaseUri(Constants.PETSTORE_BASE_URI)
                        .setContentType(ContentType.JSON)
                        .addFilter(new AllureRestAssured())
                        .addFilter(new ResponseLoggingFilter())
                        .log(LogDetail.ALL));

        return ApiClient.api(apiConfig);
    }
}
