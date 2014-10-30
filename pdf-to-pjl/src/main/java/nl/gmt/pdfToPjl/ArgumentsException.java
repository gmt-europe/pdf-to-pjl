package nl.gmt.pdfToPjl;

public class ArgumentsException extends Exception {
    public ArgumentsException() {
    }

    public ArgumentsException(String s) {
        super(s);
    }

    public ArgumentsException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ArgumentsException(Throwable throwable) {
        super(throwable);
    }
}
