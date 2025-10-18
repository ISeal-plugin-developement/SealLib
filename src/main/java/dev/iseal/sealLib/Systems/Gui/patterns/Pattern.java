package dev.iseal.sealLib.Systems.Gui.patterns;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import dev.iseal.sealLib.Systems.Gui.inventory.AbstractGui;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class Pattern {

    protected final List<Integer> slots;

    public Pattern(List<Integer> slots) {
        this.slots = slots;
    }

    public void applyPattern(AbstractGui gui, Component component) {
        if (slots == null) return;
        gui.setComponent(slots, component);
    }

}