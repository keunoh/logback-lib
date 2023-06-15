package core.joran.event;

import org.xml.sax.Locator;

public class BodyEvent extends SaxEvent {

    private String text;

    BodyEvent(String text, Locator locator) {
        super(null, null, null, locator);
        this.text = text;
    }

    public String getText() {
        if (text != null) {
            return text.trim();
        }
        return text;
    }

    @Override
    public String toString() {
        return "BodyEvent(" + getText() + ")" + locator.getLineNumber() + "," + locator.getColumnNumber();
    }

    public void append(String str) {
        text += str;
    }
}
