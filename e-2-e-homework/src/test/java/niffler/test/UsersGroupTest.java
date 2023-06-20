package niffler.test;

import io.qameta.allure.AllureId;
import niffler.jupiter.annotation.User;
import niffler.jupiter.extension.UsersGroupExtension;
import niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static niffler.jupiter.annotation.User.UserType.INVITATION_SENT;
import static niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;

@ExtendWith(UsersGroupExtension.class)
public class UsersGroupTest {
    @AllureId("104")
    @Test
    void friendsShouldBeVisible0(@User(userType = WITH_FRIENDS) UserJson user, @User(userType = INVITATION_SENT) UserJson userJson) {
        System.out.println(user.getUsername());
        System.out.println(userJson.getUsername());
    }
}
