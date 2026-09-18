package core;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.storage.CoreBlock;

import static core.Main.random;
import static core.Types.*;

public class WaveSpawner {
    public static final int MAX_ENEMIES_CAP = 350;

    public static int currentWave = 0;
    public static int waveInterval = 30;
    public static float healthMultiplier = 0.25f;
    public static boolean isWaveActive = false;
    public static Team activeTeam = Team.sharded;
    public Block spawnBlock = Blocks.spawn;
    public static int killedEnemies = 0;
    public static float waveTimer = 60f;

    public static Seq<Vec2> spawnPoints = new Seq<>();
    public static Seq<CoreBlock.CoreBuild> cores = new Seq<>();

    public static int countAliveEnemies() {
        return Groups.unit.count(u -> u.isValid() && !u.dead() && u.team != activeTeam);
    }

    public void spawnWave() {
        if (spawnPoints.isEmpty()) return;

        int aliveEnemies = countAliveEnemies();
        if (aliveEnemies >= MAX_ENEMIES_CAP) {
            waveTimer = 5f;
            return;
        }

        healthMultiplier = 0.25f * Mathf.pow(1.04f, currentWave);

        int unitCount = Mathf.random(5 + currentWave / 2, 10 + currentWave / 2);
        if (unitCount > 100) unitCount = Mathf.random(75, 100);

        int[] tiers = generateTiers(unitCount, currentWave);

        for (Vec2 point : spawnPoints) {
            for (int tier : tiers) {
                UnitType type = getUnit(tier);
                if (type == null) continue;
                float x = point.x + Mathf.range(12f);
                float y = point.y + Mathf.range(12f);
                Unit unit = type.create(Team.crux);
                unit.set(x, y);
                unit.health = unit.maxHealth * healthMultiplier;
                unit.add();
            }
        }

        currentWave++;
        isWaveActive = true;
    }

    public void onUnitDestroyEvent(EventType.UnitDestroyEvent event) {
        if (event.unit.team() == activeTeam) return;
        int alive = countAliveEnemies();
        if (alive == 0 && isWaveActive) {
            isWaveActive = false;
            if (waveTimer > 5) waveTimer = 5;
        }
        Seq<ItemStack> loot = new Seq<>(getLootForEnemy(currentWave));
        if (Team.sharded.core() != null) {
            for (ItemStack s : loot) Team.sharded.core().items.add(s.item, s.amount);
        }
        Call.label(toText(loot), 1f, event.unit.x, event.unit.y + 10);
        killedEnemies++;
    }

    public Seq<ItemStack> getLootForEnemy(int wave) {
        Seq<ItemStack> loot = new Seq<>();
        Seq<Item> available = new Seq<>();

        available.add(Items.copper);
        available.add(Items.lead);

        if (wave >= 15) {
            available.add(Items.graphite);
            available.add(Items.metaglass);
        }
        if (wave >= 30) {
            available.add(Items.silicon);
            available.add(Items.titanium);
        }
        if (wave >= 45) {
            available.add(Items.plastanium);
            available.add(Items.phaseFabric);
        }
        if (wave >= 60) {
            available.add(Items.thorium);
            available.add(Items.surgeAlloy);
        }

        int typesCount = Mathf.random(1, Math.min(3, available.size));
        available.shuffle();
        for (int i = 0; i < typesCount; i++) {
            Item item = available.get(i);
            int amount = Mathf.random(10, 50);
            loot.add(new ItemStack(item, amount));
        }

        return loot;
    }

    private UnitType getUnit(int tier) {
        return switch (tier) {
            case 2 -> tier2Units.random();
            case 3 -> tier3Units.random();
            case 4 -> tier4Units.random();
            case 5 -> tier5Units.random();
            default -> tier1Units.random();
        };
    }

    public int[] generateTiers(int count, int wave) {
        int[] tiers = new int[count];
        float[] probs = new float[5];

        float base1 = Math.max(0, 100 - wave * 1f);
        float base2 = wave >= 20 ? (wave - 20) * 0.8f + 5f : 0f;
        float base3 = wave >= 40 ? (wave - 40) * 0.7f + 5f : 0f;
        float base4 = wave >= 60 ? (wave - 60) * 0.6f + 3f : 0f;
        float base5 = wave >= 80 ? (wave - 80) * 0.5f + 2f : 0f;

        float sum = base1 + base2 + base3 + base4 + base5;
        probs[0] = base1 / sum;
        probs[1] = base2 / sum;
        probs[2] = base3 / sum;
        probs[3] = base4 / sum;
        probs[4] = base5 / sum;

        for (int i = 0; i < count; i++) {
            float r = random.nextFloat();
            float cumulative = 0;
            for (int t = 0; t < 5; t++) {
                cumulative += probs[t];
                if (r < cumulative) {
                    tiers[i] = t + 1;
                    break;
                }
            }
        }
        return tiers;
    }

    public void findSpawnPoints() {
        spawnPoints.clear();
        if (spawnBlock == null) {
            for (Block b : Vars.content.blocks()) {
                if (b instanceof mindustry.world.blocks.environment.SpawnBlock) {
                    spawnBlock = b;
                    break;
                }
            }
        }
        if (spawnBlock == null) return;
        for (Tile tile : Vars.world.tiles) {
            if (tile != null && tile.overlay() == spawnBlock) {
                spawnPoints.add(new Vec2(tile.worldx(), tile.worldy()));
            }
        }
    }

    public String toText(Seq<ItemStack> loot) {
        StringBuilder sb = new StringBuilder();
        for (ItemStack stack : loot) {
            String icon = Fonts.getUnicodeStr(stack.item.name);
            sb.append(icon).append(stack.amount).append(" ");
        }
        return sb.toString();
    }

    public void update() {
        if (Vars.state == null || !Vars.state.isPlaying() || Vars.state.isPaused() || Groups.player.isEmpty()) {
            return;
        }

        waveTimer -= Time.delta / 60f;
        if (waveTimer <= 0f) {
            spawnWave();
            waveTimer = waveInterval;
        }
    }

    public void reset() {
        currentWave = 0;
        healthMultiplier = 0.25f;
        isWaveActive = false;
        killedEnemies = 0;
        waveTimer = 60f;
        spawnPoints.clear();
    }

    public void findCores() {
        cores.clear();
        Groups.build.each(b -> {
            if (b instanceof CoreBlock.CoreBuild cb && b.team == Team.sharded) {
                cores.add(cb);
            }
        });
    }

    public void updateDisarm() {
        if (cores.isEmpty() || Groups.player.isEmpty() || Vars.state == null || Vars.state.isPaused()) return;
        cores.removeAll(c -> c == null || !c.isValid());
        if (cores.isEmpty()) return;

        float thresholdDst2 = (15f * Vars.tilesize) * (15f * Vars.tilesize);
        Groups.unit.each(u -> {
            if (u.team == activeTeam || !u.isValid() || u.dead()) return;

            float best2 = Float.MAX_VALUE;
            for (int i = 0; i < cores.size; i++) {
                var c = cores.get(i);
                float d2 = u.dst2(c);
                if (d2 < best2) best2 = d2;
            }
            if (best2 > thresholdDst2) {
                u.apply(StatusEffects.disarmed, 25f);
            }
        });
    }

    public void placeProc() {
        Tile tile = Vars.world.tile(0, 0);
        if (tile == null) return;
        tile.setNet(Blocks.worldProcessor, Team.crux, 0);
        if (!(tile.build instanceof LogicBlock.LogicBuild logic)) return;
        String code = """
                setrate 1000
                ulocate building core true @copper xcore ycore found core
                jump 1 equal found false
                fetch unitCount uc @crux 0 Block
                jump 6 lessThanEq i uc
                set i -1
                op add i i 1
                fetch unit obj @crux i Block
                jump 3 equal obj null
                ubind obj
                ucontrol pathfind xcore ycore 0 0 0
                """;
        logic.updateCode(code);
    }
}
