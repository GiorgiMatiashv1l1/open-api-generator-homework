package ge.tbc.testautomation.data;

import com.github.javafaker.Faker;
import ge.tbc.testautomation.data.model.authservice.AuthenticationRequest;
import ge.tbc.testautomation.data.model.authservice.RefreshTokenRequest;
import ge.tbc.testautomation.data.model.authservice.RegisterRequest;
import ge.tbc.testautomation.data.model.petstore.Category;
import ge.tbc.testautomation.data.model.petstore.Order;
import ge.tbc.testautomation.data.model.petstore.Pet;
import ge.tbc.testautomation.data.model.petstore.Tag;
import ge.tbc.testautomation.data.model.soap.EmployeeInfo;
import ge.tbc.testautomation.utils.SoapUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public final class ObjectFactory {

    private static final Faker FAKER = new Faker();

    private ObjectFactory() {
    }

    public static Order validOrder() {
        return new Order()
                .id(FAKER.number().numberBetween(1L, 100000L))
                .petId(Constants.DEFAULT_PET_ID)
                .quantity(FAKER.number().numberBetween(1, 10))
                .shipDate(OffsetDateTime.now())
                .status(Order.StatusEnum.APPROVED)
                .complete(true);
    }

    public static Pet validPet() {
        return new Pet()
                .id(FAKER.number().numberBetween(1L, 100000L))
                .name(FAKER.dog().name())
                .category(new Category().id(1L).name("Dogs"))
                .photoUrls(List.of(FAKER.internet().url()))
                .tags(List.of(new Tag().id(1L).name("cute")))
                .status(Pet.StatusEnum.AVAILABLE);
    }

    public static RegisterRequest adminRegisterRequest() {
        return registerRequest(uniqueEmail("admin"), Constants.VALID_PASSWORD, RegisterRequest.RoleEnum.ADMIN);
    }

    public static RegisterRequest registerRequest(String email, String password, RegisterRequest.RoleEnum role) {
        return new RegisterRequest()
                .firstname(FAKER.name().firstName())
                .lastname(FAKER.name().lastName())
                .email(email)
                .password(password)
                .role(role);
    }

    public static AuthenticationRequest authenticationRequest(String email, String password) {
        return new AuthenticationRequest()
                .email(email)
                .password(password);
    }

    public static RefreshTokenRequest refreshTokenRequest(String refreshToken) {
        return new RefreshTokenRequest().refreshToken(refreshToken);
    }

    public static String uniqueEmail(String prefix) {
        return (prefix + "." + FAKER.internet().uuid().substring(0, 8) + Constants.EMAIL_DOMAIN).toLowerCase();
    }

    public static EmployeeInfo employeeInfo(long employeeId) {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setEmployeeId(employeeId);
        employeeInfo.setName(FAKER.name().fullName());
        employeeInfo.setDepartment(FAKER.commerce().department());
        employeeInfo.setPhone(FAKER.phoneNumber().cellPhone());
        employeeInfo.setAddress(FAKER.address().fullAddress());
        employeeInfo.setSalary(BigDecimal.valueOf(FAKER.number().numberBetween(3000, 10000)));
        employeeInfo.setEmail(uniqueEmail("employee"));
        employeeInfo.setBirthDate(SoapUtils.toXmlDate(LocalDate.of(1990, 1, 1).plusDays(FAKER.number().numberBetween(0, 10000))));
        return employeeInfo;
    }

    public static long uniqueEmployeeId() {
        return FAKER.number().numberBetween(100_000, 999_999);
    }
}
