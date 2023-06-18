package core.net;

import java.io.IOException;

public interface ObjectWriter {
    void write(Object object) throws IOException;
}
