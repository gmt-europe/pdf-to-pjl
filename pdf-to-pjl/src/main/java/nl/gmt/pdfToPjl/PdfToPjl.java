package nl.gmt.pdfToPjl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfToPjl {
    private static final Pattern RE = Pattern.compile("<!\\[PJL\\[(.*?)\\]\\]>");
    private static final Pattern SET_RE = Pattern.compile("^ *SET *(.+?) *= *(.+?) *$");
    private static final Pattern RESET_RE = Pattern.compile("^ *RESET *(.+?) *$");
    private static final Pattern DEFINE_RE = Pattern.compile("\\$\\{(.*?)\\}");

    private final Arguments arguments;
    private final InputStream is;
    private final OutputStream os;
    private final Map<String, String> settings = new HashMap<String, String>();
    private Document document;
    private PdfCopy copy;
    private PjlWriter pjl;
    private PdfReader reader;

    public PdfToPjl(Arguments arguments, InputStream is, OutputStream os) {
        this.arguments = arguments;
        this.is = is;
        this.os = os;
    }

    public void transform() throws IOException, ParseException, DocumentException {
        pjl = new PjlWriter(os);

        pjl.writeEscape(false);
        pjl.beginJob(arguments.getName(), arguments.getDisplay() != null ? arguments.getDisplay() : arguments.getName());

        reader = new PdfReader(is);

        for (int pages = reader.getNumberOfPages(), i = 0; i < pages; i++) {
            Map<String, String> lastSettings = new HashMap<String, String>(settings);

            String text = PdfTextExtractor.getTextFromPage(reader, i + 1);

            Matcher matcher = RE.matcher(text);
            while (matcher.find()) {
                parsePjl(matcher.group(1));
            }

            if (document != null && !settingsEquals(lastSettings,  settings)) {
                endDocument();
            }

            if (document == null) {
                beginDocument();
            }

            copy.addPage(copy.getImportedPage(reader, i + 1));
        }

        endDocument();

        pjl.reset();
        pjl.endJob(arguments.getName());
        pjl.writeEscape(true);
    }

    private boolean settingsEquals(Map<String, String> a, Map<String, String> b) {
        if (a.size() != b.size()) {
            return false;
        }

        for (Map.Entry<String, String> entry : a.entrySet()) {
            String value = b.get(entry.getKey());
            if (value == null || !value.equals(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    private void parsePjl(String value) throws ParseException {
        Matcher matcher = SET_RE.matcher(value);
        if (matcher.find()) {
            settings.put(matcher.group(1), parseSetting(matcher.group(2)));
            return;
        }

        matcher = RESET_RE.matcher(value);
        if (matcher.find()) {
            settings.remove(matcher.group(1));
            return;
        }

        throw new ParseException(String.format("Invalid PJL instruction '%s'", value));
    }

    private String parseSetting(String setting) throws ParseException {
        return new PatternReplacer(DEFINE_RE).replaceMatches(setting, new PatternReplacer.Callback() {
            @Override
            public String foundMatch(MatchResult matchResult) throws ParseException {
                String result = arguments.getDefines().get(matchResult.group(1));
                if (result == null) {
                    throw new ParseException(String.format("Replacement value for definition ${%s} not found", matchResult.group(1)));
                }
                return result;
            }
        });
    }

    private void beginDocument() throws DocumentException, IOException {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            pjl.set(entry.getKey(), entry.getValue());
        }

        pjl.enterLanguage("PDF");

        document = new Document(reader.getPageSizeWithRotation(1));

        copy = new PdfCopy(document, os);
        copy.setCloseStream(false);

        document.open();
    }

    private void endDocument() throws IOException {
        copy.close();
        copy = null;

        document.close();
        document = null;

        pjl.writeEscape(false);
    }
}
