package com.bluesnap.androidapi;

import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;
import com.bluesnap.androidapi.services.BlueSnapValidator;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.LinkedHashMap;


/**
 * Unit tests for card and card type
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CardTest extends TestCase {
    public static final String CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE = "1234123412341238";
    public static final String CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED = "5568111111111116";

    /**
     * credit Card Regex Linked Hash Map Presentation
     *
     * @return LinkedHashMap of creditCardRegex
     */
    private static LinkedHashMap<String, String> creditCardRegex() {
        LinkedHashMap<String, String> creditCardRegex = new LinkedHashMap<>();
        creditCardRegex.put("ELO", "^(40117[8-9]|431274|438935|451416|457393|45763[1-2]|504175|506699|5067[0-6][0-9]|50677[0-8]|509[0-9][0-9][0-9]|636368|636369|636297|627780).*");
        creditCardRegex.put("HIPERCARD", "^(606282|637095).*");
        creditCardRegex.put("CENCOSUD", "^603493.*");
        creditCardRegex.put("NARANJA", "^589562.*");
        creditCardRegex.put("TARJETASHOPPING", "^(603488|(27995[0-9])).*");
        creditCardRegex.put("ARGENCARD", "^(501105).*");
        creditCardRegex.put("CABAL", "^((627170)|(589657)|(603522)|(604((20[1-9])|(2[1-9][0-9])|(3[0-9]{2})|(400)))).*");
        creditCardRegex.put("VISA", "^4.+");
        creditCardRegex.put("MASTERCARD", "^(5(([1-5])|(0[1-5]))|2(([2-6])|(7(1|20)))|6((0(0[2-9]|1[2-9]|2[6-9]|[3-5]))|(2((1(0|2|3|[5-9]))|20|7[0-9]|80))|(60|3(0|[3-9]))|(4[0-2]|[6-8]))).+");
        creditCardRegex.put("AMEX", "^3(24|4[0-9]|7|56904|379(41|12|13)).+");
        creditCardRegex.put("DISCOVER", "^(3[8-9]|(6((01(1|300))|4[4-9]|5))).+");
        creditCardRegex.put("DINERS", "^(3(0([0-5]|9|55)|6)).*");
        creditCardRegex.put("JCB", "^(2131|1800|35).*");
        creditCardRegex.put("CHINA_UNION_PAY", "(^62(([4-6]|8)[0-9]{13,16}|2[2-9][0-9]{12,15}))$");
        creditCardRegex.put("CARTE_BLEUE", "^((3(6[1-4]|77451))|(4(059(?!34)|150|201|561|562|533|556|97))|(5(0[1-4]|13|30066|341[0-1]|587[0-2]|6|8))|(6(27244|390|75[1-6]|799999998))).*");
        return creditCardRegex;
    }


    // List of cards from: http://www.freeformatter.com/credit-card-number-generator-validator.html
    // http://www.getnewidentity.com/validate-card.php

    @Test
    public void testValidAmex() {
        CreditCard card = new CreditCard();
        //AMEX("3782 8224 6310 005", "1111", "AMEX"), AMEX_FD("341111597241002", "1111", "AMEX")
        String[] validAmex = new String[]{"376140184044485", "370796644125630", "377494679521484", "3782 8224 6310 005", "341111597241002"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validAmex) {
            card.setNumber(num);
            assertTrue("AMEX luhn invalid", BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("AMEX type mismatch: " + type + " " + num, CreditCardTypeResolver.AMEX.equals(type));
        }
    }

    @Test
    public void tesValidMC() {
        CreditCard card = new CreditCard();
        //MASTERCARD("5105 1051 0510 5100", "111","MASTERCARD"), MASTERCARD_FD("5424180279791732", "111", "MASTERCARD"), MASTERCARD_WERTHER("5555555555554444", "111", "MASTERCARD"), MASTERCARD_SKRILL("5232000000123456","115","MASTERCARD"), MASTERCARD_BRAZIL("5365 2917 2765 9784","111","MASTERCARD"), MAESTR_UK_NOW_MASTERCARD("6759411100000008", "123", "MASTERCARD")
        String[] validMC = new String[]{"5572758886015288", "5522796652320905", "5212330191503840", "5105 1051 0510 5100", "5424180279791732", "5555555555554444", "5365 2917 2765 9784", "6759411100000008"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validMC) {
            card.setNumber(num);
            assertTrue("MASTERCARD luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("MASTERCARD type mismatch: " + type + " " + num, CreditCardTypeResolver.MASTERCARD.equals(type));
        }
    }

    @Test
    public void testInValidMC() {
        CreditCard card = new CreditCard();
        //MASTERCARD_SKRILL("5232000000123456","115","MASTERCARD")
        String[] validMC = new String[]{"5232000000123456"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validMC) {
            card.setNumber(num);
            assertFalse("MASTERCARD luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("MASTERCARD type mismatch: " + type + " " + num, CreditCardTypeResolver.MASTERCARD.equals(type));
        }
    }

    @Test
    public void testValidVisa() {
        CreditCard card = new CreditCard();
        //VISA("4111 1111 1111 1111", "111", "VISA"), VISA_DEBIT("4594 4001 0053 3682", "111", "VISA"), VISA_FD("4012 0000 3333 0026", "111", "VISA"), VISA_CREDIT("4263 9826 4026 9299","123","VISA"), VISA_DEBIT_INSUFFECIENT_FUNDS("4917484589897107","111","VISA")
        String[] validMC = new String[]{"4111111111111111" /*15 ones*/, "4916088887594869", "4716836794238927", "4594 4001 0053 3682", "4012 0000 3333 0026", "4263 9826 4026 9299", "4917484589897107", "4973 0100 0000 0004"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validMC) {
            card.setNumber(num);
            assertTrue("VISA luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("VISA type mismatch: " + type + " " + num, CreditCardTypeResolver.VISA.equals(type));
        }
    }

    @Test
    public void testInValidVisa() {
        CreditCard card = new CreditCard();
        String[] validMC = new String[]{"4111 1111 111" /* 10 ones */, "4111 1111 1111 1111 1111" /* 19 ones */};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validMC) {
            card.setNumber(num);
            assertFalse("InValidVISA luhn valid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("InValidVISA type match: " + type + " " + num, CreditCardTypeResolver.VISA.equals(type));
        }
    }

    @Test
    public void testValidMaestro() {
        CreditCard card = new CreditCard();
        //MAESTR_UK("6759411100000008", "123", "MAESTR_UK")
        String[] validMaestro = new String[]{"6759411100000008"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validMaestro) {
            card.setNumber(num);
            assertTrue("MAESTR_UK luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("MAESTR_UK type mismatch: " + type + " " + num, CreditCardTypeResolver.MASTERCARD.equals(type)); // ToDo check if server returns MC
        }
    }

    @Test
    public void testValidDiscover() {
        CreditCard card = new CreditCard();
        //DISCOVER("6011 1111 1111 1117", "111", "DISCOVER"), DISCOVER_FD("6011000990139424", "111", "DISCOVER"), DISCOVER_NOW_MASTERCARD("6011 1111 1111 1117", "111", "MASTERCARD")
        String[] validDiscover = new String[]{"6011 1111 1111 1117", "6011000990139424", "6011 1111 1111 1117"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validDiscover) {
            card.setNumber(num);
            assertTrue("DISCOVER luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("DISCOVER type mismatch: " + type + " " + num, CreditCardTypeResolver.DISCOVER.equals(type));
        }
    }

    @Test
    public void testInValidSolo() {
        CreditCard card = new CreditCard();
        //SOLO("6334 5898 9800 0001", "111", "SOLO")
        String[] validSolo = new String[]{"6334 5898 9800 0001"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validSolo) {
            card.setNumber(num);
            assertTrue("SOLO luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertFalse("SOLO type match: " + type + " " + num, CreditCardTypeResolver.UNKNOWN.equals(type));
        }
    }

    @Test
    public void testValidDiners() {
        CreditCard card = new CreditCard();
        //DINERS("3600 6666 3333 44", "111", "DINERS")
        String[] validDiners = new String[]{"3600 6666 3333 44"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validDiners) {
            card.setNumber(num);
            assertTrue("DINERS luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("DINERS type mismatch: " + type + " " + num, CreditCardTypeResolver.DINERS.equals(type));
        }
    }

    @Test
    public void testValidJCB() {
        CreditCard card = new CreditCard();
        //JCB("3530 1113 3330 0000", "111", "JCB"), JCB_FD("3566007770017510", "111", "JCB")
        String[] validJCB = new String[]{"3530 1113 3330 0000", "3566007770017510"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validJCB) {
            card.setNumber(num);
            assertTrue("JCB luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("JCB type mismatch: " + type + " " + num, CreditCardTypeResolver.JCB.equals(type));
        }
    }

    @Test
    public void testValidCarteBleue() {
        CreditCard card = new CreditCard();
        //CARTE_BLEUE("4973 0100 0000 0004", "111", "CARTE_BLEUE"), OLD_CARTE_BLEUE_WHICH_IS_REALLY_NOT("5817 8400 4710 8510", "111", "CARTE_BLEUE")
        String[] validCarteBleue = new String[]{"5817 8400 4710 8510"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validCarteBleue) {
            card.setNumber(num);
            assertTrue("CARTE_BLEUE luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("CARTE_BLEUE type mismatch: " + type + " " + num, CreditCardTypeResolver.CARTE_BLEUE.equals(type));
        }
    }

    @Test
    public void testValidChinaUnionPay() {
        CreditCard card = new CreditCard();
        //CHINA_UNION_PAY("6240 0086 3140 1148","111","CHINA_UNION_PAY")
        String[] validChinaUnionPay = new String[]{"6240008631401148"};
        CreditCardTypeResolver.setCreditCardRegex(creditCardRegex());
        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        for (String num : validChinaUnionPay) {
            card.setNumber(num);
            assertTrue("CHINA_UNION_PAY luhn invalid: " + num, BlueSnapValidator.creditCardNumberValidation(num));
            String type = creditCardTypeResolver.getType(card.getNumber());
            Assert.assertTrue("CHINA_UNION_PAY type mismatch: " + type + " " + num, CreditCardTypeResolver.CHINA_UNION_PAY.equals(type));
        }
    }

    @Test
    public void testInvalidStrings() {
        CreditCard card = new CreditCard();
        card.setNumber("abcdef");
        assertFalse(BlueSnapValidator.creditCardNumberValidation(card.getNumber()));
        assertFalse(BlueSnapValidator.creditCardFullValidation(card));
    }

    @Test
    public void testValidateExpiryDate() {
        assertTrue("this date should be in the future", BlueSnapValidator.isDateInFuture(11, 25));
        CreditCard card = new CreditCard();
        card.setExpirationMonth(13);
        card.setExpirationYear(33);
        assertFalse("more than 12 month a year?", BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));
        card.setExpirationMonth(21);
        card.setExpirationYear(5);
        assertFalse("Date should not be in future:" + card.getExpirationDate(),
                BlueSnapValidator.isDateInFuture(card.getExpirationMonth(), card.getExpirationYear()));
        assertFalse(BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));
        assertFalse(BlueSnapValidator.creditCardFullValidation(card));

        card.setExpirationMonth(0);
        card.setExpirationYear(29);
        assertFalse("0 month is invalid", BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));
        assertFalse(BlueSnapValidator.creditCardFullValidation(card));

        card.setExpirationMonth(1);
        card.setExpirationYear(29);
        assertTrue("have we past the year 2030? ", BlueSnapValidator.isDateInFuture(card.getExpirationMonth(), card.getExpirationYear()));
        assertTrue(BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));
        card.toString();
    }

    @Test
    public void testCurrentDateValid() {
        java.util.Calendar now = Calendar.getInstance();
        //return isYearInPast(year) || year == now.get(java.util.Calendar.YEAR) && month < (now.get(java.util.Calendar.MONTH) + 1);
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH) + 1;
        CreditCard card = new CreditCard();
        card.setExpirationMonth(month);
        card.setExpirationYear(year);
        assertTrue("the card should be valid in the current month", BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));

        year = now.get(java.util.Calendar.YEAR);
        month = now.get(java.util.Calendar.MONTH); //Go to last month
        card.setExpirationMonth(month);
        card.setExpirationYear(year);
        assertFalse("card should be invalid if expired last month", BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));
        assertFalse(BlueSnapValidator.creditCardFullValidation(card));

    }


    @Test
    public void testFuturePastExpiryDates() {
        CreditCard card = new CreditCard();
        card.setExpirationMonth(java.util.Calendar.DECEMBER);
        card.setExpirationYear(05);
        assertFalse(BlueSnapValidator.isDateInFuture(card.getExpirationMonth(), card.getExpirationYear()));
        assertFalse(BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));
        card.setExpirationMonth(11);
        card.setExpirationYear(25);
        assertTrue(BlueSnapValidator.isDateInFuture(card.getExpirationMonth(), card.getExpirationYear()));
        card.setExpirationMonth(12);
        card.setExpirationYear(25);
        assertTrue(BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));
        card.setExpirationMonth(1);
        card.setExpirationYear(25);
        assertTrue(BlueSnapValidator.creditCardExpiryDateValidation(card.getExpirationYear(), card.getExpirationMonth()));
    }

    @Test
    public void validLuhnAndNoType() {
        CreditCard card = new CreditCard();
        card.update(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE, "11/25", "123");
        //assertTrue("this should be a valid luhn", BlueSnapValidator.isValidLuhnNumber(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(BlueSnapValidator.creditCardNumberValidation(card.getNumber()));
        assertTrue(BlueSnapValidator.creditCardFullValidation(card));
        assertFalse(card.getCardType().isEmpty());
        assertTrue("this card type should be unknown", card.getCardType().equals(CreditCardTypeResolver.UNKNOWN));
    }

    @Test
    public void testValidateAll() {
        CreditCard card = new CreditCard();
        card.update(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE, "11/25", "123");
        //assertTrue("this should be a valid luhn", CreditCard.isValidLuhnNumber(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(BlueSnapValidator.creditCardNumberValidation(card.getNumber()));
        assertTrue(BlueSnapValidator.creditCardFullValidation(card));
    }

    @Test
    public void cardToStringTest() {
        CreditCardInfo creditCardInfo = new CreditCardInfo();
        CreditCard creditCard = creditCardInfo.getCreditCard();
        ContactInfo billingInfo = creditCardInfo.getBillingContactInfo();
        String number = CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED;
        creditCard.update(number, "11/25", "123");
        billingInfo.setFullName("Homer Ssn");
        //assertTrue("this should be a valid luhn", CreditCard.isValidLuhnNumber(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(BlueSnapValidator.creditCardNumberValidation(creditCard.getNumber()));
        assertTrue(BlueSnapValidator.creditCardFullValidation(creditCard));
        assertFalse(creditCard.getCardType().isEmpty());

        String cardToString = creditCard.toString();
        assertFalse("the tostring should not expose number", cardToString.contains(number));
        String last4 = number.substring(number.length() - 4);
        assertEquals("Last 4 contains too many digits", creditCard.getCardLastFourDigits().length(), 4);
        assertEquals("Last 4 contains too many digits: " + last4, last4.length(), 4);
        assertFalse("the tostring should not contain last4", cardToString.contains(last4));
        assertFalse("card tostring should not expose name", cardToString.contains(billingInfo.getFullName()));
    }

    @Test
    public void testEmptyCardStrings() {
        CreditCard card = new CreditCard();
        card.setNumber(null);
        assertTrue("Invalid type", null == card.getCardType());
        assertTrue("Invalid type", null == card.getCardSubType());

        card.setNumber("");
        assertTrue("Invalid type", null == card.getCardType());
        assertTrue("Invalid type", null == card.getCardType());


    }
}