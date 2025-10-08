package ru.castroy10.garmingps.model.enums;

import lombok.Getter;

@Getter
public enum Flag {
    BLUE("Flag, Blue"),
    RED("Flag, Red"),
    YELLOW("Flag, Yellow"),
    GREEN("Flag, Green");;

    private final String code;

    Flag(final String code) {
        this.code = code;
    }

    public static String getRandom() {
        final Flag[] flags = Flag.values();
        return flags[(int) (Math.random() * flags.length)].getCode();
    }

}
