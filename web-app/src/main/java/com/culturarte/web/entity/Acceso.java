package com.culturarte.web.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un acceso al sitio web.
 */
@Entity
@Table(name = "accesos")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String ip;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false, length = 100)
    private String browser;

    @Column(nullable = false, length = 100)
    private String sistemaOperativo;

    @Column(nullable = false)
    private LocalDateTime fechaAcceso;

    public Acceso() {
        this.fechaAcceso = LocalDateTime.now();
    }

    public Acceso(String ip, String url, String browser, String sistemaOperativo) {
        this();
        this.ip = ip;
        this.url = url;
        this.browser = browser;
        this.sistemaOperativo = sistemaOperativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public LocalDateTime getFechaAcceso() {
        return fechaAcceso;
    }

    public void setFechaAcceso(LocalDateTime fechaAcceso) {
        this.fechaAcceso = fechaAcceso;
    }

    @Override
    public String toString() {
        return "Acceso{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", url='" + url + '\'' +
                ", browser='" + browser + '\'' +
                ", sistemaOperativo='" + sistemaOperativo + '\'' +
                ", fechaAcceso=" + fechaAcceso +
                '}';
    }
}

