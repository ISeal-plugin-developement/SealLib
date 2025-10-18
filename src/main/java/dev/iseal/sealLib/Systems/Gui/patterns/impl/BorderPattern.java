package dev.iseal.sealLib.Systems.Gui.patterns.impl;

import dev.iseal.sealLib.Systems.Gui.patterns.GuiPatternUtils;
import dev.iseal.sealLib.Systems.Gui.patterns.Pattern;

public class BorderPattern extends Pattern {

    public BorderPattern(int width, int height) {
        super(GuiPatternUtils.border(width, height));
    }

}