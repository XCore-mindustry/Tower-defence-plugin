package core;

import arc.util.Align;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class PlayerUI {
    private String lastMission = "";

    public void update() {
        if (Groups.player.isEmpty() || Vars.state == null || Vars.state.isPaused()) return;

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
        String newMission = WaveSpawner.isWaveActive
                ? "[scarlet]In progress. Next: " + (int)WaveSpawner.waveTimer + "s[]"
                : "[accent]Next wave: " + (int)WaveSpawner.waveTimer + "s";
        if (!newMission.equals(lastMission)) {
            lastMission = newMission;
            Vars.state.rules.mission = newMission;
            Call.setRules(Vars.state.rules);
        }
    }
}
