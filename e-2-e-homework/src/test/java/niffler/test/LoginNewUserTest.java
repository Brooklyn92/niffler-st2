package niffler.test;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import io.qameta.allure.Allure;
import niffler.db.dao.NifflerUsersDAO;
import niffler.db.dao.NifflerUsersDAOHibernate;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

//@Disabled
public class LoginNewUserTest extends BaseWebTest {

  private static Faker faker = new Faker();
  private NifflerUsersDAO usersDAO = new NifflerUsersDAOJdbc();
  private UserEntity ue;

  private static final String TEST_PWD = "12345";

  @BeforeEach
  void createUserForTest() {
    ue = new UserEntity();
    ue.setUsername("Florida22");
    ue.setPassword(TEST_PWD);
    ue.setEnabled(true);
    ue.setAccountNonExpired(true);
    ue.setAccountNonLocked(true);
    ue.setCredentialsNonExpired(true);
    ue.setAuthorities(Arrays.stream(Authority.values()).map(
        a -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setAuthority(a);
          ae.setUser(ue);
          return ae;
        }
    ).toList());
    usersDAO.createUser(ue);
  }

//  @AfterEach
//  void cleanUp() {
//    usersDAO.removeUser(ue);
//  }

  @Test
  void loginTest() throws IOException {
    Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(ue.getUsername());
    $("input[name='password']").setValue(TEST_PWD);
    $("button[type='submit']").click();

    $("a[href*='friends']").click();
    $(".header").should(visible).shouldHave(text("Niffler. The coin keeper."));
  }

}
