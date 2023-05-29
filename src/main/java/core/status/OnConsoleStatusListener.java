package core.status;

import java.io.PrintStream;

public class OnConsoleStatusListener extends OnPrintStreamStatusListenerBase {

    @Override
    protected PrintStream getPrintStream() {
        return System.out;
    }
}
