package core.util;

public class CharSequenceState {
    final char c;
    int occurrences;

    public CharSequenceState(char c) {
        this.c = c;
        this.occurrences = 1;
    }

    void incrementOccurrences() {
        occurrences++;
    }
}
