package core.pattern.parser;

public class Node {
    static final int LITERAL = 0;
    static final int SIMPLE_KEYWORD = 1;
    static final int COMPOSITE_KEYWORD = 2;
    final int type;
    final Object value;
    Node next;

    Node(int type) {
        this(type, null);
    }

    Node(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    /**
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Node))
            return false;
        Node r = (Node) o;

        return (type == r.type) && (value != null ? value.equals(r.value) : r.value == null) && (next != null ? next.equals(r.next) : r.next == null);
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    String printNext() {
        if (next != null)
            return " -> " + next;
        else
            return "";
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        switch (type) {
            case LITERAL:
                buf.append("LITERAL(" + value + ")");
                break;
            default:
                buf.append(super.toString());
        }
        buf.append(printNext());

        return buf.toString();
    }
}