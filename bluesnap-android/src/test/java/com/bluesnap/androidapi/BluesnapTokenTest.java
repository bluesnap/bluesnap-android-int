package com.bluesnap.androidapi;

import android.text.TextUtils;
import com.bluesnap.androidapi.services.BluesnapToken;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by oz on 19/10/16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BluesnapTokenTest extends TestCase {


    @Test(expected = IllegalArgumentException.class)
    public void emptyTokenTest() {
        BluesnapToken token = new BluesnapToken("", null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void spacedTokenTest() {
        BluesnapToken token = new BluesnapToken("    ", null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shortIllegalTokenTest() {
        BluesnapToken token = new BluesnapToken("123456789", null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void notProductionTokenTest() {
        BluesnapToken token1 = new BluesnapToken("123456789101115", null);
        assertFalse("not a production token", token1.isProduction());
        BluesnapToken token2 = new BluesnapToken("12345678910111_", null);
        assertFalse("not a production token", token2.isProduction());

    }

    @Test
    public void notProductionLeagalTokenTest() {
        BluesnapToken token2 = new BluesnapToken("12345678910111_", null);
        assertFalse("not a production token", token2.isProduction());
        assertFalse("toString failed", TextUtils.isEmpty(token2.toString()));
    }

    @Test
    public void isProductionTokenTest() {
        BluesnapToken token1 = new BluesnapToken("123456789101111", null);
        assertTrue("is a production token", token1.isProduction());
        assertFalse("toString failed", TextUtils.isEmpty(token1.toString()));

        BluesnapToken token2 = new BluesnapToken("123456789101112", null);
        assertTrue("is a production token", token2.isProduction());
        assertFalse("toString failed", TextUtils.isEmpty(token2.toString()));
    }

}

