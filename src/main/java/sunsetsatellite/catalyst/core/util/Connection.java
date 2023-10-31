package sunsetsatellite.catalyst.core.util;

public enum Connection {
    INPUT("I"),
    OUTPUT("O"),
    BOTH("B"),
    NONE("X");

    final String c;

    Connection(String x) {
        this.c = x;
    }

    public String getLetter() {
        return c;
    }
}
