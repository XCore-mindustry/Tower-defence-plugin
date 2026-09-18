package core;

import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Groups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TurretsTest {

    private Turrets turrets;

    @BeforeEach
    void setUp() {
        turrets = new Turrets();
        Groups.init();
        Vars.state = new GameState();
    }

    @Test
    @DisplayName("update does nothing when empty server or paused")
    void shouldNotUpdateWhenEmptyOrPaused() {
        Vars.state.set(GameState.State.paused);
        assertThat(Groups.player.isEmpty()).isTrue();

        turrets.update();
        assertThat(turrets.activeTurrets).isEmpty();
    }
}
