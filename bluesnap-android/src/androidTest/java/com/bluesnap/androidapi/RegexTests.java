package com.bluesnap.androidapi;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Created by shevie.chen on 3/26/2018.
 */

@RunWith(AndroidJUnit4.class)
public class RegexTests extends BSAndroidIntegrationTestsBase {

    private static final String TAG = RegexTests.class.getSimpleName();


    @Before
    public void setup() {
        Log.i(TAG, "=============== Starting Regex tests ==================");
    }

    @Test
    public void check_regex_order() {

        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();

        String ccType = creditCardTypeResolver.getType("4111111111111111");
        assertEquals(CreditCardTypeResolver.VISA, ccType);

        ccType = creditCardTypeResolver.getType("401178");
        assertEquals(CreditCardTypeResolver.ELO, ccType);

        ccType = creditCardTypeResolver.getType("4011 78");
        assertEquals(CreditCardTypeResolver.ELO, ccType);

        ccType = creditCardTypeResolver.getType("40111");
        assertEquals(CreditCardTypeResolver.VISA, ccType);

        ccType = creditCardTypeResolver.getType("606282");
        assertEquals(CreditCardTypeResolver.HIPERCARD, ccType);

        ccType = creditCardTypeResolver.getType("603493");
        assertEquals(CreditCardTypeResolver.CENCOSUD, ccType);

        ccType = creditCardTypeResolver.getType("589562");
        assertEquals(CreditCardTypeResolver.NARANJA, ccType);

        ccType = creditCardTypeResolver.getType("603488");
        assertEquals(CreditCardTypeResolver.TARJETASHOPPING, ccType);

        ccType = creditCardTypeResolver.getType("501105");
        assertEquals(CreditCardTypeResolver.ARGENCARD, ccType);

        ccType = creditCardTypeResolver.getType("627170");
        assertEquals(CreditCardTypeResolver.CABAL, ccType);

        ccType = creditCardTypeResolver.getType("51111");
        assertEquals(CreditCardTypeResolver.MASTERCARD, ccType);

        ccType = creditCardTypeResolver.getType("324412");
        assertEquals(CreditCardTypeResolver.AMEX, ccType);

        ccType = creditCardTypeResolver.getType("38989");
        assertEquals(CreditCardTypeResolver.DISCOVER, ccType);

        ccType = creditCardTypeResolver.getType("30511");
        assertEquals(CreditCardTypeResolver.DINERS, ccType);

        ccType = creditCardTypeResolver.getType("2131");
        assertEquals(CreditCardTypeResolver.JCB, ccType);

        // 6280133333333333 matches MASTERCARD as well as CHINA_UNION_PAY
        // This way we assert that the order is correct
        ccType = creditCardTypeResolver.getType("6280133333333333");
        assertEquals(CreditCardTypeResolver.MASTERCARD, ccType);

        ccType = creditCardTypeResolver.getType("6240123456789123");
        assertEquals(CreditCardTypeResolver.CHINA_UNION_PAY, ccType);

        // 36123 matches DINERS as well as CARTE_BLEUE
        // This way we assert that the order is correct
        ccType = creditCardTypeResolver.getType("36123");
        assertEquals(CreditCardTypeResolver.DINERS, ccType);

        ccType = creditCardTypeResolver.getType("5641821111166669");
        assertEquals(CreditCardTypeResolver.CARTE_BLEUE, ccType);
    }

}
