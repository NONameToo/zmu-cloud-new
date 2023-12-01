package com.zmu.cloud.commons.utils;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: PhoneUtils
 * @Date 2018-11-28 20:45
 */

public class PhoneUtils {

  private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3456789]\\d{9}$");

  public static boolean verifyPhone(String str) {
    boolean b = false;
    if (StringUtils.isNotBlank(str)) {
      b = MOBILE_PATTERN.matcher(str).matches();
    }
    return b;
  }
}
