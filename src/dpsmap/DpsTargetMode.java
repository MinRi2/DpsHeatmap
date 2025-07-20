package dpsmap;

import arc.*;

public enum DpsTargetMode{
    ground, fly, both;

    public static final DpsTargetMode[] all = values();

    public boolean ground(){
        return this == ground || this == both;
    }

    public boolean fly(){
        return this == fly || this == both;
    }

    public String localized(){
        return Core.bundle.get("dpsHeatmap.targetMode." + name() + ".name", name());
    }
}
