package com.zmu.cloud.commons.utils;


public class InvitationUtil {

    private static final char chars[] = {'V', 'Y', 'P', '5', 'R', 'Q', '6', 'A', 'C', '2', 'L', 'M', 'S', 'X', 'T', 'D', '8', '7', 'B', 'N', 'H', 'E', 'J', 'K', 'Z', 'W', 'U', '3', '9', 'G', 'F'};

    public static String getInvitationCode(Long uid) {
        return genInviteCode(uid, 4);
    }

    public static String genInviteCode(long id, int len) {
        char[] a = new char[len];
        for (int i = 0; i < a.length; i++) {
            int pow = (int) Math.pow(chars.length, i);
            a[i] = charAtStuff((int) (id / pow % chars.length) + i);
        }
        return String.valueOf(a);
    }

    private static char charAtStuff(int index) {
        return index < chars.length ? chars[index] : chars[index - chars.length];
    }

    public static void main(String[] args) {
        System.out.println(getInvitationCode(1L));
    }
}