package core;

import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

import static core.Main.random;

public class Bonus {
    public float minInterval = 30f;
    public float maxInterval = 120f;
    public float timer = 60f;

    public ObjectMap<Unit, BONUS> markedUnits = new ObjectMap<>();

    public enum BONUS {
        FREEZE, STUN, POISON, FIRE, DISARMING, BLAST
    }

    public Unit getRandomUnit() {
        Seq<Unit> units = new Seq<>();
        Groups.unit.each(u -> {
            if (!u.isValid() || u.dead()) return;
            if (u.team == Team.crux) units.add(u);
        });
        return units.isEmpty() ? null : units.random();
    }

    public void update() {
        timer -= Time.delta / 60f;
        if (timer <= 0) {
            Unit randomUnit = getRandomUnit();
            if (randomUnit != null) {
                BONUS randomBonus = BONUS.values()[random.nextInt(BONUS.values().length)];
                markedUnits.put(randomUnit, randomBonus);
            }
            timer = minInterval + random.nextFloat() * (maxInterval - minInterval);
        }

        markedUnits.each((unit, bonus) -> {
            if (!unit.isValid() || unit.dead()) return;
            Color color = switch (bonus) {
                case FREEZE -> Color.cyan;
                case STUN -> Color.yellow;
                case POISON -> Color.purple;
                case FIRE -> Color.orange;
                case DISARMING -> Color.white;
                case BLAST -> Color.red;
            };
            Call.effect(Fx.circleColorSpark, unit.x, unit.y, 0, color);
        });
    }

    public void onUnitDestroyEvent(EventType.UnitDestroyEvent event) {
        BONUS bonus = markedUnits.get(event.unit);
        if (bonus == null) return;
        switch (bonus) {
            case FREEZE -> {
                applyEffect(StatusEffects.freezing);
                Call.sendMessage("[cyan]Freezing...");
            }
            case FIRE -> {
                applyEffect(StatusEffects.melting, StatusEffects.burning);
                Call.sendMessage("[orange]Firing...");
            }
            case STUN -> {
                applyEffect(StatusEffects.unmoving, StatusEffects.slow);
                Call.sendMessage("[yellow]Stunning...");
            }
            case BLAST -> {
                createExplosion(event.unit, 10f * Vars.tilesize);
                Call.sendMessage("[red]Exploding...");
            }
            case POISON -> {
                applyEffect(StatusEffects.corroded);
                Call.sendMessage("[purple]Poisoning...");
            }
            case DISARMING -> {
                applyEffect(StatusEffects.disarmed);
                Call.sendMessage("[white]Disarming...");
            }
        }
        markedUnits.remove(event.unit);
    }

    public void applyEffect(StatusEffect effect) {
        Groups.unit.each(unit -> {
            if (!unit.isValid() || unit.dead()) return;
            if (unit.team != Team.crux) return;
            unit.apply(effect, 10 * 60);
        });
    }

    public void applyEffect(StatusEffect effect1, StatusEffect effect2) {
        Groups.unit.each(unit -> {
            if (!unit.isValid() || unit.dead()) return;
            if (unit.team != Team.crux) return;
            unit.apply(effect1, 5 * 60);
            unit.apply(effect2, 10 * 60);
        });
    }

    public void applyEffect(StatusEffect effect, boolean isPositive) {
        Groups.unit.each(unit -> {
            if (!unit.isValid() || unit.dead()) return;
            if (!isPositive || unit.team != WaveSpawner.activeTeam) return;
            unit.apply(effect, 60 * 60);
        });
    }

    public void createExplosion(Unit unit, float range) {
        float x = unit.x;
        float y = unit.y;
        float damage = unit.maxHealth * 0.5f;
        Damage.damage(Team.sharded, x, y, range, damage);
    }

    public void reset() {
        markedUnits.clear();
        timer = 60f;
    }
}