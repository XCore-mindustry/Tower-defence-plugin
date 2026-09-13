package core;

import arc.Events;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.world.blocks.storage.CoreBlock;

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
        Groups.build.each(b -> {
            if (Types.cores.contains(b.block) && b.team == WaveSpawner.activeTeam && b.isValid()) {
                Call.label("[pink]" + (int)health + "♡", 0.2f, b.x, b.y);
            }
        });
    }

    public void reset() {
        health = 25000f;
    }

    public void gameOver() {
        Groups.build.each(b -> {
            if (b instanceof CoreBlock.CoreBuild && b.team == Team.sharded) {
                b.tile.setNet(Blocks.air);
            }
        });
    }
}