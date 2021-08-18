package me.nort3x.atomic.logger;

public enum Color {
    ANSI_RESET("\u001B[0m"),
    ANSI_BLACK("\u001B[30m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_WHITE("\u001B[37m"),
    ANSI_BRIGHT_WHITE("\u001B[97m"),
    ANSI_BRIGHT_BLACK("\u001B[90m");

    final String colorValue;

    Color(String s) {
        colorValue = s;
    }

    public static String color(String s, Color c) {
        if (c.equals(Color.ANSI_RESET))
            return s;
        return c.colorValue + s + Color.ANSI_RESET.colorValue;
    }

    static class Rainbow {
        StringBuilder sb = new StringBuilder();

        Rainbow of(String s, Color c) {
            sb.append(Color.color(s, c));
            return this;
        }

        Rainbow append(String s) {
            sb.append(s);
            return this;
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

}
