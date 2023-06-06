package core.pattern.parser;

import core.CoreConstants;
import core.spi.ScanException;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {

    enum TokenizerState {
        LITERAL_STATE, FORMAT_MODIFIER_STATE, KEYWORD_STATE, OPTION_STATE, RIGHT_PARENTHESIS_STATE
    }

    final String pattern;
    final int patternLength;
    final IEsacpeUtil escapeUtil;
    final IEsacpeUtil optionEscapeUtil = new RestrictedEscapeUtil();
    TokenizerState state = TokenizerState.LITERAL_STATE;
    int pointer = 0;

    TokenStream(String pattern) {
        this(pattern, new RegularEscapeUtil());
    }

    TokenStream(String pattern, IEscapeUtil escapeUtil) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("null or empty pattern string not allowed");
        }
        this.pattern = pattern;
        patternLength = pattern.length();
        this.escapeUtil = escapeUtil;
    }

    List tokenize() throws ScanException {
        List<Token> tokenList = new ArrayList<>();
        StringBuffer buf = new StringBuffer();

        while (pointer < patternLength) {
            char c = pattern.charAt(pointer);
            pointer++;

            switch (state) {
                case LITERAL_STATE:
                    handleLiteralState(c, tokenList, buf);
                    break;
                case FORMAT_MODIFIER_STATE:
                    handleFormatModifierState(c, tokenList, buf);
                    break;
                case OPTION_STATE:
                    processOption(c, tokenList, buf);
                    break;
                case KEYWORD_STATE:
                    handleKeywordState(c, tokenList, buf);
                    break;
                case RIGHT_PARENTHESIS_STATE:
                    handleRightParenthesisState(c, tokenList, buf);
                    break;

                default:
            }
        }

        // EOS
        switch (state) {
            case LITERAL_STATE:
                addValuedToken(Token.LITERAL, buf, tokenList);
                break;
            case KEYWORD_STATE:
                tokenList.add(new Token(Token.SIMPLE_KEYWORD, buf.toString()));
                break;
            case RIGHT_PARENTHESIS_STATE:
                tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
                break;

            case FORMAT_MODIFIER_STATE:
            case OPTION_STATE:
                throw new ScanException("Unexpected end of pattern string");
        }

        return tokenList;
    }

    private void handleRightParenthesisState(char c, List<Token> tokenList, StringBuffer buf) {
        tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
        switch (c) {
            case CoreConstants.RIGHT_PARENTHESIS_CHAR:
                break;
            case CoreConstants.CURLY_LEFT:
                state = TokenizerState.OPTION_STATE;
                break;
            case CoreConstants.ESCAPE_CHAR:
                escape("%{}", buf);
                state = TokenizerState.LITERAL_STATE;
                break;
            default:
                buf.append(c);
                state = TokenizerState.LITERAL_STATE;
        }
    }

    private void processOption(char c, List<Token> tokenList, StringBuffer buf) throws ScanException {
        OptionTokenizer ot = new OptionTokenizer(this);
        ot.tokenize(c, tokenList);
    }

    private void handleFormatModifierState(char c, List<Token> tokenList, StringBuffer buf) {
        if (c == CoreConstants.LEFT_PARENTHESIS_CHAR) {
            addValuedToken(Token.FORMAT_MODIFIER, buf, tokenList);
            tokenList.add(Token.BARE_COMPOSITE_KEYWORD_TOKEN);
            state = TokenizerState.LITERAL_STATE;
        } else if (Character.isJavaIdentifierStart(c)) {
            addValuedToken(Token.FORMAT_MODIFIER, buf, tokenList);
            state = TokenizerState.KEYWORD_STATE;
            buf.append(c);
        } else {
            buf.append(c);
        }
    }

    private void handleLiteralState(char c, List<Token> tokenList, StringBuffer buf) {
        switch (c) {
            case CoreConstants.ESCAPE_CHAR:
                escape("%()", buf);
                break;

            case CoreConstants.PERCENT_CHAR:
                addValuedToken(Token.LITERAL, buf, tokenList);
                tokenList.add(Token.PERCENT_TOKEN);
                state = TokenizerState.FORMAT_MODIFIER_STATE;
                break;

            case CoreConstants.RIGHT_PARENTHESIS_CHAR:
                addValuedToken(Token.LITERAL, buf, tokenList);
                state = TokenizerState.RIGHT_PARENTHESIS_STATE;
                break;

            default:
                buf.append(c);
        }
    }

    private void handleKeywordState(char c, List<Token> tokenList, StringBuffer buf) {

        if (Character.isJavaIdentifierPart(c)) {
            buf.append(c);
        } else if (c == CoreConstants.CURLY_LEFT) {
            addValuedToken(Token.SIMPLE_KEYWORD, buf, tokenList);
            state = TokenizerState.OPTION_STATE;
        } else if (c == CoreConstants.LEFT_PARENTHESIS_CHAR) {
            addValuedToken(Token.COMPOSITE_KEYWORD, buf, tokenList);
            state = TokenizerState.LITERAL_STATE;
        } else if (c == CoreConstants.PERCENT_CHAR) {
            addValuedToken(Token.SIMPLE_KEYWORD, buf, tokenList);
            tokenList.add(Token.PERCENT_TOKEN);
            state = TokenizerState.FORMAT_MODIFIER_STATE;
        } else if (c == CoreConstants.RIGHT_PARENTHESIS_CHAR) {
            addValuedToken(Token.SIMPLE_KEYWORD, buf, tokenList);
            state = TokenizerState.RIGHT_PARENTHESIS_STATE;
        } else {
            addValuedToken(Token.SIMPLE_KEYWORD, buf, tokenList);
            if (c == CoreConstants.ESCAPE_CHAR) {
                if ((pointer < patternLength)) {
                    char next = pattern.charAt(pointer++);
                    escapeUtil.escape("%()", buf, next, pointer);
                }
            } else {
                buf.append(c);
            }
            state = TokenizerState.LITERAL_STATE;
        }
    }

    void escape(String escapeChars, StringBuffer buf) {
        if ((pointer < patternLength)) {
            char next = pattern.charAt(pointer++);
            escapeUtil.escape(escapeChars, buf, next, pointer);
        }
    }

    void optionEscape(String escapeChars, StringBuffer buf) {
        if ((pointer < patternLength)) {
            char next = pattern.charAt(pointer++);
            optionEscapeUtil.escape(escapeChars, buf, next, pointer);
        }
    }

    private void addValuedToken(int type, StringBuffer buf, List<Token> tokenList) {
        if (buf.length() > 0) {
            tokenList.add(new Token(type, buf.toString()));
            buf.setLength(0);
        }
    }
}
