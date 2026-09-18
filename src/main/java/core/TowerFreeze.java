package core;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.ctype.ContentType;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.Block;

public class TowerFreeze {
    public Block dpTowerFreeze;
    public float towerFreezeRange = 80f;

    public static Seq<Building> towersFreeze = new Seq<>();

    public void onBlockBuildEndEvent(EventType.BlockBuildEndEvent event) {
        if (event.tile.block() == dpTowerFreeze && event.tile.build != null) {
            if (!towersFreeze.contains(event.tile.build)) {
                towersFreeze.add(event.tile.build);
            }
        }
    }

    public void onBlockDestroyEvent(EventType.BlockDestroyEvent event) {
        if (event.tile.block() == dpTowerFreeze && event.tile.build != null) {
            towersFreeze.remove(event.tile.build);
        }
    }

    public void onWorldLoadEvent() {
        towersFreeze.clear();
        dpTowerFreeze = Vars.content.getByName(ContentType.block, "dp-tower-freeze");
        if (dpTowerFreeze == null) Log.warn("Block dp-tower-freeze not found.");
    }

    public void update() {
        if (towersFreeze.isEmpty() || Groups.player.isEmpty() || Vars.state == null || Vars.state.isPaused()) return;
        towersFreeze.removeAll(b -> b == null || !b.isValid());

        for (int i = 0; i < towersFreeze.size; i++) {
            Building tower = towersFreeze.get(i);
            Units.nearby(null, tower.x, tower.y, towerFreezeRange, unit -> {
                if (unit != null && unit.isValid() && !unit.dead() && unit.team != Team.sharded) {
                    unit.apply(StatusEffects.freezing, 60f);
                }
            });
        }
    }
}
