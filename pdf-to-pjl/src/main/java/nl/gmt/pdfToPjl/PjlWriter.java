package nl.gmt.pdfToPjl;

import java.io.IOException;
import java.io.OutputStream;

public class PjlWriter {
    private final OutputStream os;

    public PjlWriter(OutputStream os) {
        this.os = os;
    }
    
    public void writeEscape(boolean last) throws IOException {
        String escape = "\u001B%-12345X";
        if (!last) {
            escape += "@PJL";
        }
        escape += "\n";

        os.write(escape.getBytes("ASCII"));
    }

    public void beginJob(String name, String display) throws IOException {
        writeStatement(String.format("JOB NAME = \"%s\" DISPLAY = \"%s\"", name, display));
    }

    public void enterLanguage(String language) throws IOException {
        writeStatement(String.format("ENTER LANGUAGE = %s", language));
    }

    public void set(String key, String value) throws IOException {
        writeStatement(String.format("SET %s = %s", key, value));
    }

    private void writeStatement(String text) throws IOException {
        os.write(("@PJL " + text + "\n").getBytes("ASCII"));
    }

    public void reset() throws IOException {
        writeStatement("RESET");
    }

    public void endJob(String name) throws IOException {
        writeStatement(String.format("EOJ NAME = \"%s\"", name));
    }

/*
    %-12345X@PJL
    @PJL JOB NAME = "paultest2.pdf" DISPLAY = "Printing & Stapling paultest2.pdf"
    @PJL SET OUTBIN=OPTIONALOUTBIN1
    @PJL SET PROCESSINGTYPE="STAPLING"
    @PJL SET PROCESSINGOPTION="LEFT_1PT_ANGLED"
    @PJL SET MEDIASOURCE = TRAY2
    @PJL ENTER LANGUAGE = PDF*/

}
