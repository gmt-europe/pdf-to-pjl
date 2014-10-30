package nl.gmt.pdfToPjl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class App {
    public static void main(String[] args) {
        try {
            Arguments arguments = new Arguments(args);

            InputStream is;
            if (arguments.getInput() != null) {
                is = new FileInputStream(arguments.getInput());
            } else {
                is = System.in;
            }

            OutputStream os;
            if (arguments.getOutput() != null) {
                os = new FileOutputStream(arguments.getOutput());
            } else {
                os = System.out;
            }

            new PdfToPjl(arguments, is, os).transform();
        } catch (ArgumentsException e) {
            System.err.printf("Invalid arguments: %s%n", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
