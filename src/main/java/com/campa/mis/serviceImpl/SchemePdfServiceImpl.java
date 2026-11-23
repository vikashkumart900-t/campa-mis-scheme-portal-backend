package com.campa.mis.serviceImpl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.campa.mis.repo.SchemePdfDataRepository;
import com.campa.mis.service.SchemePdfService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemePdfServiceImpl implements SchemePdfService {

	private final SchemePdfDataRepository pdfDataRepository;

	private static final Color NAVY = new Color(24, 49, 83);
	private static final Color BLUE = new Color(39, 91, 145);
	private static final Color LIGHT_BLUE = new Color(232, 240, 248);
	private static final Color LIGHT_GREY = new Color(246, 248, 250);
	private static final Color BORDER = new Color(210, 218, 228);
	private static final Color DARK = new Color(45, 55, 72);
	private static final Color WHITE = Color.WHITE;

	private static final float LEFT = 42;
	private static final float RIGHT = 42;
	private static final float TOP = 55;
	private static final float BOTTOM = 55;

	private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
	private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

	private static final float CONTENT_WIDTH = PAGE_WIDTH - LEFT - RIGHT;

	@Override
	@Transactional(readOnly = true)
	public byte[] generateCompleteSchemePdf(Long schemeId) {

		Map<String, List<Map<String, Object>>> sections = this.pdfDataRepository.fetchAllData(schemeId);

		String schemeName = this.pdfDataRepository.fetchSchemeName(schemeId);

		try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {

			PdfWriter writer = new PdfWriter(document);

			// REPORT HEADER
			writer.addReportTitle(schemeName);

			for (Map.Entry<String, List<Map<String, Object>>> section : sections.entrySet()) {

				writer.addSectionHeading(section.getKey());

				List<Map<String, Object>> records = section.getValue();

				if (records == null || records.isEmpty()) {

					writer.addEmptyMessage();

					continue;
				}

				for (Map<String, Object> record : records) {

					for (Map.Entry<String, Object> field : record.entrySet()) {

						String label = formatColumnName(field.getKey());

						String value = formatValue(field.getValue());

						writer.addTableRow(label, value);
					}

					writer.addRecordSpacing();
				}
			}

			// CLOSE CURRENT PAGE
			writer.close();

			// ADD PAGE NUMBERS
			writer.addPageNumbers();

			document.save(output);

			return output.toByteArray();

		} catch (IOException ex) {

			throw new RuntimeException("Unable to generate consolidated scheme PDF for scheme ID: " + schemeId, ex);
		}
	}

	private String formatColumnName(String column) {

		if (column == null || column.isBlank()) {
			return "";
		}

		String result = column.replace("_", " ").toLowerCase();

		return Character.toUpperCase(result.charAt(0)) + result.substring(1);
	}

	private String formatValue(Object value) {

		if (value == null) {
			return "N/A";
		}

		if (value instanceof Timestamp timestamp) {

			return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		}

		if (value instanceof Date date) {

			return date.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		}

		if (value instanceof byte[]) {
			return "Document attached";
		}

		if (value instanceof Boolean bool) {
			return bool ? "Yes" : "No";
		}

		String result = value.toString().trim();

		return result.isEmpty() ? "N/A" : result;
	}

	private static class PdfWriter {

		private final PDDocument document;

		private PDPageContentStream contentStream;

		private float y;

		private boolean alternateRow = false;

		private PdfWriter(PDDocument document) throws IOException {

			this.document = document;

			newPage();
		}

		// CREATE PAGE
		private void newPage() throws IOException {

			if (contentStream != null) {
				contentStream.close();
			}

			PDPage page = new PDPage(PDRectangle.A4);

			document.addPage(page);

			contentStream = new PDPageContentStream(document, page);

			// TOP HEADER BAND
			contentStream.setNonStrokingColor(NAVY);

			contentStream.addRect(0, PAGE_HEIGHT - 42, PAGE_WIDTH, 42);

			contentStream.fill();

			// HEADER TEXT
			writeRawText("CAMPA MIS | SCHEME PROPOSAL ", LEFT, PAGE_HEIGHT - 26, PDType1Font.HELVETICA_BOLD, 10, WHITE);

			y = PAGE_HEIGHT - TOP - 12;
		}

		// REPORT TITLE
		void addReportTitle(String schemeName) throws IOException {

			ensureSpace(115);

			// writeText("CAMPA SCHEME PROPOSAL", PDType1Font.HELVETICA_BOLD, 17, NAVY);

			y -= 5;

			// writeText("Complete Scheme Information Report", PDType1Font.HELVETICA, 10,
			// BLUE);

			y -= 8;

			drawInformationBox("Scheme-Name", String.valueOf(schemeName), "", "");

			y -= 15;
		}

		// INFORMATION BOX
		private void drawInformationBox(String label1, String value1, String label2, String value2) throws IOException {

			float height = 43;

			ensureSpace(height + 5);

			contentStream.setNonStrokingColor(LIGHT_BLUE);

			contentStream.addRect(LEFT, y - height, CONTENT_WIDTH, height);

			contentStream.fill();

			contentStream.setStrokingColor(BORDER);

			contentStream.addRect(LEFT, y - height, CONTENT_WIDTH, height);

			contentStream.stroke();

			float x1 = LEFT + 12;
			float x2 = LEFT + 265;

			writeRawText(label1, x1, y - 16, PDType1Font.HELVETICA_BOLD, 9, NAVY);

			writeRawText(value1, x1, y - 31, PDType1Font.HELVETICA, 10, DARK);

			writeRawText(label2, x2, y - 16, PDType1Font.HELVETICA_BOLD, 9, NAVY);

			writeRawText(value2, x2, y - 31, PDType1Font.HELVETICA, 9, DARK);

			y -= height;
		}

		// SECTION HEADING
		void addSectionHeading(String heading) throws IOException {

			ensureSpace(45);

			y -= 5;

			float height = 27;

			contentStream.setNonStrokingColor(NAVY);

			contentStream.addRect(LEFT, y - height, CONTENT_WIDTH, height);

			contentStream.fill();

			writeRawText(heading, LEFT + 10, y - 18, PDType1Font.HELVETICA_BOLD, 11, WHITE);

			y -= height + 10;

			alternateRow = false;
		}

		// TABLE ROW
		void addTableRow(String label, String value) throws IOException {

			float labelWidth = 150;
			float valueWidth = CONTENT_WIDTH - labelWidth;

			float padding = 7;
			float fontSize = 8.5f;
			float lineHeight = 12;

			PDFont font = PDType1Font.HELVETICA;

			List<String> labelLines = wrapText(label, PDType1Font.HELVETICA_BOLD, 8, labelWidth - 2 * padding);

			List<String> valueLines = wrapText(value, font, fontSize, valueWidth - 2 * padding);

			int maxLines = Math.max(labelLines.size(), valueLines.size());

			float rowHeight = Math.max(25, maxLines * lineHeight + 2 * padding);

			// Handle unusually large rows across pages
			int offset = 0;

			while (offset < maxLines) {

				float available = y - BOTTOM;

				if (available < 35) {
					newPage();
					available = y - BOTTOM;
				}

				int linesPerPage = Math.max(1, (int) ((available - 2 * padding) / lineHeight));

				int chunkSize = Math.min(maxLines - offset, linesPerPage);

				float chunkHeight = Math.max(25, chunkSize * lineHeight + 2 * padding);

				drawRowChunk(labelLines, valueLines, offset, chunkSize, chunkHeight, labelWidth, padding, lineHeight,
						fontSize);

				offset += chunkSize;
			}
		}

		private void drawRowChunk(List<String> labelLines, List<String> valueLines, int offset, int count, float height,
				float labelWidth, float padding, float lineHeight, float fontSize) throws IOException {

			float bottom = y - height;

			Color background = alternateRow ? LIGHT_GREY : WHITE;

			contentStream.setNonStrokingColor(background);

			contentStream.addRect(LEFT, bottom, CONTENT_WIDTH, height);

			contentStream.fill();

			contentStream.setStrokingColor(BORDER);

			contentStream.setLineWidth(0.5f);

			contentStream.addRect(LEFT, bottom, CONTENT_WIDTH, height);

			contentStream.stroke();

			float dividerX = LEFT + labelWidth;

			contentStream.moveTo(dividerX, bottom);

			contentStream.lineTo(dividerX, y);

			contentStream.stroke();

			float textY = y - padding - fontSize;

			for (int i = 0; i < count; i++) {

				int index = offset + i;

				if (index < labelLines.size()) {

					writeRawText(labelLines.get(index), LEFT + padding, textY, PDType1Font.HELVETICA_BOLD, 8, NAVY);
				}

				if (index < valueLines.size()) {

					writeRawText(valueLines.get(index), dividerX + padding, textY, PDType1Font.HELVETICA, fontSize,
							DARK);
				}

				textY -= lineHeight;
			}

			y = bottom;

			alternateRow = !alternateRow;
		}

		private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {

			List<String> lines = new ArrayList<>();

			if (text == null || text.isBlank()) {
				lines.add("N/A");
				return lines;
			}

			// First split by actual newline
			String[] paragraphs = text.split("\\r?\\n", -1);

			for (String paragraph : paragraphs) {

				// Preserve empty lines
				if (paragraph.isBlank()) {
					lines.add("");
					continue;
				}

				StringBuilder line = new StringBuilder();

				String[] words = paragraph.trim().split("\\s+");

				for (String word : words) {

					if (font.getStringWidth(word) / 1000 * fontSize > maxWidth) {

						if (line.length() > 0) {
							lines.add(line.toString());
							line.setLength(0);
						}

						StringBuilder part = new StringBuilder();

						for (char ch : word.toCharArray()) {

							String candidate = part.toString() + ch;

							if (font.getStringWidth(candidate) / 1000 * fontSize > maxWidth) {

								if (part.length() > 0) {
									lines.add(part.toString());
									part.setLength(0);
								}
							}

							part.append(ch);
						}

						if (part.length() > 0) {
							line.append(part);
						}

						continue;
					}

					String candidate = line.length() == 0 ? word : line + " " + word;

					if (font.getStringWidth(candidate) / 1000 * fontSize > maxWidth) {

						if (line.length() > 0) {
							lines.add(line.toString());
						}

						line.setLength(0);
						line.append(word);

					} else {

						if (line.length() > 0) {
							line.append(" ");
						}

						line.append(word);
					}
				}

				if (line.length() > 0) {
					lines.add(line.toString());
				}
			}

			if (lines.isEmpty()) {
				lines.add("N/A");
			}

			return lines;
		}

		// EMPTY SECTION MESSAGE
		void addEmptyMessage() throws IOException {

			ensureSpace(30);

			writeText("No records found for this section.", PDType1Font.HELVETICA_OBLIQUE, 9, Color.GRAY);

			y -= 8;
		}

		// RECORD SPACING
		void addRecordSpacing() throws IOException {
			y -= 8;
		}

		// GENERAL TEXT
		private void writeText(String text, PDFont font, float fontSize, Color color) throws IOException {

			List<String> lines = wrapText(sanitize(text), font, fontSize, CONTENT_WIDTH);

			for (String line : lines) {

				ensureSpace(fontSize + 7);

				writeRawText(line, LEFT, y, font, fontSize, color);

				y -= fontSize + 6;
			}
		}

		// WRITE SINGLE LINE
		private void writeRawText(String text, float x, float yPosition, PDFont font, float fontSize, Color color)
				throws IOException {

			contentStream.beginText();

			contentStream.setFont(font, fontSize);

			contentStream.setNonStrokingColor(color);

			contentStream.newLineAtOffset(x, yPosition);

			contentStream.showText(sanitize(text));

			contentStream.endText();
		}

		// PAGE SPACE MANAGEMENT
		private void ensureSpace(float required) throws IOException {

			if (y - required < BOTTOM) {
				newPage();
			}
		}

		// PDFBOX STANDARD FONT CHARACTER SUPPORT
		private String sanitize(String value) {

			if (value == null) {
				return "N/A";
			}

			return value.replaceAll("[^\\x20-\\x7E]", "?");
		}

		// PAGE NUMBERS AND FOOTER
		void addPageNumbers() throws IOException {

			int totalPages = document.getNumberOfPages();

			for (int i = 0; i < totalPages; i++) {

				PDPage page = document.getPage(i);

				try (PDPageContentStream footer = new PDPageContentStream(document, page, AppendMode.APPEND, true,
						true)) {

					footer.setStrokingColor(BORDER);

					footer.moveTo(LEFT, 38);

					footer.lineTo(PAGE_WIDTH - RIGHT, 38);

					footer.stroke();

					footer.beginText();

					footer.setFont(PDType1Font.HELVETICA, 8);

					footer.setNonStrokingColor(Color.GRAY);

					footer.newLineAtOffset(LEFT, 25);

					footer.showText("CAMPA Scheme MIS");

					footer.endText();

					String pageText = "Page " + (i + 1) + " of " + totalPages;

					float textWidth = PDType1Font.HELVETICA.getStringWidth(pageText) / 1000 * 8;

					footer.beginText();

					footer.setFont(PDType1Font.HELVETICA, 8);

					footer.setNonStrokingColor(Color.GRAY);

					footer.newLineAtOffset(PAGE_WIDTH - RIGHT - textWidth, 25);

					footer.showText(pageText);

					footer.endText();
				}
			}
		}

		// CLOSE CONTENT STREAM
		void close() throws IOException {

			if (contentStream != null) {

				contentStream.close();

				contentStream = null;
			}
		}
	}
}
