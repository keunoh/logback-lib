package core.joran.spi;

import java.util.ArrayList;
import java.util.List;

public class ElementPath {

    ArrayList<String> partList = new ArrayList<>();

    public ElementPath() {
    }

    public ElementPath(List<String> list) {
        partList.addAll(list);
    }

    public ElementPath(String pathStr) {
        if (pathStr == null)
            return;

        String[] partArray = pathStr.split("/");
        if (partArray == null)
            return;

        for (String part : partArray) {
            if (part.length() > 0) {
                partList.add(part);
            }
        }
    }

    public ElementPath duplicate() {
        ElementPath p = new ElementPath();
        p.partList.addAll(this.partList);
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof ElementPath)) {
            return false;
        }

        ElementPath r = (ElementPath) o;

        if (r.size() != size()) {
            return false;
        }

        int len = size();

        for (int i = 0; i < len; i++) {
            if (!equalityCheck(get(i), r.get(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean equalityCheck(String x, String y) {
        return x.equalsIgnoreCase(y);
    }

    public List<String> getCopyOfPartList() {
        return new ArrayList<>(partList);
    }

    public void push(String s) {
        partList.add(s);
    }

    public String get(int i) {
        return (String) partList.get(i);
    }

    public void pop() {
        if (!partList.isEmpty()) {
            partList.remove(partList.size() - 1);
        }
    }

    public String peekList() {
        if (!partList.isEmpty()) {
            int size = partList.size();
            return (String) partList.get(size - 1);
        } else {
            return null;
        }
    }

    public int size() {
        return partList.size();
    }

    protected String toStableString() {
        StringBuilder result = new StringBuilder();
        for (String current : partList) {
            result.append("[").append(current).append("]");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return toStableString();
    }
}