package niffler.test;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.AllureId;
import niffler.jupiter.annotation.GenerateCategory;
import niffler.jupiter.annotation.GenerateSpend;
import niffler.model.CurrencyValues;
import niffler.model.SpendJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Disabled
public class SpendsWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue("Aleksandr");
        $("input[name='password']").setValue("12345");
        $("button[type='submit']").click();
    }

    @GenerateCategory(
            category = "Автомобили",
            username = "Aleksandr"
    )

    @GenerateSpend(
        username = "Aleksandr",
        description = "Покупка автомобиля Cadillac",
        currency = CurrencyValues.RUB,
        amount = 5200000.00,
        category = "Автомобили"
    )

    @AllureId("101")
    @Test
    void spendShouldBeDeletedByActionInTable(SpendJson spend) {
        $(".spendings-table tbody").$$("tr")
            .find(text(spend.getDescription()))
            .$$("td").first()
            .scrollTo()
            .click();

        $$(".button_type_small").find(text("Delete selected"))
            .click();

        $(".spendings-table tbody")
            .$$("tr")
            .shouldHave(CollectionCondition.size(0));

//        throw new IllegalStateException();
    }
}
