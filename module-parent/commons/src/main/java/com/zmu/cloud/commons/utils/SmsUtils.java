package com.zmu.cloud.commons.utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA 2018.1.5
 *
 * @DESCRIPTION: SmsUtils
 * @Date 2018-06-30 14:17
 */

public class SmsUtils {

  public static final Random RANDOM = new Random();

  public static int randomCode() {
    return RANDOM.nextInt(899999) + 100000;
  }

  public static boolean verifyPhone(String str) {
    return PhoneUtils.verifyPhone(str);
  }

}
