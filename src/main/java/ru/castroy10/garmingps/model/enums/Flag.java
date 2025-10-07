package ru.castroy10.garmingps.model.enums;

import lombok.Getter;

@Getter
public enum Flag {
    BLUE("Flag, Blue"),
    RED("Flag, Red"),
    YELLOW("Flag, Yellow"),
    GREEN("Flag, Green");;

    private final String name;

    Flag(final String name) {
        this.name = name;
    }
}
