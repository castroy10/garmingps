package ru.castroy10.garmingps.model.enums;

import lombok.Getter;

@Getter
public enum Color {
    BLUE("FF0000FF"),
    RED("FFFF0000"),
    YELLOW("FFFFFF00"),
    GREEN("FF00FF00");

    private final String code;

    Color(final String code) {
        this.code = code;
    }

    public static String getRandom() {
        final Color[] colors = Color.values();
        return colors[(int) (Math.random() * colors.length)].getCode();
    }

}
