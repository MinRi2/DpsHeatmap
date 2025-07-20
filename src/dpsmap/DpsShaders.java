package dpsmap;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;

public class DpsShaders{
    public static DpsHeatmapShader heatmap;

    public static void init(){
        heatmap = new DpsHeatmapShader();
    }

    public static class DpsHeatmapShader extends ModShader{
        public static final float baseAlpha = 0.2f;

        public float intensity = 2f;
        public float alpha = 1f;

        public final Color outline = Pal.accent.cpy();

        public DpsHeatmapShader(){
            super("screenspace", "heatmap");
        }

        @Override
        public void apply(){
            super.apply();

            setUniformf("u_intensity", intensity);
            setUniformf("u_alpha", baseAlpha * alpha);
            setUniformf("u_time", Time.time);
            setUniformf("u_inv", 1f / Core.graphics.getWidth(), 1f / Core.graphics.getHeight());
            setUniformf("u_outline", outline);
        }
    }

    public static class ModShader extends Shader{

        public ModShader(String vert, String frag){
            super(getShaderFi(vert + ".vert"), getShaderFi(frag + ".frag"));
        }
    }

    public static Fi getShaderFi(String name) {
        Fi modded = DpsVars.thisMod.root.child("shaders").child(name);
        if (modded.exists()) return modded;

        return Vars.tree.get("shaders/" + name);
    }
}
