package dpsmap;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.BaseTurret.*;
import mindustry.world.blocks.defense.turrets.ReloadTurret.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.consumers.*;
import mindustry.world.modules.*;

public class DpsHeatmap{
    private static boolean enable;
    private static boolean showPlayerTeam;
    private static boolean checkGround, checkFly;

    public static void update(){
        enable = DpsSettings.enable.get();

        DpsTargetMode targetMode = DpsTargetMode.all[DpsSettings.targetMode.get()];
        checkGround = targetMode.ground();
        checkFly = targetMode.fly();
        showPlayerTeam = DpsSettings.showPlayerTeam.get();
    }

    public static void draw(){
        if(!enable) return;

        DpsShaders.heatmap.alpha = DpsSettings.alpha.get() / 100f;
        DpsShaders.heatmap.intensity = DpsSettings.intensify.get() / 100f;

        if(Mathf.zero(DpsShaders.heatmap.alpha) || Mathf.zero(DpsShaders.heatmap.intensity)) return;

        Rules rules = Vars.state.rules;
        FrameBuffer buffer = Vars.renderer.effectBuffer;
        Rect rect = Core.camera.bounds(Tmp.r1);
        rect.grow(Vars.tilesize * 5);

        Team team = Vars.player.team();
        for(TeamData data : Vars.state.teams.present){
            if(!showPlayerTeam && data.team == team) continue;

            DpsShaders.heatmap.outline.set(data.team.color);

            buffer.begin(Color.clear);
            if(data.unitTree != null){
                data.unitTree.intersect(rect, unit -> {
                    if(unit.inFogTo(team) || !unit.checkTarget(checkGround, checkFly)) return;
                    drawDpsRange(unit, unit.type.estimateDps() * rules.unitDamage(data.team), unit.range());
                });
            }

            if(data.turretTree != null){
                data.turretTree.intersect(rect, build -> {
                    if(build.inFogTo(team) || !(build instanceof BaseTurretBuild baseTurret)){
                        return;
                    }

                    // If not an instance of TurretBuild, needn't check.
                    if(baseTurret instanceof TurretBuild turret){
                        Turret turretBlock = (Turret)turret.block;
                        if((checkFly && !checkGround && !turretBlock.targetAir) || (checkGround && !checkFly && !turretBlock.targetGround)){
                            return;
                        }
                    }

                    float dps = baseTurret.estimateDps() * rules.blockDamage(data.team);

                    // anuke forgot it
                    BaseTurret baseBlock = (BaseTurret)baseTurret.block;
                    ConsumeLiquidBase coolant = baseBlock.coolant;
                    LiquidModule liquids = baseTurret.liquids;
                    if(liquids != null && coolant != null && liquids.current() != null && coolant.optional){
                        Liquid liquid = liquids.current();
                        if(coolant.consumes(liquid) && !Mathf.zero(liquids.currentAmount())){
                            float capacity = liquid.heatCapacity;

                            float lastEfficiency = baseTurret.efficiency;
                            baseTurret.efficiency = 1f; // assume turret is shooting.
                            dps *= baseBlock.coolantMultiplier * capacity * coolant.efficiency(baseTurret);
                            baseTurret.efficiency = lastEfficiency;
                        }
                    }

                    drawDpsRange(baseTurret, dps, baseTurret.range());
                });
            }

            buffer.end();

            Draw.z(DpsVars.dpsHeatmapLayer - data.team.id * 0.01f);
            buffer.blit(DpsShaders.heatmap);
            Draw.reset();
        }
    }

    private static void drawDpsRange(Position pos, float dps, float range){
        float mapped = Mathf.map(dps, DpsSettings.minDamage.get(), DpsSettings.maxDamage.get(), 0f, 1f);
        float alpha = Mathf.clamp(mapped, 0f, 1f);

        if(Mathf.equal(alpha, 0f)){
            return;
        }

        Draw.alpha(alpha);
        Fill.circle(pos.getX(), pos.getY(), range);
    }

}
