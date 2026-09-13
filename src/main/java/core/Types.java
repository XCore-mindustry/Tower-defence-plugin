package core;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.type.UnitType;
import mindustry.world.Block;

public class Types {
    public static Seq<UnitType> tier1Units = Seq.with(
            UnitTypes.dagger,
            UnitTypes.nova,
            UnitTypes.crawler,
            UnitTypes.elude,
            UnitTypes.merui,
            UnitTypes.stell,
            UnitTypes.alpha
    );

    public static Seq<UnitType> tier2Units = Seq.with(
            UnitTypes.mace,
            UnitTypes.pulsar,
            UnitTypes.horizon,
            UnitTypes.atrax,
            UnitTypes.cleroi,
            UnitTypes.locus,
            UnitTypes.poly,
            UnitTypes.beta,
            UnitTypes.avert
    );

    public static Seq<UnitType> tier3Units = Seq.with(
            UnitTypes.fortress,
            UnitTypes.quasar,
            UnitTypes.spiroct,
            UnitTypes.anthicus,
            UnitTypes.precept,
            UnitTypes.mega,
            UnitTypes.zenith,
            UnitTypes.gamma,
            UnitTypes.obviate
    );

    public static Seq<UnitType> tier4Units = Seq.with(
            UnitTypes.scepter,
            UnitTypes.arkyid,
            UnitTypes.tecta,
            UnitTypes.vanquish,
            UnitTypes.quad,
            UnitTypes.antumbra,
            UnitTypes.quell,
            UnitTypes.vela

    );

    public static Seq<UnitType> tier5Units = Seq.with(
            UnitTypes.reign,
            UnitTypes.toxopid,
            UnitTypes.oct,
            UnitTypes.eclipse,
            UnitTypes.corvus,
            UnitTypes.collaris,
            UnitTypes.latum,
            UnitTypes.disrupt,
            UnitTypes.conquer
    );

    public static Seq<Block> cores = Seq.with(
            Blocks.coreShard,
            Blocks.coreFoundation,
            Blocks.coreNucleus,
            Blocks.coreBastion,
            Blocks.coreCitadel,
            Blocks.coreAcropolis
    );
}