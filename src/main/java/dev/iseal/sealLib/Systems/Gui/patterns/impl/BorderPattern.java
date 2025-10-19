package dev.iseal.sealLib.Systems.Gui.patterns.impl;

import dev.iseal.sealLib.Systems.Gui.patterns.GuiPatternUtils;
import dev.iseal.sealLib.Systems.Gui.patterns.Pattern;

public class BorderPattern extends Pattern {

    public BorderPattern(int width, int height) {
        this(width, height, 0);
    }

    /**
     * Creates a border pattern with a specified offset from the edge.
     * @param width Width of the GUI
     * @param height Height of the GUI
     * @param offset Number of slots to offset from the edge (0 = outermost border)
     */
    public BorderPattern(int width, int height, int offset) {
        super(GuiPatternUtils.getBorderSlots(width, height, offset), offset);
    }
}