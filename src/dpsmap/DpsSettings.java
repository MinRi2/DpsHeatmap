package dpsmap;

import arc.*;
import mindustry.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

public class DpsSettings{
    public static SettingValue<Boolean> enable, showPlayerTeam;
    public static SettingValue<Integer> minDamage, maxDamage;
    public static SettingValue<Integer> intensify, alpha;
    public static SettingValue<Integer> targetMode;

    public static void init(){
        enable = new SettingValue<>("dpsHeatmapEnable", true);
        showPlayerTeam = new SettingValue<>("dpsHeatmapShowPlayerTeam", true);
        minDamage = new SettingValue<>("dpsHeatmapMinDamage", 10);
        maxDamage = new SettingValue<>("dpsHeatmapMaxDamage", 3000);
        intensify = new SettingValue<>("dpsHeatmapIntensify", 100);
        alpha = new SettingValue<>("dpsHeatmapAlpha", 75);

        targetMode = new SettingValue<>("dpsHeatmapTargetMode", DpsTargetMode.both.ordinal());
        settings();

        targetMode.reset();
    }

    public static void settings(){
        SettingsTable graphics = Vars.ui.settings.graphics;
        graphics.checkPref(enable.name, enable.defaultValue);
        graphics.checkPref(showPlayerTeam.name, showPlayerTeam.defaultValue);
        graphics.sliderPref(minDamage.name, minDamage.defaultValue, 0, 200, 10, n -> "" + n);
        graphics.sliderPref(maxDamage.name, maxDamage.defaultValue, 200, 2_0000, 1000, n -> "" + n);
        graphics.sliderPref(intensify.name, intensify.defaultValue, 0, 200, 25, n -> n + "%");

        graphics.sliderPref(targetMode.name, targetMode.defaultValue, 0, DpsTargetMode.all.length - 1, 1, n -> DpsTargetMode.all[n].localized());
    }

    public static class SettingValue<T>{
        public final String name;
        private final T defaultValue;

        public SettingValue(String name, T defaultValue){
            this.name = name;
            this.defaultValue = defaultValue;
            Core.settings.defaults(name, defaultValue);
        }

        public T get(){
            return get(defaultValue);
        }

        public T get(T def){
            return (T)Core.settings.get(name, def);
        }

        public void set(T value){
            Core.settings.put(name, value);
        }

        public boolean toggle(){
            if(defaultValue instanceof Boolean){
                boolean newValue = !(boolean)get();
                Core.settings.put(name, newValue);
                return newValue;
            }
            return false;
        }

        public void reset(){
            Core.settings.put(name, defaultValue);
        }

        public String localized(){
            return Core.bundle.get("setting." + name + ".name");
        }
    }
}
