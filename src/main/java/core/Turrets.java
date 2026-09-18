package core;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;

public class Turrets {
    public final Seq<Building> activeTurrets = new Seq<>();

    public void onBlockBuildEndEvent(EventType.BlockBuildEndEvent event) {
        if (event.tile != null && event.tile.build != null) {
            Building b = event.tile.build;
            if (b instanceof ItemTurret.ItemTurretBuild || b instanceof LiquidTurret.LiquidTurretBuild) {
                if (!activeTurrets.contains(b)) {
                    activeTurrets.add(b);
                }
            }
        }
    }

    public void onBlockDestroyEvent(EventType.BlockDestroyEvent event) {
        if (event.tile != null && event.tile.build != null) {
            activeTurrets.remove(event.tile.build);
        }
    }

    public void onWorldLoadEvent() {
        activeTurrets.clear();
        Groups.build.each(b -> {
            if (b instanceof ItemTurret.ItemTurretBuild || b instanceof LiquidTurret.LiquidTurretBuild) {
                activeTurrets.add(b);
            }
        });
    }

    public void update() {
        if (activeTurrets.isEmpty() || Groups.player.isEmpty() || Vars.state == null || Vars.state.isPaused()) return;
        activeTurrets.removeAll(b -> b == null || !b.isValid());

        for (int i = 0; i < activeTurrets.size; i++) {
            Building build = activeTurrets.get(i);
            if (build instanceof ItemTurret.ItemTurretBuild turret) {
                ItemTurret block = (ItemTurret) turret.block;
                if (block.ammoTypes.isEmpty()) continue;
                int targetAmmo = block.maxAmmo / 2;
                Item ammo = block.ammoTypes.keys().next();
                if (turret.totalAmmo > targetAmmo) {
                    turret.items.remove(ammo, turret.totalAmmo - targetAmmo);
                } else {
                    while (turret.totalAmmo < targetAmmo) {
                        int before = turret.totalAmmo;
                        turret.handleItem(turret, ammo);
                        if (turret.totalAmmo <= before) break;
                    }
                }
            } else if (build instanceof LiquidTurret.LiquidTurretBuild liquidTurret) {
                LiquidTurret liquidBlock = (LiquidTurret) liquidTurret.block;
                if (liquidBlock.ammoTypes.isEmpty()) continue;
                Liquid liquid = liquidBlock.ammoTypes.keys().next();
                float targetLiquid = liquidBlock.liquidCapacity / 2f;
                if (liquidTurret.liquids.get(liquid) < targetLiquid) {
                    liquidTurret.handleLiquid(liquidTurret, liquid, targetLiquid - liquidTurret.liquids.get(liquid));
                }
            }
        }
    }
}
