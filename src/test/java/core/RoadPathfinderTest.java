package core;

import arc.Events;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.ai.Pathfinder;
import mindustry.content.Blocks;
import mindustry.core.ContentLoader;
import mindustry.core.GameState;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.graphics.CacheLayer;
import mindustry.world.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoadPathfinderTest {
    private Seq<Pathfinder.PathCost> flowCosts;
    private Seq<Pathfinder.PathCost> controlCosts;
    private Pathfinder previous;
    private RoadPathfinder roads;

    @BeforeEach
    void setUp() {
        flowCosts = Pathfinder.costTypes.copy();
        controlCosts = ControlPathfinder.costTypes.copy();
        previous = Vars.pathfinder;
        Vars.headless = true;
        CacheLayer.init();
        Vars.content = new ContentLoader();
        Vars.content.createBaseContent();
        Vars.state = new GameState();
        Vars.world = new World();
        Vars.world.setGenerating(true);
        Vars.world.resize(3, 1);
        Vars.world.tiles.set(0, 0, new Tile(0, 0, Blocks.sand, Blocks.spawn, Blocks.air));
        Vars.world.tiles.set(1, 0, new Tile(1, 0, Blocks.sand, Blocks.air, Blocks.air));
        Vars.world.tiles.set(2, 0, new Tile(2, 0, Blocks.stone, Blocks.air, Blocks.air));
        roads = RoadPathfinder.install();
        Events.fire(new EventType.WorldLoadEndEvent());
    }

    @AfterEach
    void tearDown() {
        Pathfinder.costTypes.clear().addAll(flowCosts);
        ControlPathfinder.costTypes.clear().addAll(controlCosts);
        Vars.pathfinder = previous;
        Events.clear();
    }

    @Test
    void spawnFloorMarksTheWholeRoadAndBothPathfindersPreferIt() {
        int road = roads.packTile(Vars.world.tile(1, 0));
        int outside = roads.packTile(Vars.world.tile(2, 0));

        assertThat(road & RoadPathfinder.ROAD_BIT).isNotZero();
        assertThat(outside & RoadPathfinder.ROAD_BIT).isZero();
        var flow = Pathfinder.costTypes.get(Pathfinder.costGround);
        var control = ControlPathfinder.costTypes.get(ControlPathfinder.costIdGround);
        assertThat(flow.getCost(Team.crux.id, outside))
                .isEqualTo(flowCosts.get(Pathfinder.costGround).getCost(Team.crux.id, outside) + 6000);
        assertThat(control.getCost(Team.crux.id, outside))
                .isEqualTo(controlCosts.get(ControlPathfinder.costIdGround).getCost(Team.crux.id, outside) + 6000);
        assertThat(flow.getCost(Team.crux.id, road))
                .isEqualTo(flowCosts.get(Pathfinder.costGround).getCost(Team.crux.id, road));
        assertThat(control.getCost(Team.crux.id, road))
                .isEqualTo(controlCosts.get(ControlPathfinder.costIdGround).getCost(Team.crux.id, road));
    }

    @Test
    void defenderCostsAndImpassableTilesAreUnchanged() {
        assertThat(roads.wrap((team, tile) -> 7, false).getCost(Team.sharded.id, 0)).isEqualTo(7);
        assertThat(roads.wrap((team, tile) -> -1, false).getCost(Team.crux.id, 0)).isEqualTo(-1);
        assertThat(roads.wrap((team, tile) -> -1, true).getCost(Team.crux.id, 0)).isEqualTo(-1);
        assertThat(roads.wrap((team, tile) -> 7000, true).getCost(Team.crux.id, 0)).isEqualTo(7000);
        // The control pathfinder must still allow a costly tile if it was passable before.
        assertThat(roads.wrap((team, tile) -> 6001, true).getCost(Team.crux.id, 0)).isEqualTo(6999);
    }

    @Test
    void roadFloorsAreReplacedWhenAnotherMapLoads() {
        Vars.world.tiles.set(0, 0, new Tile(0, 0, Blocks.stone, Blocks.spawn, Blocks.air));
        Events.fire(new EventType.WorldLoadEndEvent());

        assertThat(roads.packTile(Vars.world.tile(1, 0)) & RoadPathfinder.ROAD_BIT).isZero();
        assertThat(roads.packTile(Vars.world.tile(2, 0)) & RoadPathfinder.ROAD_BIT).isNotZero();
    }

    @Test
    void mapsWithoutSpawnMarkersKeepVanillaCosts() {
        Vars.world.tiles.set(0, 0, new Tile(0, 0, Blocks.sand, Blocks.air, Blocks.air));
        Events.fire(new EventType.WorldLoadEndEvent());

        assertThat(roads.hasRoadFloors()).isFalse();
        assertThat(roads.wrap((team, tile) -> 7, false).getCost(Team.crux.id, 0)).isEqualTo(7);
    }

    @Test
    void installationDoesNotWrapCostsTwice() {
        var cost = Pathfinder.costTypes.get(Pathfinder.costGround);

        assertThat(RoadPathfinder.install()).isSameAs(roads);
        assertThat(Pathfinder.costTypes.get(Pathfinder.costGround)).isSameAs(cost);
    }
}
