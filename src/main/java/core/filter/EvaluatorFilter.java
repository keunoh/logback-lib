package core.filter;

import core.boolex.EvaluationException;
import core.boolex.EventEvaluator;
import core.spi.FilterReply;

public class EvaluatorFilter<E> extends AbstractMatcherFilter<E> {

    EventEvaluator<E> evaluator;

    @Override
    public void start() {
        if (evaluator != null) {
            super.start();
        } else {
            addError("no evaluator set for filter " + this.getName());
        }
    }

    public EventEvaluator<E> getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(EventEvaluator<E> evaluator) {
        this.evaluator = evaluator;
    }

    public FilterReply decide(E event) {
        if (!isStarted() || !evaluator.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        try {
            if (evaluator.evaluate(event)) {
                return onMatch;
            } else {
                return onMismatch;
            }
        } catch (EvaluationException e) {
            addError("Evaluator " + evaluator.getName() + " threw an exception", e);
            return FilterReply.NEUTRAL;
        }
    }
}



















