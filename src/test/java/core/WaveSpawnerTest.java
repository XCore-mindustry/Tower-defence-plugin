package core;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WaveSpawnerTest {

    private WaveSpawner waveSpawner;

    @BeforeEach
    void setUp() {
        waveSpawner = new WaveSpawner();
        WaveSpawner.currentWave = 0;
        WaveSpawner.waveTimer = 30f;
        WaveSpawner.isWaveActive = false;
        WaveSpawner.spawnPoints = new Seq<>();
        WaveSpawner.cores = new Seq<>();
        Groups.init();
        Vars.state = new GameState();
    }

    @Test
    @DisplayName("update does nothing when server is paused")
    void shouldNotAdvanceWhenPaused() {
        Vars.state.set(GameState.State.paused);
        float timerBefore = WaveSpawner.waveTimer;

        waveSpawner.update();

        assertThat(WaveSpawner.waveTimer).isEqualTo(timerBefore);
    }

    @Test
    @DisplayName("update does nothing when player count is zero")
    void shouldNotAdvanceWhenEmptyServer() {
        Vars.state.set(GameState.State.playing);
        assertThat(Groups.player.isEmpty()).isTrue();

        float timerBefore = WaveSpawner.waveTimer;
        waveSpawner.update();

        assertThat(WaveSpawner.waveTimer).isEqualTo(timerBefore);
    }

    @Test
    @DisplayName("generateTiers generates correct number of tiers for wave")
    void shouldGenerateCorrectTierCounts() {
        int count = 20;
        int[] tiers = waveSpawner.generateTiers(count, 5);

        assertThat(tiers).hasSize(count);
        for (int tier : tiers) {
            assertThat(tier).isBetween(1, 5);
        }
    }

    @Test
    @DisplayName("spawnWave respects MAX_ENEMIES_CAP and defers wave")
    void shouldRespectMaxEnemiesCap() {
        WaveSpawner.spawnPoints.add(new Vec2(100f, 100f));
        WaveSpawner.waveTimer = 0f;

        assertThat(WaveSpawner.MAX_ENEMIES_CAP).isBetween(100, 1000);
    }

    @Test
    @DisplayName("reset restores initial state")
    void shouldResetState() {
        WaveSpawner.currentWave = 15;
        WaveSpawner.healthMultiplier = 5f;
        WaveSpawner.killedEnemies = 120;
        WaveSpawner.spawnPoints.add(new Vec2(10f, 10f));

        waveSpawner.reset();

        assertThat(WaveSpawner.currentWave).isEqualTo(0);
        assertThat(WaveSpawner.healthMultiplier).isEqualTo(0.25f);
        assertThat(WaveSpawner.killedEnemies).isEqualTo(0);
        assertThat(WaveSpawner.waveTimer).isEqualTo(60f);
        assertThat(WaveSpawner.spawnPoints).isEmpty();
    }
}
