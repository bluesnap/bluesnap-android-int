package com.bluesnap.androidapi;

import com.bluesnap.androidapi.models.Card;
import com.bluesnap.androidapi.models.CardType;
import com.bluesnap.androidapi.models.returningshopper.ContactInfo;
import com.bluesnap.androidapi.models.returningshopper.CreditCard;
import com.bluesnap.androidapi.models.returningshopper.CreditCardInfo;
import com.bluesnap.androidapi.services.AndroidUtil;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.bluesnap.androidapi.services.AndroidUtil.getCalendarInstance;

/**
 * Unit tests for card and card type
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CardTest extends TestCase {
    public static final String CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE = "1234123412341238";
    public static final String CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED = "5568111111111116";


    // List of cards from: http://www.freeformatter.com/credit-card-number-generator-validator.html
    // http://www.getnewidentity.com/validate-card.php

    @Test
    public void testValidAmex() throws Exception {
        Card card = new Card();
        //AMEX("3782 8224 6310 005", "1111", "AMEX"), AMEX_FD("341111597241002", "1111", "AMEX")
        String[] validAmex = new String[]{"376140184044485", "370796644125630", "377494679521484", "3782 8224 6310 005", "341111597241002"};
        for (String num : validAmex) {
            card.setNumber(num);
            assertTrue("AMEX luhn invalid", card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("AMEX type mismatch: " + type + " " + num, CardType.AMEX.equals(type));
        }
    }

    @Test
    public void tesValidMC() throws Exception {
        Card card = new Card();
        //MASTERCARD("5105 1051 0510 5100", "111","MASTERCARD"), MASTERCARD_FD("5424180279791732", "111", "MASTERCARD"), MASTERCARD_WERTHER("5555555555554444", "111", "MASTERCARD"), MASTERCARD_SKRILL("5232000000123456","115","MASTERCARD"), MASTERCARD_BRAZIL("5365 2917 2765 9784","111","MASTERCARD"), MAESTR_UK_NOW_MASTERCARD("6759411100000008", "123", "MASTERCARD")
        String[] validMC = new String[]{"5572758886015288", "5522796652320905", "5212330191503840", "5105 1051 0510 5100", "5424180279791732", "5555555555554444", "5365 2917 2765 9784", "6759411100000008"};
        for (String num : validMC) {
            card.setNumber(num);
            assertTrue("MASTERCARD luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("MASTERCARD type mismatch: " + type + " " + num, CardType.MASTERCARD.equals(type));
        }
    }

    @Test
    public void testInValidMC() throws Exception {
        Card card = new Card();
        //MASTERCARD_SKRILL("5232000000123456","115","MASTERCARD")
        String[] validMC = new String[]{"5232000000123456"};
        for (String num : validMC) {
            card.setNumber(num);
            assertFalse("MASTERCARD luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("MASTERCARD type mismatch: " + type + " " + num, CardType.MASTERCARD.equals(type));
        }
    }

    @Test
    public void testValidVisa() throws Exception {
        Card card = new Card();
        //VISA("4111 1111 1111 1111", "111", "VISA"), VISA_DEBIT("4594 4001 0053 3682", "111", "VISA"), VISA_FD("4012 0000 3333 0026", "111", "VISA"), VISA_CREDIT("4263 9826 4026 9299","123","VISA"), VISA_DEBIT_INSUFFECIENT_FUNDS("4917484589897107","111","VISA")
        String[] validMC = new String[]{"4111111111111111" /*15 ones*/, "4916088887594869", "4716836794238927", "4594 4001 0053 3682", "4012 0000 3333 0026", "4263 9826 4026 9299", "4917484589897107", "4973 0100 0000 0004"};
        for (String num : validMC) {
            card.setNumber(num);
            assertTrue("VISA luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("VISA type mismatch: " + type + " " + num, CardType.VISA.equals(type));
        }
    }

    @Test
    public void testInValidVisa() throws Exception {
        Card card = new Card();
        String[] validMC = new String[]{"4111 1111 111" /* 10 ones */, "4111 1111 1111 1111 1111" /* 19 ones */};
        for (String num : validMC) {
            card.setNumber(num);
            assertFalse("InValidVISA luhn valid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("InValidVISA type match: " + type + " " + num, CardType.VISA.equals(type));
        }
    }

    @Test
    public void testValidMaestro() throws Exception {
        Card card = new Card();
        //MAESTR_UK("6759411100000008", "123", "MAESTR_UK")
        String[] validMaestro = new String[]{"6759411100000008"};
        for (String num : validMaestro) {
            card.setNumber(num);
            assertTrue("MAESTR_UK luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("MAESTR_UK type mismatch: " + type + " " + num, CardType.MASTERCARD.equals(type)); // ToDo check if server returns MC
        }
    }

    @Test
    public void testValidDiscover() throws Exception {
        Card card = new Card();
        //DISCOVER("6011 1111 1111 1117", "111", "DISCOVER"), DISCOVER_FD("6011000990139424", "111", "DISCOVER"), DISCOVER_NOW_MASTERCARD("6011 1111 1111 1117", "111", "MASTERCARD")
        String[] validDiscover = new String[]{"6011 1111 1111 1117", "6011000990139424", "6011 1111 1111 1117"};
        for (String num : validDiscover) {
            card.setNumber(num);
            assertTrue("DISCOVER luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("DISCOVER type mismatch: " + type + " " + num, CardType.DISCOVER.equals(type));
        }
    }

    @Test
    public void testInValidSolo() throws Exception {
        Card card = new Card();
        //SOLO("6334 5898 9800 0001", "111", "SOLO")
        String[] validSolo = new String[]{"6334 5898 9800 0001"};
        for (String num : validSolo) {
            card.setNumber(num);
            assertTrue("SOLO luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertFalse("SOLO type match: " + type + " " + num, CardType.UNKNOWN.equals(type));
        }
    }

    @Test
    public void testValidDiners() throws Exception {
        Card card = new Card();
        //DINERS("3600 6666 3333 44", "111", "DINERS")
        String[] validDiners = new String[]{"3600 6666 3333 44"};
        for (String num : validDiners) {
            card.setNumber(num);
            assertTrue("DINERS luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("DINERS type mismatch: " + type + " " + num, CardType.DINERS_CLUB.equals(type));
        }
    }

    @Test
    public void testValidJCB() throws Exception {
        Card card = new Card();
        //JCB("3530 1113 3330 0000", "111", "JCB"), JCB_FD("3566007770017510", "111", "JCB")
        String[] validJCB = new String[]{"3530 1113 3330 0000", "3566007770017510"};
        for (String num : validJCB) {
            card.setNumber(num);
            assertTrue("JCB luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("JCB type mismatch: " + type + " " + num, CardType.JCB.equals(type));
        }
    }

    @Test
    public void testValidCarteBleue() throws Exception {
        Card card = new Card();
        //CARTE_BLEUE("4973 0100 0000 0004", "111", "CARTE_BLEUE"), OLD_CARTE_BLEUE_WHICH_IS_REALLY_NOT("5817 8400 4710 8510", "111", "CARTE_BLEUE")
        String[] validCarteBleue = new String[]{"5817 8400 4710 8510"};
        for (String num : validCarteBleue) {
            card.setNumber(num);
            assertTrue("CARTE_BLEUE luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("CARTE_BLEUE type mismatch: " + type + " " + num, CardType.UNKNOWN.equals(type));
        }
    }

    @Test
    public void testValidChinaUnionPay() throws Exception {
        Card card = new Card();
        //CHINA_UNION_PAY("6240 0086 3140 1148","111","CHINA_UNION_PAY")
        String[] validChinaUnionPay = new String[]{"6240008631401148"};
        for (String num : validChinaUnionPay) {
            card.setNumber(num);
            assertTrue("CHINA_UNION_PAY luhn invalid: " + num, card.validateNumber());
            String type = CardType.getType(card.getNumber());
            Assert.assertTrue("CHINA_UNION_PAY type mismatch: " + type + " " + num, CardType.CHINA_UNION_PAY.equals(type));
        }
    }

    @Test
    public void testInvalidStrings() throws Exception {
        Card card = new Card();
        card.setNumber("abcdef");
        assertFalse(card.validateNumber());
        assertFalse(card.validateAll());
    }

    @Test
    public void testValidateExpiryDate() throws Exception {
        assertTrue("this date should be in the futre", AndroidUtil.isDateInFuture(11, 99));
        Card card = new Card();
        card.setExpMonth(13);
        card.setExpYear(33);
        assertFalse("more than 12 month a year?", card.validateExpiryDate());
        card.setExpMonth(21);
        card.setExpYear(5);
        assertFalse("Date should not be in future:" + card.getExpDate(),
                AndroidUtil.isDateInFuture(card.getExpMonth(), card.getExpYear()));
        assertFalse(card.validateExpiryDate());
        assertFalse(card.validateAll());

        card.setExpMonth(0);
        card.setExpYear(25);
        assertFalse("0 month is invalid", card.validateExpiryDate());
        assertFalse(card.validateAll());

        card.setExpMonth(1);
        card.setExpYear(22);
        assertTrue("have we past the year 2022? ", AndroidUtil.isDateInFuture(card.getExpMonth(), card.getExpYear()));
        assertTrue(card.validateExpiryDate());
    }

    @Test
    public void testCurrentDateValid() throws Exception {
        java.util.Calendar now = getCalendarInstance();
        //return isYearInPast(year) || year == now.get(java.util.Calendar.YEAR) && month < (now.get(java.util.Calendar.MONTH) + 1);
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH) + 1;
        Card card = new Card();
        card.setExpMonth(month);
        card.setExpYear(year);
        assertTrue("the card should be valid in the current month", card.validateExpiryDate());

        year = now.get(java.util.Calendar.YEAR);
        month = now.get(java.util.Calendar.MONTH); //Go to last month
        card.setExpMonth(month);
        card.setExpYear(year);
        assertFalse("card should be invalid if expired last month", card.validateExpiryDate());
        assertFalse(card.validateAll());

    }


    @Test
    public void testFuturePastExpiryDates() throws Exception {
        Card card = new Card();
        card.setExpMonth(java.util.Calendar.DECEMBER);
        card.setExpYear(05);
        assertFalse(AndroidUtil.isDateInFuture(card.getExpMonth(), card.getExpYear()));
        assertFalse(card.validateExpiryDate());
        card.setExpMonth(11);
        card.setExpYear(30);
        assertTrue(AndroidUtil.isDateInFuture(card.getExpMonth(), card.getExpYear()));
        card.setExpMonth(12);
        card.setExpYear(30);
        assertTrue(card.validateExpiryDate());
        card.setExpMonth(1);
        card.setExpYear(30);
        assertTrue(card.validateExpiryDate());
    }

    @Test
    public void validLuhnAndNoType() {
        Card card = new Card();
        card.update(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE, "11/50", "123", "13MAAA", "Homer Ssn");
        assertTrue("this should be a valid luhn", CreditCard.isValidLuhnNumber(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(card.validateNumber());
        assertTrue(card.validateAll());
        assertFalse(card.getType().isEmpty());
        assertTrue("this card type should be unknown", card.getType().equals(CardType.UNKNOWN));
    }

    @Test
    public void testValidateAll() throws Exception {
        Card card = new Card();
        card.update(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE, "11/50", "123", "13MAAA", "Homer Ssn");
        assertTrue("this should be a valid luhn", CreditCard.isValidLuhnNumber(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(card.validateNumber());
        assertTrue(card.validateAll());
    }

    @Test
    public void cardToStringTest() {
        CreditCardInfo creditCardInfo = new CreditCardInfo();
        CreditCard creditCard = creditCardInfo.getCreditCard();
        ContactInfo billingInfo = creditCardInfo.getBillingContactInfo();
        String number = CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED;
        creditCard.update(number, "11/50", "123");
        billingInfo.setFullName("Homer Ssn");
        assertTrue("this should be a valid luhn", CreditCard.isValidLuhnNumber(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(creditCard.validateNumber());
        assertTrue(creditCard.validateAll());
        assertFalse(creditCard.getCardType().isEmpty());

        String cardToString = creditCard.toString();
        assertFalse("the tostring should not expose number", cardToString.contains(number));
        String last4 = number.substring(number.length() - 4, number.length());
        assertEquals("Last 4 contains too many digits", creditCard.getCardLastFourDigits().length(), 4);
        assertEquals("Last 4 contains too many digits: " + last4, last4.length(), 4);
        assertTrue("the tostring should containg last4", cardToString.contains(last4));
        assertFalse("card tostring should not expose name", cardToString.contains(billingInfo.getFullName()));
    }
}