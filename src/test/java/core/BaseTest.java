package core;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Groups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseTest {

    private Base base;

    @BeforeEach
    void setUp() {
        base = new Base();
        WaveSpawner.cores = new Seq<>();
        Groups.init();
        Vars.state = new GameState();
    }

    @Test
    @DisplayName("reset restores base health to 25000")
    void shouldResetHealth() {
        base.health = 500f;
        base.reset();
        assertThat(base.health).isEqualTo(25000f);
    }

    @Test
    @DisplayName("update does not throw or search world when empty")
    void shouldHandleEmptyGracefully() {
        Vars.state.set(GameState.State.paused);
        base.update();
        assertThat(base.health).isEqualTo(25000f);
    }
}
