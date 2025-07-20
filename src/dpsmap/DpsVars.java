package dpsmap;

import mindustry.*;
import mindustry.graphics.*;
import mindustry.mod.Mods.*;

public class DpsVars{
    public static float dpsHeatmapLayer = Layer.overlayUI - 1.01f;

    public static LoadedMod thisMod;

    public static void init(){
        thisMod = Vars.mods.getMod(Main.class);
    }
}
