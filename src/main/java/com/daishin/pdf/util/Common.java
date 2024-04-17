package com.daishin.pdf.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Common {

    public static String getCurrnetYYYYMM() {
        SimpleDateFormat sdfyyyymm = new SimpleDateFormat("yyyyMM");
        Date now = new Date();
        return sdfyyyymm.format(now);
    }

}
