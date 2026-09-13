package core;

import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class PlayerUI {
    private float dotTimer = 0f;
    private int dotCount = 0;
    private static final float DOT_INTERVAL = 0.5f;

    public void update() {
        for (Player p : Groups.player) {
            String uiLabel = "[grey]\uE80EX[gold]Core [grey]>[] [scarlet]Tower| ⚔ |Defence[lightgrey]\n\n" +
                    "[white]Current wave: [accent]" + WaveSpawner.currentWave + "\n" +
                    "[white]Health multiplier: [orange]" + WaveSpawner.healthMultiplier + "\n" +
                    "[white]Kills: [red]" + WaveSpawner.killedEnemies;
            Call.infoPopup(p.con, uiLabel, 1f, Align.topLeft, 200, 0, 0, 0);
        }
        updateMissionText();
    }

    private void updateMissionText() {
        dotTimer += Time.delta / 60f;
        if (dotTimer >= DOT_INTERVAL) {
            dotTimer = 0f;
            dotCount = (dotCount + 1) % 4;
        }
        String dots = ".".repeat(dotCount);

        Vars.state.rules.mission = WaveSpawner.isWaveActive ? "[scarlet]In progress" + dots + "[]" : "[accent]Next wave: " + (int)WaveSpawner.firstWaveTimer + "s" + dots;

        Call.setRules(Vars.state.rules);
    }
}