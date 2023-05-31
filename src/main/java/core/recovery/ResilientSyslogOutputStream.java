package core.recovery;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ResilientSyslogOutputStream extends ResilientOutputStreamBase {

    String syslogHost;
    int port;

    public ResilientSyslogOutputStream(String syslogHost, int port) throws UnknownHostException, SocketException {
        this.syslogHost = syslogHost;
        this.port = port;
        super.os = new SyslogOutputStream(syslogHost, port);
        this.presumedClean = true;
    }

    @Override
    String getDescription() {
        return "syslog [" + syslogHost + ":" + port + "]";
    }

    @Override
    OutputStream openNewOutputStream() throws IOException {
        return new SyslogOutputStream(syslogHost, port);
    }

    @Override
    public String toString() {
        return "c.q.l.c.recovery.ResilientSyslogOutputStream@" + System.identityHashCode(this);
    }
}
