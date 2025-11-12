package com.culturarte.web.service;

import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generar constancias de pago en PDF usando iText.
 * 
 * Según el requisito 7.2, el PDF debe ser generado dinámicamente
 * cada vez que se solicita desde el Sitio Web.
 */
@Service
public class PDFService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Genera un PDF con la constancia de pago de una colaboración.
     * 
     * @param colaboracion Información de la colaboración
     * @param colaborador Información del colaborador
     * @param propuesta Información de la propuesta
     * @return Array de bytes con el contenido del PDF
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public byte[] generarConstanciaPago(DTColaboracion colaboracion, 
                                       DTColaborador colaborador, 
                                       DTPropuesta propuesta) throws Exception {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Título del documento
            Paragraph titulo = new Paragraph("CONSTANCIA DE PAGO DE COLABORACIÓN")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(titulo);

            // Información del colaborador
            Paragraph seccionColaborador = new Paragraph("DATOS DEL COLABORADOR")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(15)
                    .setMarginBottom(10);
            document.add(seccionColaborador);

            Table tablaColaborador = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            agregarFilaTabla(tablaColaborador, "Nombre:", 
                    colaborador.getNombre() + " " + colaborador.getApellido());
            agregarFilaTabla(tablaColaborador, "Nickname:", colaborador.getNickname());
            agregarFilaTabla(tablaColaborador, "Email:", colaborador.getEmail());
            
            document.add(tablaColaborador);

            // Información de la colaboración
            Paragraph seccionColaboracion = new Paragraph("DATOS DE LA COLABORACIÓN")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(10)
                    .setMarginBottom(10);
            document.add(seccionColaboracion);

            Table tablaColaboracion = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            agregarFilaTabla(tablaColaboracion, "Monto:", 
                    String.format("$%.2f", colaboracion.getMonto()));
            
            String fechaStr = colaboracion.getFecha() != null 
                    ? colaboracion.getFecha().format(DATE_FORMATTER) 
                    : "N/A";
            agregarFilaTabla(tablaColaboracion, "Fecha:", fechaStr);
            
            String horaStr = colaboracion.getHora() != null 
                    ? colaboracion.getHora().format(TIME_FORMATTER) 
                    : "N/A";
            agregarFilaTabla(tablaColaboracion, "Hora:", horaStr);
            
            String tipoRetornoStr = colaboracion.getTipoRetorno() != null 
                    ? colaboracion.getTipoRetorno().toString() 
                    : "N/A";
            agregarFilaTabla(tablaColaboracion, "Tipo de Retorno:", tipoRetornoStr);
            
            document.add(tablaColaboracion);

            // Información de la propuesta
            if (propuesta != null) {
                Paragraph seccionPropuesta = new Paragraph("DATOS DE LA PROPUESTA")
                        .setFontSize(14)
                        .setBold()
                        .setMarginTop(10)
                        .setMarginBottom(10);
                document.add(seccionPropuesta);

                Table tablaPropuesta = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                        .useAllAvailableWidth()
                        .setMarginBottom(15);

                agregarFilaTabla(tablaPropuesta, "Título:", propuesta.getTitulo());
                
                if (propuesta.getDescripcion() != null && !propuesta.getDescripcion().isEmpty()) {
                    String descripcion = propuesta.getDescripcion();
                    if (descripcion.length() > 100) {
                        descripcion = descripcion.substring(0, 100) + "...";
                    }
                    agregarFilaTabla(tablaPropuesta, "Descripción:", descripcion);
                }
                
                if (propuesta.getCategoria() != null) {
                    agregarFilaTabla(tablaPropuesta, "Categoría:", propuesta.getCategoria());
                }
                
                if (propuesta.getProponente() != null) {
                    agregarFilaTabla(tablaPropuesta, "Proponente:", propuesta.getProponente());
                }
                
                if (propuesta.getEstadoActual() != null) {
                    agregarFilaTabla(tablaPropuesta, "Estado:", 
                            propuesta.getEstadoActual().toString());
                }
                
                document.add(tablaPropuesta);
            }

            // Pie de página
            Paragraph piePagina = new Paragraph(
                    "Este documento es una constancia de pago generada automáticamente por el sistema Culturarte.\n" +
                    "Fecha de emisión: " + java.time.LocalDate.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30)
                    .setFontColor(ColorConstants.GRAY);
            document.add(piePagina);

        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    /**
     * Agrega una fila a una tabla con dos columnas: etiqueta y valor.
     */
    private void agregarFilaTabla(Table tabla, String etiqueta, String valor) {
        Paragraph etiquetaPara = new Paragraph(etiqueta)
                .setBold()
                .setMargin(5);
        Paragraph valorPara = new Paragraph(valor != null ? valor : "N/A")
                .setMargin(5);
        
        tabla.addCell(etiquetaPara);
        tabla.addCell(valorPara);
    }
}

