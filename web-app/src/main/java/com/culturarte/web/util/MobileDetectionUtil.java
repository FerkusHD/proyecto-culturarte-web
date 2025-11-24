package com.culturarte.web.util;

import jakarta.servlet.http.HttpServletRequest;

public class MobileDetectionUtil {

    private static final String[] MOBILE_USER_AGENTS = {
            "Android", "iPhone", "iPad", "iPod", "BlackBerry",
            "Windows Phone", "webOS", "Mobile"
    };

    public static boolean isMobileDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null || userAgent.isEmpty()) {
            return false;
        }

        userAgent = userAgent.toLowerCase();

        for (String mobileAgent : MOBILE_USER_AGENTS) {
            if (userAgent.contains(mobileAgent.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
