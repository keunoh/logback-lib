package core.pattern.parser;

import core.CoreConstants;
import core.pattern.Converter;
import core.pattern.FormatInfo;
import core.pattern.IdentityCompositeConverter;
import core.pattern.ReplacingCompositeConverter;
import core.spi.ContextAwareBase;
import core.spi.ScanException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser<E> extends ContextAwareBase {

    public final static String MISSING_RIGHT_PARENTHESIS = CoreConstants.CODES_URL + "#missingRightParenthesis";
    public final static Map<String, String> DEFAULT_COMPOSITE_CONVERTER_MAP = new HashMap<String, String>();
    public final static String REPLACE_CONVERTER_WORD = "replace";

    static {
        DEFAULT_COMPOSITE_CONVERTER_MAP.put(Token.BARE_COMPOSITE_KEYWORD_TOKEN.getValue().toString(), IdentityCompositeConverter.class.getName());
        DEFAULT_COMPOSITE_CONVERTER_MAP.put(REPLACE_CONVERTER_WORD, ReplacingCompositeConverter.class.getName());
    }

    final List tokenList;
    int pointer = 0;

    Parser(TokenStream ts) throws ScanException {
        this.tokenList = ts.tokenize();
    }

    public Parser(String pattern) throws ScanException {
        this(pattern, new RegularEscapeUtil());
    }

    public Parser(String pattern, IEscapeUtil escapeUtil) throws ScanException {
        try {
            TokenStream ts = new TokenStream(pattern, escapeUtil);
            this.tokenList = ts.tokenize();
        } catch (IllegalArgumentException npe) {
            throw new ScanException("Failed to initialize Parser", npe);
        }
    }

    public Converter<E> compile(final Node top, Map converterMap) {
        Compiler<E> compiler = new Compiler<E>(top, converterMap);
        compiler.setContext(context);
        return compiler.compile();
    }

    public Node parse() throws ScanException {
        return E();
    }

    Node E() throws ScanException {
        Node t = T();
        if (t == null) {
            return null;
        }
        Node eOpt = Eopt();
        if (eOpt != null) {
            t.setNext(eOpt);
        }
        return t;
    }

    Node Eopt() throws ScanException {
        Token next = getCurrentToken();
        if (next == null) {
            return null;
        } else {
            return E();
        }
    }

    Node T() throws ScanException {
        Token t = getCurrentToken();
        expectNotNull(t, "a LITERAL or '%'");

        switch (t.getType()) {
            case Token.LITERAL:
                advanceTokenPointer();
                FormatInfo fi;
                Token u = getCurrentToken();
                FormattingNode c;
                expectNotNull(u, "a FORMAT_MODIFIER, SIMPLE_KEYWORD or COMPOUND_KEYWORD");
                if (u.getType() == Token.FORMAT_MODIFIER) {
                    fi = FormatInfo.valueOf((String) u.getValue());
                    advanceTokenPointer();
                    c = C();
                    c.setFormatInfo(fi);
                } else {
                    c = C();
                }
                return c;

            default:
                return null;
        }

    }

    FormattingNode C() throws ScanException {
        Token t = getCurrentToken();
        expectNotNull(t, "a LEFT_PARENTHESIS or KEYWORD");
        int type = t.getType();
        switch (type) {
            case Token.SIMPLE_KEYWORD:
                return SINGLE();
            case Token.COMPOSITE_KEYWORD:
                advanceTokenPointer();
                return COMPOSITE(t.getValue().toString());
            default:
                throw new IllegalStateException("Unexpected token " + t);
        }
    }

    FormattingNode SINGLE() throws ScanException {
        Token t = getNextToken();
        SimpleKeywordNode keywordNode = new SimpleKeywordNode(t.getValue());

        Token ot = getCurrentToken();
        if (ot != null && ot.getType() == Token.OPTION) {
            List<String> optionList = (List<String>) ot.getValue();
            keywordNode.setOptions(optionList);
            advanceTokenPointer();
        }
        return keywordNode;
    }

    FormattingNode COMPOSITE(String keyword) throws ScanException {
        CompositeNode compositeNode = new CompositeNode(keyword);

        Node childNode = E();
        compositeNode.setChildNode(childNode);

        Token t = getNextToken();

        if (t == null || t.getType() != Token.RIGHT_PARENTHESIS) {
            String msg = "Expecting RIGHT_PARENTHESIS token but got " + t;
            addError(msg);
            addError("See also " + MISSING_RIGHT_PARENTHESIS);
            throw new ScanException(msg);
        }
        Token ot = getCurrentToken();
        if (ot != null && ot.getType() == Token.OPTION) {
            List<String> optionList = (List<String>) ot.getValue();
            compositeNode.setOptions(optionList);
            advanceTokenPointer();
        }
        return compositeNode;
    }

    Token getNextToken() {
        if (pointer < tokenList.size()) {
            return (Token) tokenList.get(pointer++);
        }
        return null;
    }

    Token getCurrentToken() {
        if (pointer < tokenList.size()) {
            return (Token) tokenList.get(pointer);
        }
        return null;
    }

    void advanceTokenPointer() {
        pointer++;
    }

    void expectNotNull(Token t, String expected) {
        if (t == null)
            throw new IllegalStateException("All tokens consumed but was expecting " + expected);
    }
}