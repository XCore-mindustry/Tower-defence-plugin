
package core;

import arc.Events;
import core.towerGetter.TowerGetter;
import mindustry.Vars;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Blocks;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class DpContent {

    public static Block dpRoadWall, dpGauss;

    private static final Set<String> notifiedAttempts = new HashSet<>();

    public static void load() {
        dpRoadWall = Vars.content.getByName(ContentType.block, "dp-road-wall");
        dpGauss = Vars.content.getByName(ContentType.block, "dp-gauss");

        Events.on(EventType.WorldLoadEvent.class, event -> notifiedAttempts.clear());
    }

    public static void onBuildSelectEvent(EventType.BuildSelectEvent event, TowerGetter towerGetter) {
        if (event.breaking) return;

        if (!(event.tile.build instanceof ConstructBlock.ConstructBuild build)) return;

        String towerName = build.current.name;

        Player player = findPlayer(
                event.builder,
                Collections.newSetFromMap(new IdentityHashMap<>())
        );

        if (!towerGetter.isLocked(towerName, player)) return;

        int x = event.tile.x;
        int y = event.tile.y;

        event.tile.setNet(build.previous, event.team, build.rotation);

        Groups.unit.each(unit ->
                unit.plans.remove(plan -> plan.x == x && plan.y == y)
        );

        event.team.data().plans.remove(plan -> plan.x == x && plan.y == y);

        if (player != null && player.uuid() != null) {
            String attempt = player.uuid() + ":" + towerName + ":" + x + ":" + y;

            if (notifiedAttempts.add(attempt)) {
                String message = towerGetter.getRequirementMessage(towerName);

                if (message != null) {
                    player.sendMessage(message);
                }
            }
        }
    }

    private static Player findPlayer(Unit unit, Set<Unit> checked) {
        if (unit == null || !checked.add(unit)) return null;

        if (unit.isPlayer()) {
            return unit.getPlayer();
        }

        if (unit.controller() instanceof BuilderAI ai) {
            Player player = findPlayer(ai.following, checked);

            if (player != null) return player;

            return findPlayer(ai.assistFollowing, checked);
        }

        return null;
    }

    public static void onPickupEvent(EventType.PickupEvent event) {
        if (event.build == null) return;

        if (event.build.block == dpRoadWall) {
            Tile tile = event.build.tile;

            tile.setNet(
                    Blocks.copperWall,
                    event.build.team,
                    event.build.rotation
            );
        }
    }
}