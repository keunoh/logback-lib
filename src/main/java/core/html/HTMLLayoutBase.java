package core.html;

import core.Context;
import core.CoreConstants;
import core.LayoutBase;
import core.pattern.Converter;
import core.pattern.ConverterUtil;
import core.pattern.parser.Node;
import core.pattern.parser.Parser;
import core.spi.ScanException;

import java.util.HashMap;
import java.util.Map;

import static core.CoreConstants.LINE_SEPARATOR;

public abstract class HTMLLayoutBase<E> extends LayoutBase<E> {

    protected String pattern;
    protected Converter<E> head;
    protected String title = "Logback Log Messages";
    protected CssBuilder cssBuilder;
    protected long counter = 0;

    public void setPattern(String conversionPattern) {
        pattern = conversionPattern;
    }

    /**
     * Returns the value of the <b>ConversionPattern </b> option.
     */
    public String getPattern() {
        return pattern;
    }

    public CssBuilder getCssBuilder() {
        return cssBuilder;
    }

    public void setCssBuilder(CssBuilder cssBuilder) {
        this.cssBuilder = cssBuilder;
    }

    @Override
    public void start() {
        int errorCount = 0;

        try {
            Parser<E> p = new Parser<E>(pattern);
            p.setContext(getContext());
            Node t = p.parse();
            this.head = p.compile(t, getEffectiveConverterMap());
            ConverterUtil.startConverters(this.head);
        } catch (ScanException ex) {
            addError("Incorrect pattern found", ex);
            errorCount++;
        }

        if (errorCount == 0) {
            super.started = true;
        }
    }

    protected abstract Map<String, String> getDefaultConverterMap();

    public Map<String, String> getEffectiveConverterMap() {
        Map<String, String> effectiveMap = new HashMap<>();

        Map<String, String> defaultMap = getDefaultConverterMap();
        if (defaultMap != null) {
            effectiveMap.putAll(defaultMap);
        }

        Context context = getContext();
        if (context != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> contextMap = (Map<String, String>) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
            if (contextMap != null) {
                effectiveMap.putAll(contextMap);
            }
        }
        return effectiveMap;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the current value of the <b>Title </b> option.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the content type output by this layout, i.e "text/html".
     */
    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public String getFileHeader() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
        sbuf.append(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sbuf.append(LINE_SEPARATOR);
        sbuf.append("<html>");
        sbuf.append(LINE_SEPARATOR);
        sbuf.append("  <head>");
        sbuf.append(LINE_SEPARATOR);
        sbuf.append("    <title>");
        sbuf.append(title);
        sbuf.append("</title>");
        sbuf.append(LINE_SEPARATOR);

        cssBuilder.addCss(sbuf);

        sbuf.append(LINE_SEPARATOR);
        sbuf.append("  </head>");
        sbuf.append(LINE_SEPARATOR);
        sbuf.append("<body>");
        sbuf.append(LINE_SEPARATOR);

        return sbuf.toString();
    }

    public String getPresentationHeader() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("<hr/>");
        sbuf.append(LINE_SEPARATOR);
        sbuf.append("<p>Log session start time ");
        sbuf.append(new java.util.Date());
        sbuf.append("</p><p></p>");
        sbuf.append(LINE_SEPARATOR);
        sbuf.append(LINE_SEPARATOR);
        sbuf.append("<table cellspacing=\"0\">");
        sbuf.append(LINE_SEPARATOR);

        buildHeaderRowForTable(sbuf);

        return sbuf.toString();
    }

    private void buildHeaderRowForTable(StringBuilder sbuf) {
        Converter c = head;
        String name;
        sbuf.append("<tr class=\"header\">");
        sbuf.append(LINE_SEPARATOR);
        while (c != null) {
            name = computeConverterName(c);
            if (name == null) {
                c = c.getNext();
                continue;
            }
            sbuf.append("<td class=\"");
            sbuf.append(computeConverterName(c));
            sbuf.append("\">");
            sbuf.append(computeConverterName(c));
            sbuf.append("</td>");
            sbuf.append(LINE_SEPARATOR);
            c = c.getNext();
        }
        sbuf.append("</tr>");
        sbuf.append(LINE_SEPARATOR);
    }

    public String getPresentationFooter() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("</table>");
        return sbuf.toString();
    }

    /**
     * Returns the appropriate HTML footers.
     */
    @Override
    public String getFileFooter() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(LINE_SEPARATOR);
        sbuf.append("</body></html>");
        return sbuf.toString();
    }

    protected void startNewTableIfLimitReached(StringBuilder sbuf) {
        if (this.counter >= CoreConstants.TABLE_ROW_LIMIT) {
            counter = 0;
            sbuf.append("</table>");
            sbuf.append(LINE_SEPARATOR);
            sbuf.append("<p></p>");
            sbuf.append("<table cellspacing=\"0\">");
            sbuf.append(LINE_SEPARATOR);
            buildHeaderRowForTable(sbuf);
        }
    }

    protected String computeConverterName(Converter c) {
        String className = c.getClass().getSimpleName();
        int index = className.indexOf("Converter");
        if (index == -1) {
            return className;
        } else {
            return className.substring(0, index);
        }
    }
}































