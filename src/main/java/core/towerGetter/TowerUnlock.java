
package core.towerGetter;

import arc.Core;
import arc.files.Fi;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public abstract class TowerUnlock {

    private final String towerName;
    private final String displayName;
    private final String mapName;
    private final int targetWave;
    private final Fi file;

    private final Set<String> participants = new HashSet<>();
    private final Set<String> completedPlayers = new HashSet<>();

    private boolean active;
    private boolean completed;

    public TowerUnlock(String towerName, String displayName, String mapName, int targetWave, String fileName) {
        this.towerName = towerName;
        this.displayName = displayName;
        this.mapName = mapName;
        this.targetWave = targetWave;
        this.file = new Fi("config/thowersWhiteList/" + fileName);

        load();
    }

    public void onWorldLoad() {
        participants.clear();
        completed = false;
        active = false;

        Core.app.post(() -> {
            active = isOwnMap();

            if (active) {
                Groups.player.each(this::onPlayerJoin);
            }
        });
    }

    public void onPlayerJoin(Player player) {
        if (!active || player == null || player.uuid() == null) return;

        participants.add(player.uuid());
    }

    public void onWaveCleared(int wave) {
        if (!active || completed || wave != targetWave) return;

        completed = true;

        Set<String> newlyUnlocked = new HashSet<>();

        for (String uuid : participants) {
            if (completedPlayers.add(uuid)) {
                newlyUnlocked.add(uuid);
            }
        }

        for (Player player : Groups.player) {
            if (newlyUnlocked.contains(player.uuid())) {
                player.sendMessage("[acid]" + displayName + " unlocked!");
            }
        }

        if (!newlyUnlocked.isEmpty()) {
            save();
        }
    }

    public boolean hasAchievement(Player player) {
        return player != null && completedPlayers.contains(player.uuid());
    }

    public boolean isLocked(Player player) {
        if (isOwnMap()) return false;

        return !hasAchievement(player);
    }

    private boolean isOwnMap() {
        if (Vars.state == null || Vars.state.map == null) return false;

        String currentMap = Vars.state.map.plainName();

        return normalizeMapName(currentMap).equals(normalizeMapName(mapName));
    }

    private String normalizeMapName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    public String getTowerName() {
        return towerName;
    }

    private void load() {
        file.parent().mkdirs();

        if (!file.exists()) return;

        Properties properties = new Properties();

        try {
            properties.load(new StringReader(file.readString()));

            for (String uuid : properties.stringPropertyNames()) {
                if (Boolean.parseBoolean(properties.getProperty(uuid))) {
                    completedPlayers.add(uuid);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tower whitelist: " + file.path(), e);
        }
    }

    private void save() {
        file.parent().mkdirs();

        Properties properties = new Properties();

        for (String uuid : completedPlayers) {
            properties.setProperty(uuid, "true");
        }

        try {
            StringWriter writer = new StringWriter();
            properties.store(writer, null);

            file.writeString(writer.toString(), false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save tower whitelist: " + file.path(), e);
        }
    }
}