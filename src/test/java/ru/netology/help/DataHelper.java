package ru.netology.help;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static final Faker faker = new Faker(Locale.ENGLISH);
    private static final Faker fakerWithCyrillicLocale = new Faker(new Locale("ru", "RU"));

    @Value
    public static class CardData {
        private final String number;
        private final String month;
        private final String year;
        private final String holder;
        private final String cvc;
    }

    public static CardData getValidApprovedCard() {
        return new CardData(getNumberByStatus("approved"), generateMonth(1), generateYear(2),
                generateValidHolder(), generateValidCVC());
    }

    public static String getNumberByStatus(String status) {
        if (status.equalsIgnoreCase("APPROVED")) {
            return "444444444444444";
        }
        return null;
    }

    public static String generateInvalidCardNumberWith15Digits() {
        return faker.numerify("444444#########");
    }

    public static String generateInvalidValidCardNumberWith17Digits() {
        return faker.numerify("444444############");
    }

    public static String generateInvalidCardNumberWithRandomSymbols() {
        return faker.letterify("???? ???? ???? ????");
    }

    public static String generateMonth(int shiftMonth) {
        return LocalDate.now().plusMonths(shiftMonth).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateMonthWithRandomSymbols() {
        return faker.letterify("??");
    }

    public static String generateYear(int shiftYear) {
        return LocalDate.now().plusYears(shiftYear).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateValidHolder() {
        return faker.name().fullName().toUpperCase();
    }

    public static String generateValidHolderWithDoubleLastName() {
        return faker.name().lastName().toUpperCase() + "-" + faker.name().lastName().toUpperCase() + " "
                + faker.name().firstName().toUpperCase();
    }

    public static String generateInvalidHolderWithCyrillicSymbols() {
        return fakerWithCyrillicLocale.name().firstName().toUpperCase() + " "
                + fakerWithCyrillicLocale.name().lastName().toUpperCase();
    }

    public static String generateHolderWithInvalidSymbols() {
        return faker.numerify("#### #### #### ####");
    }

    public static String generateValidCVC() {
        return faker.numerify("###");
    }

    public static String generateInvalidCVCWith2Digit() {
        return faker.numerify("##");
    }

    public static String generateInvalidCVCWithRandomSymbols() {
        return faker.letterify("???");
    }

    public static String generateRandomOneDigit() {
        return faker.numerify("#");
    }
}
