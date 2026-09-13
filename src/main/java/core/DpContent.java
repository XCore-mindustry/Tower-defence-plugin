package core;

import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.world.Block;

public class DpContent {
    public static Block
    dpRoadWall
            ;

    public static void load() {
        dpRoadWall = Vars.content.getByName(ContentType.block, "dp-road-wall");
    }

    public static void onBlockBuildBeginEvent(EventType.BlockBuildBeginEvent event) {
        if (!event.breaking) return;
        event.tile.setNet(dpRoadWall, event.team, 0);
    }
}
