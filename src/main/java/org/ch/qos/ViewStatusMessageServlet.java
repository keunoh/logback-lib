package org.ch.qos;

import lib.slf4j.LoggerFactory;

public class ViewStatusMessageServlet extends ViewStatusMessagesServletBase {

    private static final long serialVersionUID = 443878494348593337L;

    @Override
    protected StatusManager getStatusManager(HttpServletRequest req, HttpServletResponse resp) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        return lc.getStatusManager();
    }

    @Override
    protected String getPageTitle(HttpServletRequest req, HttpServletResponse resp) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        return "<h2>Status messages for LoggerContext named [" + lc.getName() + "]</h2>\r\n";
    }
}
