package com.culturarte.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidad para parsear el User-Agent string y extraer información
 * sobre el navegador y el sistema operativo.
 */
public class UserAgentParser {

    // Patrones para detectar navegadores
    private static final Pattern CHROME_PATTERN = Pattern.compile("Chrome/([\\d.]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern FIREFOX_PATTERN = Pattern.compile("Firefox/([\\d.]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SAFARI_PATTERN = Pattern.compile("Safari/([\\d.]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern EDGE_PATTERN = Pattern.compile("Edg(?:e|A|iOS)?/([\\d.]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern OPERA_PATTERN = Pattern.compile("OPR/([\\d.]+)|Opera/([\\d.]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern IE_PATTERN = Pattern.compile("MSIE ([\\d.]+)|Trident/.*rv:([\\d.]+)", Pattern.CASE_INSENSITIVE);

    // Patrones para detectar sistemas operativos
    private static final Pattern WINDOWS_PATTERN = Pattern.compile("Windows (?:NT |)([\\d.]+|10|11|Vista|XP)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MACOS_PATTERN = Pattern.compile("Mac OS X ([\\d_]+)|Macintosh", Pattern.CASE_INSENSITIVE);
    private static final Pattern LINUX_PATTERN = Pattern.compile("Linux", Pattern.CASE_INSENSITIVE);
    private static final Pattern ANDROID_PATTERN = Pattern.compile("Android ([\\d.]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern IOS_PATTERN = Pattern.compile("iPhone|iPad|iPod", Pattern.CASE_INSENSITIVE);

    /**
     * Parsea el User-Agent y retorna el nombre del navegador.
     * 
     * @param userAgent String del User-Agent
     * @return Nombre del navegador (Chrome, Firefox, Safari, Edge, Opera, IE, o "Unknown")
     */
    public static String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }

        // Verificar Chrome (debe verificarse antes de Safari porque Chrome contiene "Safari")
        if (CHROME_PATTERN.matcher(userAgent).find() && !EDGE_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = CHROME_PATTERN.matcher(userAgent);
            if (matcher.find()) {
                return "Chrome " + matcher.group(1);
            }
        }

        // Verificar Edge
        if (EDGE_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = EDGE_PATTERN.matcher(userAgent);
            if (matcher.find()) {
                String version = matcher.group(1);
                return "Edge " + (version != null ? version : "");
            }
        }

        // Verificar Firefox
        if (FIREFOX_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = FIREFOX_PATTERN.matcher(userAgent);
            if (matcher.find()) {
                return "Firefox " + matcher.group(1);
            }
        }

        // Verificar Opera
        if (OPERA_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = OPERA_PATTERN.matcher(userAgent);
            if (matcher.find()) {
                String version = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                return "Opera " + (version != null ? version : "");
            }
        }

        // Verificar Internet Explorer
        if (IE_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = IE_PATTERN.matcher(userAgent);
            if (matcher.find()) {
                String version = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                return "IE " + (version != null ? version : "");
            }
        }

        // Verificar Safari (debe ser el último porque otros navegadores pueden contener "Safari")
        if (SAFARI_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = SAFARI_PATTERN.matcher(userAgent);
            if (matcher.find()) {
                return "Safari " + matcher.group(1);
            }
        }

        return "Unknown";
    }

    /**
     * Parsea el User-Agent y retorna el nombre del sistema operativo.
     * 
     * @param userAgent String del User-Agent
     * @return Nombre del sistema operativo (Windows, Mac, Linux, Android, iOS, o "Unknown")
     */
    public static String parseOperatingSystem(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }

        // Verificar Android
        if (ANDROID_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = ANDROID_PATTERN.matcher(userAgent);
            if (matcher.find()) {
                return "Android " + matcher.group(1);
            }
        }

        // Verificar iOS
        if (IOS_PATTERN.matcher(userAgent).find()) {
            if (userAgent.contains("iPad")) {
                return "iOS (iPad)";
            } else if (userAgent.contains("iPhone")) {
                return "iOS (iPhone)";
            } else {
                return "iOS (iPod)";
            }
        }

        // Verificar Windows
        if (WINDOWS_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = WINDOWS_PATTERN.matcher(userAgent);
            if (matcher.find()) {
                String version = matcher.group(1);
                if (version != null) {
                    return "Windows " + version;
                }
            }
            return "Windows";
        }

        // Verificar macOS
        if (MACOS_PATTERN.matcher(userAgent).find()) {
            Matcher matcher = MACOS_PATTERN.matcher(userAgent);
            if (matcher.find() && matcher.group(1) != null) {
                return "Mac OS X " + matcher.group(1).replace("_", ".");
            }
            return "Mac";
        }

        // Verificar Linux
        if (LINUX_PATTERN.matcher(userAgent).find()) {
            return "Linux";
        }

        return "Unknown";
    }
}

