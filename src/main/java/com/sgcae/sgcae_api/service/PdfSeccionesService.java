package com.sgcae.sgcae_api.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.sgcae.sgcae_api.entity.Secciones;
import com.sgcae.sgcae_api.repository.SeccionesRepository;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PdfSeccionesService {

    private final SeccionesRepository seccionesRepository;

    // Paleta de colores mejorada
    private static final Color COLOR_PRIMARIO = new Color(128, 0, 64);      // Vino
    private static final Color COLOR_SECUNDARIO = new Color(186, 136, 59);  // Dorado
    private static final Color COLOR_ACENTO = new Color(240, 240, 240);     // Gris claro
    private static final Color COLOR_TEXTO = new Color(51, 51, 51);         // Gris oscuro
    private static final Color COLOR_BLANCO = Color.WHITE;

    public PdfSeccionesService(SeccionesRepository seccionesRepository) {
        this.seccionesRepository = seccionesRepository;
    }

    public byte[] generarPdfSecciones() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 100, 70);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            writer.setPageEvent(new HeaderFooterPageEvent());
            document.open();

            // ---- Título Principal ----
            Font tituloFont = new Font(Font.HELVETICA, 22, Font.BOLD, COLOR_PRIMARIO);
            Paragraph titulo = new Paragraph("Reporte de Secciones", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10);
            document.add(titulo);


            // ---- Obtener secciones ----
            List<Secciones> secciones = seccionesRepository.findAll();

            // ---- Resumen estadístico (opcional pero elegante) ----
            agregarResumen(document, secciones);

            // ---- Crear tabla con diseño mejorado ----
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(20);
            table.setSpacingAfter(20);

            // Anchos de columnas optimizados
            float[] columnWidths = {2.5f, 4f, 3f, 2f};
            table.setWidths(columnWidths);

            // Encabezados con gradiente visual
            String[] headers = {"Número de Sección", "Colonias", "Encargado", "Meta"};
            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, COLOR_BLANCO);

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(COLOR_PRIMARIO);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(12);
                cell.setBorderWidth(0);
                table.addCell(cell);
            }

            // Filas con alternancia de colores
            Font cellFont = new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_TEXTO);
            boolean alternar = false;

            for (Secciones s : secciones) {
                Color bgColor = alternar ? COLOR_ACENTO : COLOR_BLANCO;

                addStyledCell(table, String.valueOf(s.getNumero_seccion()), cellFont, bgColor, Element.ALIGN_CENTER);
                addStyledCell(table, s.getColonias(), cellFont, bgColor, Element.ALIGN_CENTER);
                addStyledCell(table, s.getEncargado(), cellFont, bgColor, Element.ALIGN_CENTER);
                addStyledCell(table, String.valueOf(s.getMeta()), cellFont, bgColor, Element.ALIGN_CENTER);

                alternar = !alternar;
            }

            document.add(table);

            // ---- Pie de documento (opcional) ----
            agregarNotaFinal(document, secciones.size());

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de secciones", e);
        }
    }

    // ---- Método auxiliar para celdas con estilo ----
    private void addStyledCell(PdfPTable table, String texto, Font font, Color bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(10);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(220, 220, 220));
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    // ---- Resumen estadístico ----
    private void agregarResumen(Document document, List<Secciones> secciones) throws DocumentException {
        int totalSecciones = secciones.size();
        int metaTotal = secciones.stream().mapToInt(Secciones::getMeta).sum();

        PdfPTable resumenTable = new PdfPTable(2);
        resumenTable.setWidthPercentage(50);
        resumenTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        resumenTable.setSpacingAfter(15);

        Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, COLOR_TEXTO);
        Font valueFont = new Font(Font.HELVETICA, 14, Font.BOLD, COLOR_PRIMARIO);

        addResumenCell(resumenTable, "Total de Secciones", String.valueOf(totalSecciones), labelFont, valueFont);
        addResumenCell(resumenTable, "Meta Global", String.valueOf(metaTotal), labelFont, valueFont);

        document.add(resumenTable);
    }

    private void addResumenCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(COLOR_SECUNDARIO);
        cell.setBorderWidth(1.5f);
        cell.setPadding(15);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", labelFont));
        p.add(new Chunk(value, valueFont));
        p.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(p);
        table.addCell(cell);
    }

    // ---- Nota final ----
    private void agregarNotaFinal(Document document, int totalSecciones) throws DocumentException {
        Font notaFont = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(128, 128, 128));
        Paragraph nota = new Paragraph(
                "Este reporte contiene " + totalSecciones + " sección(es) registrada(s) en el sistema.",
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
                e.printStackTrace();
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
            cb.lineTo(rect.getLeft() + 600, rect.getTop() - 60);
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

            // Acento
            cb.setColorFill(COLOR_SECUNDARIO);
            cb.rectangle(rect.getLeft(), rect.getBottom() + 35, rect.getWidth(), 5);
            cb.fill();

            // Número de página
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_CENTER,
                    new Phrase("Página " + writer.getPageNumber(),
                            new Font(Font.HELVETICA, 9, Font.NORMAL, COLOR_BLANCO)),
                    (rect.getLeft() + rect.getRight()) / 2,
                    rect.getBottom() + 18,
                    0
            );

            // Información adicional
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
        }
    }
}