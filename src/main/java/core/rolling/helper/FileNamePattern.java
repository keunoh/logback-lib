package core.rolling.helper;

import core.spi.ContextAwareBase;

import java.util.HashMap;
import java.util.Map;

public class FileNamePattern extends ContextAwareBase {

    static final Map<String, String> CONVERTER_MAP = new HashMap<>();
    static {
        CONVERTER_MAP.put(IntegerTokenConverter.CONVERTER_KEY, IntegerTokenConverter.class.getName());
    }
}
