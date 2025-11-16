package com.awesoft.ccx.lib;

import dan200.computercraft.core.computer.ComputerSide;
import net.minecraft.core.Direction;

public class DirectionLib {
    public static ComputerSide toComputerSide(Direction direction, Direction blockFacing) {
        if (direction == Direction.UP) return ComputerSide.TOP;
        if (direction == Direction.DOWN) return ComputerSide.BOTTOM;

        return switch (blockFacing) {
            case NORTH -> switch (direction) {
                case NORTH -> ComputerSide.FRONT;
                case SOUTH -> ComputerSide.BACK;
                case WEST  -> ComputerSide.RIGHT;
                case EAST  -> ComputerSide.LEFT;
                default -> ComputerSide.BACK;
            };
            case SOUTH -> switch (direction) {
                case NORTH -> ComputerSide.BACK;
                case SOUTH -> ComputerSide.FRONT;
                case WEST  -> ComputerSide.LEFT;
                case EAST  -> ComputerSide.RIGHT;
                default -> ComputerSide.BACK;
            };
            case EAST -> switch (direction) {
                case NORTH -> ComputerSide.RIGHT;
                case SOUTH -> ComputerSide.LEFT;
                case WEST  -> ComputerSide.BACK;
                case EAST  -> ComputerSide.FRONT;
                default -> ComputerSide.BACK;
            };
            case WEST -> switch (direction) {
                case NORTH -> ComputerSide.LEFT;
                case SOUTH -> ComputerSide.RIGHT;
                case WEST  -> ComputerSide.FRONT;
                case EAST  -> ComputerSide.BACK;
                default -> ComputerSide.BACK;
            };
            default -> ComputerSide.BACK;
        };
    }
    public static ComputerSide fromString(String string) {
        if (string.equalsIgnoreCase("front")) return ComputerSide.FRONT;
        if (string.equalsIgnoreCase("back")) return ComputerSide.BACK;
        if (string.equalsIgnoreCase("top")) return ComputerSide.TOP;
        if (string.equalsIgnoreCase("bottom")) return ComputerSide.BOTTOM;
        if (string.equalsIgnoreCase("right")) return ComputerSide.RIGHT;
        if (string.equalsIgnoreCase("left")) return ComputerSide.LEFT;
        return null;
    }
}
