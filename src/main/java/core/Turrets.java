package core;

import mindustry.gen.Groups;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class Turrets {

    public void update() {
        Groups.build.each(build -> {
            if (!(build instanceof ItemTurret.ItemTurretBuild turret)) return;
            ItemTurret block = (ItemTurret) turret.block;
            if (block.ammoTypes.isEmpty()) return;

            int target = block.maxAmmo / 2;
            Item ammo = block.ammoTypes.keys().next();

            if (turret.totalAmmo > target) {
                turret.items.remove(ammo, turret.totalAmmo - target);
            } else {
                while (turret.totalAmmo < target) {
                    int before = turret.totalAmmo;
                    turret.handleItem(turret, ammo);
                    if (turret.totalAmmo <= before) break;
                }
            }
        });
    }
}