package core.towerGetter;

import core.towers.Gauss;
import mindustry.gen.Player;

import java.util.ArrayList;
import java.util.List;

public class TowerGetter {
    private final List<TowerUnlock> towers = new ArrayList<>();

    public TowerGetter() {
        towers.add(new Gauss());
    }

    public void onWorldLoad() {
        for (TowerUnlock tower : towers) {
            tower.onWorldLoad();
        }
    }

    public void onPlayerJoin(Player player) {
        for (TowerUnlock tower : towers) {
            tower.onPlayerJoin(player);
        }
    }

    public void onWaveCleared(int wave) {
        for (TowerUnlock tower : towers) {
            tower.onWaveCleared(wave);
        }
    }

    public boolean hasTower(String towerName, Player player) {
        if (player == null) return false;

        for (TowerUnlock tower : towers) {
            if (tower.getTowerName().equals(towerName)) {
                return tower.hasAchievement(player);
            }
        }

        return false;
    }

    public boolean isLocked(String towerName, Player player) {
        for (TowerUnlock tower : towers) {
            if (tower.getTowerName().equals(towerName)) {
                return tower.isLocked(player);
            }
        }

        return false;
    }

    public String getRequirementMessage(String towerName) {
        for (TowerUnlock tower : towers) {
            if (tower.getTowerName().equals(towerName)) {
                return tower.getRequirementMessage();
            }
        }

        return null;
    }
}