package core;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.world.Block;
import mindustry.world.Tile;

public class DpContent {
    public static Block
    dpRoadWall
            ;

    public static void load() {
        dpRoadWall = Vars.content.getByName(ContentType.block, "dp-road-wall");
    }

    public static void onBlockBuildBeginEvent(EventType.BlockBuildBeginEvent event) {
        if (!event.breaking) return;
        if (event.tile.block() != dpRoadWall) return;
        event.tile.setNet(dpRoadWall, event.team, 0);
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
