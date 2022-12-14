package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.gson.Gson;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;
import ru.netology.help.DataHelper;
import ru.netology.help.DbHelper;
import ru.netology.page.TripCardPage;
import ru.netology.page.TripFormPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class CreditCardTests {
    private static DataHelper.CardData cardData;
    private static TripCardPage tripCard;
    private static TripFormPage tripForm;
    private static List<DbHelper.PaymentEntity> payments;
    private static List<DbHelper.CreditRequestEntity> credits;
    private static List<DbHelper.OrderEntity> orders;
    private static final Gson gson = new Gson();
    private static final RequestSpecification spec = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(9999)
            .setAccept(ContentType.JSON).setContentType(ContentType.JSON).log(LogDetail.ALL).build();
    private static final String creditUrl = "/credit";

    @BeforeClass
    public void setupClass() {
        DbHelper.setDown();
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true).savePageSource(true));
    }

    @BeforeMethod
    public void setupMethod() {
        open("http://localhost:8080/");
        tripCard = new TripCardPage();
    }

    @AfterMethod
    public void setDownMethod() {
        DbHelper.setDown();
    }

    @AfterClass
    public void setDownClass() {
        SelenideLogger.removeListener("allure");
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldHappyPath() {
        cardData = DataHelper.getValidApprovedCard();

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyNumber() {
        cardData = DataHelper.getValidApprovedCard();
        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm("", cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue("", cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertNumberFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith15DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWith15Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertNumberFieldIsInvalidValue();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldUnsuccessfulWith17DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidValidCardNumberWith17Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationWithErrorNotification();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWithRandomSymbols();
        var matchesNumber = "";

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertNumberFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "";
        var matchesMonth = "";

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldAddingNullInMonthWith1Digit() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateRandomOneDigit();
        var matchesMonth = "0" + month;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldDeleting3DigitInMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = cardData.getMonth() + DataHelper.generateRandomOneDigit();
        var matchesMonth = cardData.getMonth();

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith00InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "00";
        var matchesMonth = month;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsInvalidValue();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWith01InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "01";
        var matchesMonth = month;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWith06InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "06";
        var matchesMonth = month;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWith12InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "12";
        var matchesMonth = month;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith13InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "13";
        var matchesMonth = month;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsInvalidValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonthWithRandomSymbols();
        var matchesMonth = "";

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsEmptyValue();
    }

    @Story("???????????? ???????? ??????")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "";
        var matchesYear = year;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithOneDigitInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateRandomOneDigit();
        var matchesYear = year;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsInvalidValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldDeletingThirdDigitInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = cardData.getYear() + DataHelper.generateRandomOneDigit();
        var matchesYear = cardData.getYear();

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithPrevYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateYear(-1);
        var matchesYear = year;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsInvalidValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithPrevMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonth(-1);
        var matchesMonth = month;
        var year = DataHelper.generateYear(0);
        var matchesYear = year;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertMonthFieldIsInvalidValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateMonthWithRandomSymbols();
        var matchesYear = "";

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertYearFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "";
        var matchesHolder = holder;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertHolderFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldSuccessfulWithHyphenInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateValidHolderWithDoubleLastName();
        var matchesHolder = holder;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldAutoUpperCaseInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = cardData.getHolder().toLowerCase();
        var matchesHolder = cardData.getHolder();

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldVisibleNotificationWithCyrillicInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderWithCyrillicSymbols();
        var matchesHolder = "";

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertHolderFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateHolderWithInvalidSymbols();
        var matchesHolder = "";

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertHolderFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "";
        var matchesCvc = cvc;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertCvcFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith2DigitsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWith2Digit();
        var matchesCvc = cvc;

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertCvcFieldIsInvalidValue();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldSuccessfulWith4DigitsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = cardData.getCvc() + DataHelper.generateRandomOneDigit();
        var matchesCvc = cardData.getCvc();

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWithRandomSymbols();
        var matchesCvc = "";

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertCvcFieldIsEmptyValue();
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldHappyPathStatus200() {
        cardData = DataHelper.getValidApprovedCard();
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyBody() {
        cardData = DataHelper.getValidApprovedCard();
        given().spec(spec)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyNumber() {
        cardData = new DataHelper.CardData(null, DataHelper.generateMonth(1), DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyMonth() {
        cardData = new DataHelper.CardData(DataHelper.getNumberByStatus("approved"), null, DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyYear() {
        cardData = new DataHelper.CardData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1), null,
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyHolder() {
        cardData = new DataHelper.CardData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), null, DataHelper.generateValidCVC());
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldStatus400WithEmptyCvc() {
        cardData = new DataHelper.CardData(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), DataHelper.generateValidHolder(), null);
        var body = gson.toJson(cardData);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = DbHelper.getPayments();
        credits = DbHelper.getCreditsRequest();
        orders = DbHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }
}
