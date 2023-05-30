package core.encoder;

import core.CoreConstants;

public class EchoEncoder<E> extends EncoderBase<E> {

    String fileHeader;
    String fileFooter;

    public byte[] encode(E event) {
        String val = event + CoreConstants.LINE_SEPARATOR;
        return val.getBytes();
    }

    public byte[] footerBytes() {
        if (fileFooter == null)
            return null;
        return fileFooter.getBytes();
    }

    public byte[] headerBytes() {
        if (fileHeader == null)
            return null;
        return fileHeader.getBytes();
    }
}
