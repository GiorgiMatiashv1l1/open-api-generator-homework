package ge.tbc.testautomation.data;

import com.github.javafaker.Faker;
import ge.tbc.testautomation.data.model.authservice.LoginRequest;
import ge.tbc.testautomation.data.model.authservice.RefreshTokenRequest;
import ge.tbc.testautomation.data.model.authservice.RegisterUserRequest;
import ge.tbc.testautomation.data.model.petstore.Category;
import ge.tbc.testautomation.data.model.petstore.Order;
import ge.tbc.testautomation.data.model.petstore.Pet;
import ge.tbc.testautomation.data.model.petstore.Tag;
import ge.tbc.testautomation.data.model.soap.EmployeeInfo;
import ge.tbc.testautomation.db.model.EmployeeRow;
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

    public static RegisterUserRequest adminRegisterRequest() {
        return registerRequest(uniqueEmail("admin"), Constants.VALID_PASSWORD, RegisterUserRequest.RoleEnum.ADMIN);
    }

    public static RegisterUserRequest registerRequest(String email, String password, RegisterUserRequest.RoleEnum role) {
        return new RegisterUserRequest()
                .firstname(FAKER.name().firstName())
                .lastname(FAKER.name().lastName())
                .email(email)
                .password(password)
                .role(role);
    }

    public static LoginRequest authenticationRequest(String email, String password) {
        return new LoginRequest()
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

    public static EmployeeRow employeeRow(long employeeId) {
        EmployeeRow row = new EmployeeRow();
        row.setEmployeeId(employeeId);
        row.setName(FAKER.name().fullName());
        row.setDepartment(FAKER.commerce().department());
        row.setPhone(FAKER.phoneNumber().cellPhone());
        row.setAddress(FAKER.address().fullAddress());
        row.setSalary(BigDecimal.valueOf(FAKER.number().numberBetween(3000, 10000)));
        row.setEmail(uniqueEmail("employee"));
        row.setBirthDate(LocalDate.of(1990, 1, 1).plusDays(FAKER.number().numberBetween(0, 10000)));
        return row;
    }

    public static EmployeeInfo toEmployeeInfo(EmployeeRow row) {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setEmployeeId(row.getEmployeeId());
        employeeInfo.setName(row.getName());
        employeeInfo.setDepartment(row.getDepartment());
        employeeInfo.setPhone(row.getPhone());
        employeeInfo.setAddress(row.getAddress());
        employeeInfo.setSalary(row.getSalary());
        employeeInfo.setEmail(row.getEmail());
        employeeInfo.setBirthDate(SoapUtils.toXmlDate(row.getBirthDate()));
        return employeeInfo;
    }

    public static EmployeeRow toEmployeeRow(EmployeeInfo employeeInfo) {
        EmployeeRow row = new EmployeeRow();
        row.setEmployeeId(employeeInfo.getEmployeeId());
        row.setName(employeeInfo.getName());
        row.setDepartment(employeeInfo.getDepartment());
        row.setPhone(employeeInfo.getPhone());
        row.setAddress(employeeInfo.getAddress());
        row.setSalary(employeeInfo.getSalary());
        row.setEmail(employeeInfo.getEmail());
        row.setBirthDate(SoapUtils.toLocalDate(employeeInfo.getBirthDate()));
        return row;
    }
}
