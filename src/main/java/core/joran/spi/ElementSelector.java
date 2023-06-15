package core.joran.spi;

import java.util.List;

public class ElementSelector extends ElementPath {

    public ElementSelector() {
        super();
    }

    public ElementSelector(List<String> list) {
        super(list);
    }

    public ElementSelector(String p) {
        super(p);
    }

    public boolean fullPathMatch(ElementPath path) {
        if (path.size() != size()) {
            return false;
        }

        int len = size();
        for (int i = 0; i < len; i++) {
            if (!equalityCheck(get(i), path.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int getTailMatchLength(ElementPath p) {
        if (p == null)
            return 0;

        int lSize = this.partList.size();
        int rSize = p.partList.size();

        if ((lSize == 0) || (rSize == 0))
            return 0;

        int minLen = (lSize <= rSize) ? lSize : rSize;
        int match = 0;
    }
}





















