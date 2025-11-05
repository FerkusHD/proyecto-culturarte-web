//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.05 a las 03:36:28 PM GMT-03:00 
//


package com.culturarte.soap.gen;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="propuesta" type="{http://www.culturarte.com/ws/propuestas}propuestaType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "propuesta"
})
@XmlRootElement(name = "getPropuestaResponse", namespace = "http://www.culturarte.com/ws/propuestas")
public class GetPropuestaResponse {

    @XmlElement(namespace = "http://www.culturarte.com/ws/propuestas", required = true)
    protected PropuestaType propuesta;

    /**
     * Obtiene el valor de la propiedad propuesta.
     * 
     * @return
     *     possible object is
     *     {@link PropuestaType }
     *     
     */
    public PropuestaType getPropuesta() {
        return propuesta;
    }

    /**
     * Define el valor de la propiedad propuesta.
     * 
     * @param value
     *     allowed object is
     *     {@link PropuestaType }
     *     
     */
    public void setPropuesta(PropuestaType value) {
        this.propuesta = value;
    }

}
