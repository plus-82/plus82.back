package com.etplus.util;

public class MaskingUtil {

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email; // 잘못된 형식은 그대로 리턴
        }

        String[] parts = email.split("@", 2);
        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.length() <= 2) {
            // 아이디가 짧을 경우 한 글자만 보이고 나머지는 마스킹
            return localPart.charAt(0) + "****@" + domainPart;
        } else {
            // 앞 2글자만 보이고 나머지는 마스킹
            String visible = localPart.substring(0, 2);
            String masked = "*".repeat(localPart.length() - 2);
            return visible + masked + "@" + domainPart;
        }
    }
}