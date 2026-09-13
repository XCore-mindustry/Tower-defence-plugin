package core;

import arc.util.Align;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class PlayerUI {

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
        Vars.state.rules.mission = WaveSpawner.isWaveActive ? "[scarlet]In progress. Next: " + (int)WaveSpawner.waveTimer + "s" + "[]" : "[accent]Next wave: " + (int)WaveSpawner.waveTimer + "s";
        Call.setRules(Vars.state.rules);
    }
}