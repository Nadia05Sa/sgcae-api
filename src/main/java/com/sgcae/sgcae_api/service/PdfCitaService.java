package com.sgcae.sgcae_api.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.sgcae.sgcae_api.entity.Cita;
import com.sgcae.sgcae_api.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PdfCitaService {

    private final CitaRepository citaRepository;

    // Paleta de colores consistente
    private static final Color COLOR_PRIMARIO = new Color(128, 0, 64);      // Vino
    private static final Color COLOR_SECUNDARIO = new Color(186, 136, 59);  // Dorado
    private static final Color COLOR_ACENTO = new Color(240, 240, 240);     // Gris claro
    private static final Color COLOR_TEXTO = new Color(51, 51, 51);         // Gris oscuro
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_EXITO = new Color(76, 175, 80);        // Verde

    public PdfCitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public byte[] generarPdfCitasCompletadas() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Orientación horizontal para mejor visualización de 8 columnas
            Document document = new Document(PageSize.A4.rotate(), 40, 40, 100, 70);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            writer.setPageEvent(new HeaderFooterPageEvent());
            document.open();

            // ---- Título Principal ----
            Font tituloFont = new Font(Font.HELVETICA, 22, Font.BOLD, COLOR_PRIMARIO);
            Paragraph titulo = new Paragraph("Reporte de Citas Completadas", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(8);
            document.add(titulo);


            // ---- Obtener citas completadas ----
            List<Cita> citas = citaRepository.findByEstado(Cita.Estado.COMPLETADA);

            // ---- Resumen estadístico ----
            agregarResumen(document, citas);

            // ---- Crear tabla con diseño mejorado ----
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setSpacingBefore(20);
            table.setSpacingAfter(20);

            // Anchos de columnas optimizados para orientación horizontal
            float[] columnWidths = {1.2f, 3f, 2.8f, 2.5f, 2f, 2.2f, 2f, 2.3f};
            table.setWidths(columnWidths);

            // Encabezados con diseño moderno
            String[] headers = {
                    "ID", "Nombre Completo", "CURP", "Colonia", "Teléfono",
                    "Usuario", "Apoyo", "Fecha Registro"
            };

            Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, COLOR_BLANCO);

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(COLOR_PRIMARIO);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(10);
                cell.setBorderWidth(0);
                table.addCell(cell);
            }

            // Filas con alternancia de colores
            Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL, COLOR_TEXTO);
            Font cellFontBold = new Font(Font.HELVETICA, 8, Font.BOLD, COLOR_TEXTO);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            boolean alternar = false;

            for (Cita c : citas) {
                Color bgColor = alternar ? COLOR_ACENTO : COLOR_BLANCO;

                // ID (con estilo destacado)
                addStyledCell(table, String.valueOf(c.getIdCita()), cellFontBold, bgColor, Element.ALIGN_CENTER);

                // Nombre
                addStyledCell(table, c.getNombreCompleto(), cellFont, bgColor, Element.ALIGN_LEFT);

                // CURP (mayúsculas y centrado)
                addStyledCell(table, c.getCurp().toUpperCase(), cellFont, bgColor, Element.ALIGN_CENTER);

                // Colonia
                addStyledCell(table, c.getColonia(), cellFont, bgColor, Element.ALIGN_LEFT);

                // Teléfono
                addStyledCell(table, formatearTelefono(c.getTelefono()), cellFont, bgColor, Element.ALIGN_CENTER);

                // Usuario
                String usuario = c.getUsuario() != null ? c.getUsuario().getNombre() : "Sin asignar";
                addStyledCell(table, usuario, cellFont, bgColor, Element.ALIGN_LEFT);

                // Apoyo
                String apoyo = c.getApoyo() != null ? c.getApoyo().getTipo() : "N/A";
                addStyledCell(table, apoyo, cellFont, bgColor, Element.ALIGN_LEFT);

                // Fecha
                String fecha = dateFormat.format(c.getFechaRegistro());
                addStyledCell(table, fecha, cellFont, bgColor, Element.ALIGN_CENTER);

                alternar = !alternar;
            }

            document.add(table);

            // ---- Nota final ----
            agregarNotaFinal(document, citas.size());

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de citas completadas", e);
        }
    }

    // ---- Método auxiliar para celdas con estilo ----
    private void addStyledCell(PdfPTable table, String texto, Font font, Color bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(220, 220, 220));
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    // ---- Formatear teléfono para mejor visualización ----
    private String formatearTelefono(String telefono) {
        if (telefono == null || telefono.length() != 10) {
            return telefono;
        }
        // Formato: (XXX) XXX-XXXX
        return String.format("(%s) %s-%s",
                telefono.substring(0, 3),
                telefono.substring(3, 6),
                telefono.substring(6));
    }

    // ---- Resumen estadístico ----
    private void agregarResumen(Document document, List<Cita> citas) throws DocumentException {
        int totalCitas = citas.size();

        // Conteo por tipo de apoyo
        Map<String, Long> apoyosPorTipo = citas.stream()
                .filter(c -> c.getApoyo() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getApoyo().getTipo(),
                        Collectors.counting()
                ));

        PdfPTable resumenTable = new PdfPTable(3);
        resumenTable.setWidthPercentage(70);
        resumenTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        resumenTable.setSpacingAfter(15);

        Font labelFont = new Font(Font.HELVETICA, 9, Font.BOLD, COLOR_TEXTO);
        Font valueFont = new Font(Font.HELVETICA, 16, Font.BOLD, COLOR_PRIMARIO);

        // Total de citas
        addResumenCell(resumenTable, "Total de Citas", String.valueOf(totalCitas), labelFont, valueFont, COLOR_EXITO);

        // Usuarios únicos
        long usuariosUnicos = citas.stream()
                .filter(c -> c.getUsuario() != null)
                .map(c -> c.getUsuario().getNombre())
                .distinct()
                .count();
        addResumenCell(resumenTable, "Usuarios Activos", String.valueOf(usuariosUnicos), labelFont, valueFont, COLOR_SECUNDARIO);

        // Tipos de apoyo
        addResumenCell(resumenTable, "Tipos de Apoyo", String.valueOf(apoyosPorTipo.size()), labelFont, valueFont, COLOR_PRIMARIO);

        document.add(resumenTable);
    }

    private void addResumenCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont, Color accentColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(accentColor);
        cell.setBorderWidth(2f);
        cell.setPadding(12);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(COLOR_BLANCO);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", labelFont));

        Font coloredValueFont = new Font(valueFont.getFamily(), valueFont.getSize(), valueFont.getStyle(), accentColor);
        p.add(new Chunk(value, coloredValueFont));
        p.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(p);
        table.addCell(cell);
    }

    // ---- Nota final ----
    private void agregarNotaFinal(Document document, int totalCitas) throws DocumentException {
        Font notaFont = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(128, 128, 128));
        Paragraph nota = new Paragraph(
                "Este reporte contiene " + totalCitas + " cita(s) con estado COMPLETADA. " +
                        "Documento generado automáticamente por el Sistema de Gestión de Citas y Apoyos Electorales (SGCAE).",
                notaFont
        );
        nota.setAlignment(Element.ALIGN_CENTER);
        nota.setSpacingBefore(20);
        document.add(nota);
    }

    // ---- Clase para encabezado y pie de página mejorados ----
    private static class HeaderFooterPageEvent extends PdfPageEventHelper {

        private Image headerImage;

        public HeaderFooterPageEvent() {
            try {
                this.headerImage = Image.getInstance("src/main/resources/static/logo.png");
                this.headerImage.scaleAbsolute(120, 40);
            } catch (Exception e) {
                // Logo opcional - continúa sin error si no existe
                System.out.println("Logo no encontrado - continuando sin imagen");
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Rectangle rect = document.getPageSize();

            // ----- Encabezado con diseño moderno -----
            // Fondo principal
            cb.setColorFill(COLOR_SECUNDARIO);
            cb.rectangle(rect.getLeft(), rect.getTop() - 60, rect.getWidth(), 60);
            cb.fill();


            // Línea decorativa
            cb.setColorStroke(COLOR_PRIMARIO);
            cb.setLineWidth(3);
            cb.moveTo(rect.getLeft(), rect.getTop() - 60);
            cb.lineTo(rect.getLeft() + 900, rect.getTop() - 60);
            cb.stroke();

            // Imagen en encabezado
            if (headerImage != null) {
                headerImage.setAbsolutePosition(rect.getLeft() + 30, rect.getTop() - 50);
                try {
                    cb.addImage(headerImage);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }


            // ----- Pie de página minimalista -----
            // Fondo
            cb.setColorFill(COLOR_PRIMARIO);
            cb.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), 40);
            cb.fill();

            // Acento superior
            cb.setColorFill(COLOR_SECUNDARIO);
            cb.rectangle(rect.getLeft(), rect.getBottom() + 35, rect.getWidth(), 5);
            cb.fill();

            // Número de página (centrado)
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_CENTER,
                    new Phrase("Página " + writer.getPageNumber(),
                            new Font(Font.HELVETICA, 9, Font.BOLD, COLOR_BLANCO)),
                    (rect.getLeft() + rect.getRight()) / 2,
                    rect.getBottom() + 18,
                    0
            );

            // Fecha/hora de generación (derecha)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_RIGHT,
                    new Phrase(sdf.format(new Date()),
                            new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(200, 200, 200))),
                    rect.getRight() - 20,
                    rect.getBottom() + 18,
                    0
            );

            // Texto adicional (izquierda)
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_LEFT,
                    new Phrase("Confidencial",
                            new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(200, 200, 200))),
                    rect.getLeft() + 20,
                    rect.getBottom() + 18,
                    0
            );
        }
    }
}