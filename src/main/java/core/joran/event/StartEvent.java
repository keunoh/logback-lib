package core.joran.event;

import core.joran.spi.ElementPath;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

public class StartEvent extends SaxEvent {

    final public Attributes attributes;
    final public ElementPath elementPath;

    StartEvent(ElementPath elementPath, String namespaceURI, String localName, String qName, Attributes attributes, Locator locator) {
        super(namespaceURI, localName, qName, locator);
        this.attributes = attributes;
        this.elementPath = elementPath;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("StartEvent(");
        b.append(getQName());
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if (i > 0)
                    b.append(' ');
                b.append(attributes.getLocalName(i)).append("=\"").append(attributes.getValue(i)).append("\"");
            }
        }
        b.append(")  [");
        b.append(locator.getLineNumber());
        b.append(",");
        b.append(locator.getColumnNumber());
        b.append("]");
        return b.toString();
    }
}
