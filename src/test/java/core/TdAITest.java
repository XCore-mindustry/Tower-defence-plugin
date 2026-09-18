package core;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Groups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TdAITest {

    private TdAI ai;

    @BeforeEach
    void setUp() {
        ai = new TdAI();
        WaveSpawner.cores = new Seq<>();
        Groups.init();
        Vars.state = new GameState();
    }

    @Test
    @DisplayName("keepState returns true to protect custom AI from reset")
    void shouldKeepState() {
        assertThat(ai.keepState()).isTrue();
    }

    @Test
    @DisplayName("isLogicControllable returns false to prevent player processor hijacking")
    void shouldNotBeLogicControllable() {
        assertThat(ai.isLogicControllable()).isFalse();
    }

    @Test
    @DisplayName("engage distance corresponds to 10 tiles (80 world units)")
    void shouldHaveExpectedEngageDistance() {
        assertThat(TdAI.ENGAGE_DISTANCE).isEqualTo(10f * Vars.tilesize);
    }

    @Test
    @DisplayName("getTargetCore returns null when no cores exist")
    void shouldHandleNoCoresGracefully() {
        assertThat(ai.getTargetCore()).isNull();
    }
}
