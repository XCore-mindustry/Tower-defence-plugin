package core;

import arc.Events;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.ai.Pathfinder;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.world.Tile;

/**
 * Keeps wave units on the floor types marked by spawn overlays.
 * Adapted from Xpdustry TowerDefense's TowerPathfinder (MIT, copyright 2024 Xpdustry).
 * See META-INF/NOTICE-xpdustry.md.
 */
public final class RoadPathfinder extends Pathfinder {
    // Mindustry v160 uses bits 0..28 for PathTile; this bit survives its tile updates.
    static final int ROAD_BIT = 1 << 30;
    static final int OFF_ROAD_COST = 6000;
    private volatile boolean[] roadFloors = new boolean[0];

    private RoadPathfinder() {
        // Floor types must be known before Pathfinder packs the world and starts its worker.
        Events.on(EventType.WorldLoadEndEvent.class, event -> loadRoadFloors());
    }

    public static RoadPathfinder install() {
        if (Vars.pathfinder instanceof RoadPathfinder roads) return roads;
        var roads = new RoadPathfinder();
        for (int i = 0; i < Pathfinder.costTypes.size; i++) {
            Pathfinder.costTypes.set(i, roads.wrap(Pathfinder.costTypes.get(i), false));
        }
        // TdAI requests paths from ControlPathfinder, which reads the same packed tiles.
        for (int i = 0; i < ControlPathfinder.costTypes.size; i++) {
            ControlPathfinder.costTypes.set(i, roads.wrap(ControlPathfinder.costTypes.get(i), true));
        }
        Vars.pathfinder = roads;
        return roads;
    }

    void loadRoadFloors() {
        boolean[] floors = new boolean[Vars.content.blocks().size];
        boolean found = false;
        for (Tile tile : Vars.world.tiles) {
            if (tile.overlay() == Blocks.spawn) {
                floors[tile.floor().id] = true;
                found = true;
            }
        }
        // Publish one immutable snapshot; cost callbacks run on pathfinding workers.
        roadFloors = found ? floors : new boolean[0];
    }

    public boolean hasRoadFloors() {
        return roadFloors.length != 0;
    }

    @Override
    public int packTile(Tile tile) {
        int packed = super.packTile(tile);
        var floors = roadFloors;
        int floor = tile.floor().id;
        if (floor >= 0 && floor < floors.length && floors[floor] && !tile.block().isStatic()) {
            packed |= ROAD_BIT;
        }
        return packed;
    }

    PathCost wrap(PathCost original, boolean control) {
        return (team, tile) -> {
            int cost = original.getCost(team, tile);
            // This plugin spawns its enemies on crux, independently of vanilla wave rules.
            if (!hasRoadFloors() || team != Team.crux.id || cost < 0 || (tile & ROAD_BIT) != 0) return cost;
            if (control) {
                // ControlPathfinder treats >=7000 as solid. A preference must not create walls.
                return cost >= 7000 ? cost : Math.min(6999, cost + OFF_ROAD_COST);
            }
            return cost + OFF_ROAD_COST;
        };
    }
}
