package core;

import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.type.Weapon;

public class TdAI extends AIController {
    public static final float ENGAGE_DISTANCE = 10f * Vars.tilesize;

    @Override
    public boolean keepState() {
        return true;
    }

    @Override
    public boolean isLogicControllable() {
        return false;
    }

    @Override
    public void updateMovement() {
        Building core = getTargetCore();
        if (core == null) return;

        if (unit.elevation > 0f) {
            unit.elevation = 0f;
        }

        float touchRadius = core.block.size * Vars.tilesize / 2f + unit.hitSize / 2f;
        if (!unit.within(core, touchRadius)) {
            var result = Vars.controlPath.getPathPosition(unit, Tmp.v2.set(core.x, core.y));
            if (result.move) {
                moveTo(result.dest, 1f, Tmp.v2.epsilonEquals(result.dest, 4.1f) ? 30f : 0f);
            }
        }

        if (!unit.type.omniMovement) {
            unit.lookAt(unit.prefRotation());
        }
    }

    @Override
    public void updateWeapons() {
        Building core = getTargetCore();
        boolean inRange = core != null && unit.within(core, ENGAGE_DISTANCE);

        unit.isShooting = inRange;

        if (inRange) {
            target = core;
            for (var mount : unit.mounts) {
                Weapon weapon = mount.weapon;
                if (!weapon.controllable || weapon.noAttack) continue;

                mount.target = core;
                mount.aimX = core.x;
                mount.aimY = core.y;
                mount.shoot = true;
                mount.rotate = true;
            }
            faceTarget();
        } else {
            target = null;
            for (var mount : unit.mounts) {
                mount.target = null;
                mount.shoot = false;
            }
        }
    }

    public Building getTargetCore() {
        Building core = unit != null ? unit.closestEnemyCore() : null;
        if (core != null && core.isValid()) return core;
        if (!WaveSpawner.cores.isEmpty()) {
            var fallback = WaveSpawner.cores.first();
            if (fallback != null && fallback.isValid()) return fallback;
        }
        return null;
    }
}
