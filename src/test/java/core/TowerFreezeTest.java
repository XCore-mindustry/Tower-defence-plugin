package core;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Groups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TowerFreezeTest {

    private TowerFreeze towerFreeze;

    @BeforeEach
    void setUp() {
        towerFreeze = new TowerFreeze();
        TowerFreeze.towersFreeze = new Seq<>();
        Groups.init();
        Vars.state = new GameState();
    }

    @Test
    @DisplayName("update does nothing when game is paused or server is empty")
    void shouldNotUpdateWhenPausedOrEmpty() {
        Vars.state.set(GameState.State.paused);
        assertThat(Groups.player.isEmpty()).isTrue();

        // Should exit cleanly without errors
        towerFreeze.update();
        assertThat(TowerFreeze.towersFreeze).isEmpty();
    }
}
