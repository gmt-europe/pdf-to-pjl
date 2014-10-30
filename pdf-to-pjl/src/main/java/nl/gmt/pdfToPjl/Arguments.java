package nl.gmt.pdfToPjl;

import java.util.Map;
import java.util.TreeMap;

public class Arguments {
    private String name;
    private String display;
    private String input;
    private String output;
    private Map<String, String> defines = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    public Arguments(String[] args) throws ArgumentsException {
        if (args.length == 0) {
            printHelp();
        }

        boolean hadName = false;
        boolean hadDisplay = false;
        boolean hadInput = false;
        boolean hadOutput = false;
        boolean hadDefine = false;

        for (String arg : args) {
            if (hadName) {
                name = arg;
                hadName = false;
            } else if (hadDisplay) {
                display = arg;
                hadDefine = false;
            } else if (hadInput) {
                input = arg;
                hadInput = false;
            } else if (hadOutput) {
                output = arg;
                hadOutput = false;
            } else if (hadDefine) {
                int pos = arg.indexOf('=');
                if (pos == -1) {
                    throw new ArgumentsException("Format of -d must be <key>=<value>");
                }
                String key = arg.substring(0, pos).trim();
                String value = arg.substring(pos + 1).trim();
                if (key.length() == 0) {
                    throw new ArgumentsException("Missing key for -d");
                }
                if (value.length() == 0) {
                    throw new ArgumentsException("Missing value for -d");
                }
                if (defines.get(key) != null) {
                    throw new ArgumentsException(String.format("Duplicate definition of '%s'", key));
                }
                defines.put(key, value);
                hadDefine = false;
            } else if ("-n".equals(arg)) {
                hadName = true;
            } else if ("-D".equals(arg)) {
                hadDisplay = true;
            } else if ("-i".equals(arg)) {
                hadInput = true;
            } else if ("-o".equals(arg)) {
                hadOutput = true;
            } else if ("-d".equals(arg)) {
                hadDefine = true;
            } else {
                throw new ArgumentsException(String.format("Invalid argument '%s'", arg));
            }
        }

        if (hadName) {
            throw new ArgumentsException("Missing value for -n");
        }
        if (hadDisplay) {
            throw new ArgumentsException("Missing value for -D");
        }
        if (hadInput) {
            throw new ArgumentsException("Missing value for -i");
        }
        if (hadOutput) {
            throw new ArgumentsException("Missing value for -o");
        }
        if (hadDefine) {
            throw new ArgumentsException("Missing value for -d");
        }

        if (name == null) {
            throw new ArgumentsException("Name (-n) is mandatory");
        }
    }

    private void printHelp() {
        System.err.println("Convert PDF file with PJL instructions into a PJL job");
        System.err.println("GMT (c) 2014");
        System.err.println();
        System.err.println("pdf-to-pjl.jar -n <job name> [-D <display name>] [-i <input file name>]");
        System.err.println("               [-o <output file name>] [-d <key>=<value>]...");
        System.err.println();
        System.err.println("  -n <job name>: Name of the printer job");
        System.err.println("  -D <display name>: Display name of the printer job");
        System.err.println("  -i <input file name>: Name of the file to use as input; stdin is used");
        System.err.println("                        when omitted");
        System.err.println("  -o <output file name>: Name of the file to output the job to; stdout");
        System.err.println("                         is used when omitted");
        System.err.println("  -d <key>=<value>: Register a replacement value");
        System.err.println();
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public Map<String, String> getDefines() {
        return defines;
    }
}
