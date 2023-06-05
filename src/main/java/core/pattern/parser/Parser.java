package core.pattern.parser;

import core.CoreConstants;
import core.pattern.Converter;
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
    }
}

























