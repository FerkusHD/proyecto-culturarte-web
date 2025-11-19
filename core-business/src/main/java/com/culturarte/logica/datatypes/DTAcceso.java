/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.culturarte.logica.datatypes;

import java.time.LocalDateTime;

/**
 *
 * @author maicol
 */
public class DTAcceso {
    private String id;
    private String ip;
    private String url;
    private String browser;
    private String sistemaOperativo;
    private LocalDateTime fechaAcceso;

    public DTAcceso(String id, String ip, String url, String browser, String sistemaOperativo, LocalDateTime fechaAcceso) {
        this.id = id;
        this.ip = ip;
        this.url = url;
        this.browser = browser;
        this.sistemaOperativo = sistemaOperativo;
        this.fechaAcceso = fechaAcceso;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getFechaAcceso() {
        return fechaAcceso;
    }

    public void setFechaAcceso(LocalDateTime fechaAcceso) {
        this.fechaAcceso = fechaAcceso;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}