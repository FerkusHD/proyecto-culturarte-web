package com.culturarte.web.filter;

import com.culturarte.web.service.AccesoService;
import com.culturarte.web.util.UserAgentParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@Order(1) 
public class AccesoFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AccesoFilter.class);

    @Autowired
    private AccesoService accesoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Obtener la IP del cliente
            String ip = obtenerIpCliente(request);
            
            // Obtener la URL completa accedida
            String url = obtenerUrlCompleta(request);
            
            // Obtener el User-Agent
            String userAgent = request.getHeader("User-Agent");
            if (userAgent == null) {
                userAgent = "";
            }
            
            // Parsear el navegador y sistema operativo desde el User-Agent
            String browser = UserAgentParser.parseBrowser(userAgent);
            String sistemaOperativo = UserAgentParser.parseOperatingSystem(userAgent);
            
            logger.debug("Acceso registrado: IP={}, URL={}, Browser={}, SO={}", ip, url, browser, sistemaOperativo);
            
            // Registrar el acceso de forma asíncrona (no bloquea la respuesta)
            accesoService.registrarAcceso(ip, url, browser, sistemaOperativo);
            
            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error en AccesoFilter", e);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Obtiene la dirección IP real del cliente, considerando proxies y load balancers.
     * 
     * @param request HttpServletRequest
     * @return Dirección IP del cliente
     */
    private String obtenerIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // Si hay múltiples IPs (proxies), tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip != null ? ip : "unknown";
    }

    /**
     * Obtiene la URL completa accedida, incluyendo query parameters.
     * 
     * @param request HttpServletRequest
     * @return URL completa
     */
    private String obtenerUrlCompleta(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        
        if (queryString != null) {
            return requestURL.append("?").append(queryString).toString();
        }
        
        return requestURL.toString();
    }
}

