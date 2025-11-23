package com.culturarte.web.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilidad para detectar si la petición proviene de un dispositivo móvil
 */
public class MobileDetectionUtil {

    private static final String[] MOBILE_USER_AGENTS = {
            "Android", "iPhone", "iPad", "iPod", "BlackBerry",
            "Windows Phone", "webOS", "Mobile"
    };

    /**
     * Detecta si la petición proviene de un dispositivo móvil mediante el
     * User-Agent
     * 
     * @param request HttpServletRequest
     * @return true si es un dispositivo móvil, false en caso contrario
     */
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

    /**
     * Obtiene el User-Agent de la petición
     * 
     * @param request HttpServletRequest
     * @return User-Agent string
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
