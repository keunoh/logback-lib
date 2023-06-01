package core.subst;

import core.CoreConstants;
import core.spi.ScanException;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    enum TokenizerState {
        LITERAL_STATE, START_STATE, DEFAULT_VAL_STATE
    }

    final String pattern;
    final int patternLength;

    public Tokenizer(String pattern) {
        this.pattern = pattern;
        patternLength = pattern.length();
    }

    TokenizerState state = TokenizerState.LITERAL_STATE;
    int pointer = 0;

    List<Token> tokenize() throws ScanException {
        List<Token> tokenList = new ArrayList<>();
        StringBuilder buf = new StringBuilder();

        while (pointer < patternLength) {
            char c = pattern.charAt(pointer);
            pointer++;

            switch (state) {
                case LITERAL_STATE:
                    handleLiteralState(c, tokenList, buf);
                    break;
                case START_STATE:
                    handleStartState(c, tokenList, buf);
                    break;
                case DEFAULT_VAL_STATE:
                    handleDefaultValueState(c, tokenList, buf);
                default:
            }
        }

        // EOS
        switch (state) {
            case LITERAL_STATE:
                addLiteralToken(tokenList, buf);
                break;
            case DEFAULT_VAL_STATE:
                // trailing colon. see also LOGBACK-1140
                buf.append(CoreConstants.COLON_CHAR);
                addLiteralToken(tokenList, buf);
                break;
            case START_STATE:
                buf.append(CoreConstants.DOLLAR);
                addLiteralToken(tokenList, buf);
                break;
        }
        return tokenList;
    }

    public void handleDefaultValueState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        switch (c) {
            case CoreConstants.DASH_CHAR:
                tokenList.add(Token.DEFAULT_SEP_TOKEN);
                state = TokenizerState.LITERAL_STATE;
                break;
            case CoreConstants.DOLLAR:
                stringBuilder.append(CoreConstants.COLON_CHAR);
                addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                state = TokenizerState.START_STATE;
                break;
            default:
                stringBuilder.append(CoreConstants.COLON_CHAR).append(c);
                state = TokenizerState.LITERAL_STATE;
                break;
        }
    }

    private void handleStartState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        if (c == CoreConstants.CURLY_LEFT) {
            tokenList.add(Token.START_TOKEN);
        } else {
            stringBuilder.append(CoreConstants.DOLLAR).append(c);
        }
        state = TokenizerState.LITERAL_STATE;
    }

    private void handleLiteralState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        if (c == CoreConstants.DOLLAR) {
            addLiteralToken(tokenList, stringBuilder);
            stringBuilder.setLength(0);
            state = TokenizerState.START_STATE;
        } else if (c == CoreConstants.COLON_CHAR) {
            addLiteralToken(tokenList, stringBuilder);
            stringBuilder.setLength(0);
            state = TokenizerState.DEFAULT_VAL_STATE;
        } else if (c == CoreConstants.CURLY_RIGHT) {
            addLiteralToken(tokenList, stringBuilder);
            tokenList.add(Token.CURLY_RIGHT_TOKEN);
            stringBuilder.setLength(0);
        } else {
            stringBuilder.append(c);
        }
    }

    private void addLiteralToken(List<Token> tokenList, StringBuilder stringBuilder) {
        if (stringBuilder.length() == 0)
            return;
        tokenList.add(new Token(Token.Type.LITERAL, stringBuilder.toString()));
    }
}