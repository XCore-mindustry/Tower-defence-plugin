package core;

import core.towerGetter.TowerGetter;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

public class DpContent {
    public static Block
    dpRoadWall, dpGauss
            ;

    public static void load() {
        dpRoadWall = Vars.content.getByName(ContentType.block, "dp-road-wall");
        dpGauss = Vars.content.getByName(ContentType.block, "dp-gauss");
    }

    public static void onBlockBuildBeginEvent(EventType.BlockBuildBeginEvent event, TowerGetter towerGetter) {
        if (!(event.tile.build instanceof ConstructBlock.ConstructBuild build)) return;

        Player player = Groups.player.find(p -> p.unit() == event.unit);

        if (player == null) return;

        if (towerGetter.isLocked(build.current.name, player)) {
            event.tile.setNet(build.previous, event.team, build.rotation);
        }
    }

    public static void onPickupEvent(EventType.PickupEvent event) {
        if (event.build == null) return;

        if (event.build.block == dpRoadWall) {
            Tile tile = event.build.tile;

            tile.setNet(
                    Blocks.copperWall, event.build.team, event.build.rotation
            );
        }
    }
}
