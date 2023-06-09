package core.joran.event;

import core.Context;
import core.joran.spi.ElementPath;
import core.joran.spi.JoranException;
import core.spi.ContextAware;
import core.spi.ContextAwareImpl;
import core.status.Status;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static core.CoreConstants.XML_PARSING;

public class SaxEventRecorder extends DefaultHandler implements ContextAware {

    final ContextAwareImpl cai;

    public SaxEventRecorder(Context context) {
        cai = new ContextAwareImpl(context, this);
    }

    public List<SaxEvent> saxEventList = new ArrayList<SaxEvent>();
    Locator locator;
    ElementPath globalElementPath = new ElementPath();

    final public void recordEvents(InputStream inputStream) throws JoranException {
        recordEvents(new InputSource(inputStream));
    }

    public List<SaxEvent> recordEvents(InputSource inputSource) throws JoranException {
        SAXParser saxParser = buildSaxParser();
        try {
            saxParser.parse(inputSource, this);
            return saxEventList;
        } catch (IOException ie) {
            handleError("I/O error occurred while parsing xml file", ie);
        } catch (SAXException se) {
            // Exception added into StatusManager via Sax error handling. No need to add it again
            throw new JoranException("Problem parsing XML document. See previously reported errors.", se);
        } catch (Exception ex) {
            handleError("Unexpected exception while parsing XML document.", ex);
        }
        throw new IllegalStateException("This point can never be reached");
    }
    private void handleError(String errMsg, Throwable t) throws JoranException {
        addError(errMsg, t);
        throw new JoranException(errMsg, t);
    }

    private SAXParser buildSaxParser() throws JoranException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            //spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setNamespaceAware(true);
            return spf.newSAXParser();
        } catch (Exception pce) {
            String errMsg = "Parser configuration error occurred";
            addError(errMsg, pce);
            throw new JoranException(errMsg, pce);
        }
    }

    public void startDocument() {
    }

    public Locator getLocator() {
        return locator;
    }

    public void setDocumentLocator(Locator l) {
        locator = l;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {

        String tagName = getTagName(localName, qName);
        globalElementPath.push(tagName);
        ElementPath current = globalElementPath.duplicate();
        saxEventList.add(new StartEvent(current, namespaceURI, localName, qName, atts, getLocator()));
    }

    public void characters(char[] ch, int start, int length) {
        String bodyStr = new String(ch, start, length);
        SaxEvent lastEvent = getLastEvent();
        if (lastEvent instanceof BodyEvent) {
            BodyEvent be = (BodyEvent) lastEvent;
            be.append(bodyStr);
        } else {
            // ignore space only text if the previous event is not a BodyEvent
            if (!isSpaceOnly(bodyStr)) {
                saxEventList.add(new BodyEvent(bodyStr, getLocator()));
            }
        }
    }

    boolean isSpaceOnly(String bodyStr) {
        String bodyTrimmed = bodyStr.trim();
        return (bodyTrimmed.length() == 0);
    }

    SaxEvent getLastEvent() {
        if (saxEventList.isEmpty()) {
            return null;
        }
        int size = saxEventList.size();
        return saxEventList.get(size - 1);
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        saxEventList.add(new EndEvent(namespaceURI, localName, qName, getLocator()));
        globalElementPath.pop();
    }

    String getTagName(String localName, String qName) {
        String tagName = localName;
        if ((tagName == null) || (tagName.length() < 1)) {
            tagName = qName;
        }
        return tagName;
    }

    public void error(SAXParseException spe) throws SAXException {
        addError(XML_PARSING + " - Parsing error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
        addError(spe.toString());

    }

    public void fatalError(SAXParseException spe) throws SAXException {
        addError(XML_PARSING + " - Parsing fatal error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
        addError(spe.toString());
    }

    public void warning(SAXParseException spe) throws SAXException {
        addWarn(XML_PARSING + " - Parsing warning on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber(), spe);
    }

    public void addError(String msg) {
        cai.addError(msg);
    }

    public void addError(String msg, Throwable ex) {
        cai.addError(msg, ex);
    }

    public void addInfo(String msg) {
        cai.addInfo(msg);
    }

    public void addInfo(String msg, Throwable ex) {
        cai.addInfo(msg, ex);
    }

    public void addStatus(Status status) {
        cai.addStatus(status);
    }

    public void addWarn(String msg) {
        cai.addWarn(msg);
    }

    public void addWarn(String msg, Throwable ex) {
        cai.addWarn(msg, ex);
    }

    public Context getContext() {
        return cai.getContext();
    }

    public void setContext(Context context) {
        cai.setContext(context);
    }

    public List<SaxEvent> getSaxEventList() {
        return saxEventList;
    }
}

















