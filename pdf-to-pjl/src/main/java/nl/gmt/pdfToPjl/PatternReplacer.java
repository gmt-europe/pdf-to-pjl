package nl.gmt.pdfToPjl;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternReplacer {
    public static interface Callback {
        public String foundMatch(MatchResult matchResult) throws ParseException;
    }

    private final Pattern pattern;

    public PatternReplacer(Pattern pattern) {
        this.pattern = pattern;
    }

    public String replaceMatches(String string, Callback callback) throws ParseException {
        StringBuilder sb = new StringBuilder();
        int end = 0;

        Matcher matcher = this.pattern.matcher(string);
        while (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            String replacement = callback.foundMatch(matchResult);

            sb.append(string.substring(end, matchResult.start())).append(replacement);
            end = matchResult.end();
        }

        sb.append(string.substring(end));

        return sb.toString();
    }
}
