package core;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;

public class Base {
    public float health = 25000f;

    public void onBuildDamageEvent(EventType.BuildDamageEvent event) {
        if (event.build == null) return;
        event.build.health = event.build.maxHealth;
        if (!Types.cores.contains(event.build.block)) return;
        if (event.build.team != WaveSpawner.activeTeam) return;
        health -= event.source.damage;
        if (health <= 0f) {
            health = 0f;
            gameOver();
        }
    }

    public void update() {
        if (Groups.player.isEmpty() || Vars.state == null || Vars.state.isPaused()) return;
        for (int i = 0; i < WaveSpawner.cores.size; i++) {
            var b = WaveSpawner.cores.get(i);
            if (b != null && b.isValid() && b.team == WaveSpawner.activeTeam) {
                Call.label("[pink]" + (int)health + "♡", 0.5f, b.x, b.y);
            }
        }
    }

    public void reset() {
        health = 25000f;
    }

    public void gameOver() {
        for (int i = 0; i < WaveSpawner.cores.size; i++) {
            var b = WaveSpawner.cores.get(i);
            if (b != null && b.isValid() && b.team == Team.sharded) {
                b.tile.setNet(Blocks.air);
            }
        }
    }
}
