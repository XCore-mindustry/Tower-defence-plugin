package core;

import mindustry.gen.Groups;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;

public class Turrets {

    public void update() {
        Groups.build.each(build -> {
            if (build instanceof ItemTurret.ItemTurretBuild turret) {
                ItemTurret block = (ItemTurret) turret.block;
                if (block.ammoTypes.isEmpty()) return;
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
            }
            else if (build instanceof LiquidTurret.LiquidTurretBuild liquidTurret) {
                LiquidTurret liquidBlock = (LiquidTurret) liquidTurret.block;
                if (liquidBlock.ammoTypes.isEmpty()) return;
                Liquid liquid = liquidBlock.ammoTypes.keys().next();
                float targetLiquid = liquidBlock.liquidCapacity / 2f;
                if (liquidTurret.liquids.get(liquid) < targetLiquid) {
                    liquidTurret.handleLiquid(liquidTurret, liquid, targetLiquid - liquidTurret.liquids.get(liquid));
                }
            }
        });
    }
}