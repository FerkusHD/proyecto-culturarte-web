//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.05 a las 03:36:28 PM GMT-03:00 
//


package com.culturarte.soap.gen;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipoRetorno.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tipoRetorno"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ENTRADA_GRATIS"/&gt;
 *     &lt;enumeration value="VISITA_GUIADA"/&gt;
 *     &lt;enumeration value="ACCESO_BACKSTAGE"/&gt;
 *     &lt;enumeration value="MERCHANDISING"/&gt;
 *     &lt;enumeration value="MEET_AND_GREET"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tipoRetorno", namespace = "http://www.culturarte.com/ws/tipos")
@XmlEnum
public enum TipoRetorno {

    ENTRADA_GRATIS,
    VISITA_GUIADA,
    ACCESO_BACKSTAGE,
    MERCHANDISING,
    MEET_AND_GREET;

    public String value() {
        return name();
    }

    public static TipoRetorno fromValue(String v) {
        return valueOf(v);
    }

}
