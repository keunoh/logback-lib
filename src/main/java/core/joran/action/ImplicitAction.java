package core.joran.action;

import core.joran.spi.ElementPath;
import core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public abstract class ImplicitAction extends Action {

    public abstract boolean isApplicable(ElementPath currentElementPath, Attributes attributes, InterpretationContext ec);
}
