package org.ch.qos.spi;

import lib.slf4j.Marker;
import org.ch.qos.Level;
import org.ch.qos.Logger;
import org.ch.qos.turbo.TurboFilter;

import java.util.concurrent.CopyOnWriteArrayList;

final public class TurboFilterList extends CopyOnWriteArrayList<TurboFilter> {

    private static final long serialVersionUID = 1L;

    public FilterReply getTurboFilterChainDecision(final Marker marker, final Logger logger, final Level level, final String format, final Object[] params,
                                                   final Throwable t) {

        final int size = size();
        if (size == 1) {
            try {
                TurboFilter tf = get(0);
                return tf.decide(marker, logger, level, format, params, t);
            } catch (IndexOutOfBoundsException iobe) {
                return FilterReply.NEUTRAL;
            }
        }

        Object[] tfa = toArray();
        final int len = tfa.length;
        for (int i = 0; i < len; i++) {
            final TurboFilter tf = (TurboFilter) tfa[i];
            final FilterReply r = tf.decide(marker, logger, level, format, params, t);
            if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
                return r;
            }
        }
        return FilterReply.NEUTRAL;
    }
}
