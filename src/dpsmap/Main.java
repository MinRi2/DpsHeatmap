package dpsmap;

import arc.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class Main extends Mod{

    public Main(){
        Events.on(ClientLoadEvent.class, e -> {
            DpsVars.init();
            DpsSettings.init();
            DpsKeyBinds.init();
            DpsShaders.init();

//            showDpsDialog();
        });

        Events.run(Trigger.update, () -> {
            if(Core.input.keyRelease(DpsKeyBinds.enable)){
                String key = DpsSettings.enable.toggle() ? "setting.enable" : "setting.disable";
                String info = Core.bundle.format(key, DpsSettings.enable.localized());
                Vars.ui.showInfoFade(info);
            }

            if(Core.input.keyRelease(DpsKeyBinds.showPlayerTeam)){
                String key = DpsSettings.showPlayerTeam.toggle() ? "setting.enable" : "setting.disable";
                String info = Core.bundle.format(key, DpsSettings.showPlayerTeam.localized());
                Vars.ui.showInfoFade(info);
            }

            if(Core.input.keyRelease(DpsKeyBinds.nextTargetMode)){
                int next = (DpsSettings.targetMode.get() + 1) % DpsTargetMode.all.length;
                DpsTargetMode nextMode = DpsTargetMode.all[next];
                DpsSettings.targetMode.set(next);
                Vars.ui.showInfoFade(Core.bundle.format("dpsHeatmap.nextTargetMode", nextMode.localized()));
            }

            DpsHeatmap.update();
        });

        Events.run(Trigger.postDraw, DpsHeatmap::draw);
    }

    private static void showDpsDialog(){
        BaseDialog dialog = new BaseDialog("dps");
        Table table = dialog.cont;
        table.pane(t -> {

            int i = 0;
            for(UnitType type : Vars.content.units().copy().sort(UnitType::estimateDps)){
                t.image(type.uiIcon).size(Vars.iconSmall).scaling(Scaling.fit);
                t.add(Strings.autoFixed(type.estimateDps(), 2)).style(Styles.outlineLabel).pad(8f);
                if(++i % 4 == 0){
                    t.row();
                }
            }
        });
        dialog.addCloseButton();
        dialog.show();
    }
}
