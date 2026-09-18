package core;

import arc.Events;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.mod.Plugin;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.Random;

import static core.DpContent.load;

public class Main extends Plugin {
    public final WaveSpawner waveSpawner = new WaveSpawner();
    public final TowerFreeze towerFreeze = new TowerFreeze();
    public final Base base = new Base();
    public final PlayerUI ui = new PlayerUI();
    public static Random random = new Random();
    public final Turrets turrets = new Turrets();
    public final Bonus bonus = new Bonus();

    public void init() {
        Log.info("tower-defence-plugin loaded. uilabel");

        Timer.schedule(ui::update, 0f, 1f);
        Timer.schedule(base::update, 0f, 0.5f);
        Timer.schedule(turrets::update, 0f, 0.5f);
        Timer.schedule(towerFreeze::update, 0f, 0.25f);
        Timer.schedule(waveSpawner::updateDisarm, 0f, 0.25f);
        Timer.schedule(bonus::updateVisuals, 0f, 0.2f);

        Events.run(EventType.Trigger.update, this::update);

        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            towerFreeze.onBlockBuildEndEvent(e);
            turrets.onBlockBuildEndEvent(e);
        });
        Events.on(EventType.BlockDestroyEvent.class, e -> {
            towerFreeze.onBlockDestroyEvent(e);
            turrets.onBlockDestroyEvent(e);
        });
        Events.on(EventType.WorldLoadEvent.class, e -> {
            load();
            waveSpawner.reset();
            base.reset();
            bonus.reset();
            waveSpawner.findCores();
            towerFreeze.onWorldLoadEvent();
            turrets.onWorldLoadEvent();
            waveSpawner.findSpawnPoints();
            roadBorders();
            waveSpawner.placeProc();
        });
        Events.on(EventType.UnitDestroyEvent.class, event -> {
            waveSpawner.onUnitDestroyEvent(event);
            bonus.onUnitDestroyEvent(event);
        });
        Events.on(EventType.BuildDamageEvent.class, base::onBuildDamageEvent);
        Events.on(EventType.BlockBuildBeginEvent.class, DpContent::onBlockBuildBeginEvent);
    }

    public void update() {
        waveSpawner.update();
        bonus.update();
    }

    public void roadBorders() {
        Block target = Vars.content.getByName(ContentType.block, "metal-wall-2");
        if (target == null) return;
        for (Tile tile : Vars.world.tiles) {
            if (tile == null) continue;
            if (tile.block() == target) {
                tile.setFloor(Blocks.empty.asFloor());
                tile.setBlock(DpContent.dpRoadWall, WaveSpawner.activeTeam);
            }
        }
    }
}
