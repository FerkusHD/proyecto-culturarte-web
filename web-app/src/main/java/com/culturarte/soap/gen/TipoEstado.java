//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.05 a las 04:11:41 PM GMT-03:00 
//


package com.culturarte.soap.gen;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipoEstado.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tipoEstado"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="INGRESADA"/&gt;
 *     &lt;enumeration value="PUBLICADA"/&gt;
 *     &lt;enumeration value="EN_FINANCIACION"/&gt;
 *     &lt;enumeration value="FINANCIADA"/&gt;
 *     &lt;enumeration value="CANCELADA"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tipoEstado", namespace = "http://www.culturarte.com/ws/tipos")
@XmlEnum
public enum TipoEstado {

    INGRESADA,
    PUBLICADA,
    EN_FINANCIACION,
    FINANCIADA,
    CANCELADA;

    public String value() {
        return name();
    }

    public static TipoEstado fromValue(String v) {
        return valueOf(v);
    }

}
