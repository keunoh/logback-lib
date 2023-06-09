package core.pattern;

public class FormatInfo {

    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;
    private boolean leftPad = true;
    private boolean leftTruncate = true;

    public FormatInfo() {
    }

    public FormatInfo(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public FormatInfo(int min, int max, boolean leftPad, boolean leftTruncate) {
        this.min = min;
        this.max = max;
        this.leftPad = leftPad;
        this.leftTruncate = leftTruncate;
    }

    public static FormatInfo valueOf(String str) throws IllegalArgumentException {
        if (str == null)
            throw new NullPointerException("Argument cannot be null");

        FormatInfo fi = new FormatInfo();

        int indexOfDot = str.indexOf('.');
        String minPart = null;
        String maxPart = null;
        if (indexOfDot != -1) {
            minPart = str.substring(0, indexOfDot);
            if (indexOfDot + 1 == str.length()) {
                throw new IllegalArgumentException("Formatting string [" + str + "] should not end with '.'");
            } else {
                maxPart = str.substring(indexOfDot + 1);
            }
        } else {
            minPart = str;
        }

        if (minPart != null && minPart.length() > 0) {
            int min = Integer.parseInt(minPart);
            if (min >= 0) {
                fi.min = min;
            } else {
                fi.min = -min;
                fi.leftPad = false;
            }
        }

        if (maxPart != null && maxPart.length() > 0) {
            int max = Integer.parseInt(maxPart);
            if (max >= 0) {
                fi.max = max;
            } else {
                fi.max = -max;
                fi.leftTruncate = false;
            }
        }
        return fi;
    }

    public boolean isLeftPad() {
        return leftPad;
    }

    public void setLeftPad(boolean leftAlign) {
        this.leftPad = leftAlign;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public boolean isLeftTruncate() {
        return leftTruncate;
    }

    public void setLeftTruncate(boolean leftTruncate) {
        this.leftTruncate = leftTruncate;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FormatInfo))
            return false;

        FormatInfo r = (FormatInfo) o;

        return (min == r.min) && (max == r.max) && (leftPad == r.leftPad) && (leftTruncate == r.leftTruncate);
    }

    @Override
    public int hashCode() {
        int result = min;
        result = 31 * result + max;
        result = 31 * result + (leftPad ? 1 : 0);
        result = 31 * result + (leftTruncate ? 1 : 0);
        return result;
    }

    public String toString() {
        return "FormatInfo(" + min + ", " + max + ", " + leftPad + ", " + leftTruncate + ")";
    }
}