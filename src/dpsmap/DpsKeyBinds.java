package dpsmap;

import arc.input.*;

public class DpsKeyBinds{
    public static KeyBind enable, showPlayerTeam, nextTargetMode;

    public static void init(){
        String category = "Dps heat map";
        enable = KeyBind.add(DpsSettings.enable.name, KeyCode.f1, category);
        showPlayerTeam = KeyBind.add(DpsSettings.showPlayerTeam.name, KeyCode.f2, category);
        nextTargetMode = KeyBind.add(DpsSettings.targetMode.name, KeyCode.f3, category);
    }
}
