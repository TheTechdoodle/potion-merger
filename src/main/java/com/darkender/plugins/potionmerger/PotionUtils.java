package com.darkender.plugins.potionmerger;

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class PotionUtils
{
    public static Set<PotionEffect> getEffectsFromBase(PotionData base)
    {
        Set<PotionEffect> effects = new HashSet<>();
        switch(base.getType())
        {
            case FIRE_RESISTANCE:
                effects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, base.isExtended() ? 9600 : 3600, 0));
                break;
            case INSTANT_DAMAGE:
                effects.add(new PotionEffect(PotionEffectType.HARM, 0, base.isUpgraded() ? 1 : 0));
                break;
            case INSTANT_HEAL:
                effects.add(new PotionEffect(PotionEffectType.HEAL, 0, base.isUpgraded() ? 1 : 0));
                break;
            case INVISIBILITY:
                effects.add(new PotionEffect(PotionEffectType.INVISIBILITY, base.isExtended() ? 9600 : 3600, 0));
                break;
            case JUMP:
                effects.add(new PotionEffect(PotionEffectType.JUMP,
                        base.isExtended() ? 9600 : base.isUpgraded() ? 1800 : 3600, base.isUpgraded() ? 1 : 0));
                break;
            case LUCK:
                effects.add(new PotionEffect(PotionEffectType.LUCK, 6000, 0));
                break;
            case NIGHT_VISION:
                effects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, base.isExtended() ? 9600 : 3600, 0));
                break;
            case POISON:
                effects.add(new PotionEffect(PotionEffectType.POISON,
                        base.isExtended() ? 1800 : base.isUpgraded() ? 420 : 900, base.isUpgraded() ? 1 : 0));
                break;
            case REGEN:
                effects.add(new PotionEffect(PotionEffectType.REGENERATION,
                        base.isExtended() ? 1800 : base.isUpgraded() ? 440 : 4050, base.isUpgraded() ? 1 : 0));
                break;
            case SLOW_FALLING:
                effects.add(new PotionEffect(PotionEffectType.SLOW_FALLING,
                        base.isExtended() ? 4800 : 1800, 0));
                break;
            case SLOWNESS:
                effects.add(new PotionEffect(PotionEffectType.SLOW,
                        base.isExtended() ? 4800 : base.isUpgraded() ? 400 : 1800, base.isUpgraded() ? 3 : 0));
                break;
            case SPEED:
                effects.add(new PotionEffect(PotionEffectType.SPEED,
                        base.isExtended() ? 9600 : base.isUpgraded() ? 1800 : 3600, base.isUpgraded() ? 1 : 0));
                break;
            case STRENGTH:
                effects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, // Who named these???
                        base.isExtended() ? 9600 : base.isUpgraded() ? 1800 : 3600, base.isUpgraded() ? 1 : 0));
                break;
            case TURTLE_MASTER:
                effects.add(new PotionEffect(PotionEffectType.SLOW,
                        base.isExtended() ? 800 : 400, base.isUpgraded() ? 5 : 3));
                effects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                        base.isExtended() ? 800 : 400, base.isUpgraded() ? 3 : 2));
                break;
            case WATER_BREATHING:
                effects.add(new PotionEffect(PotionEffectType.WATER_BREATHING,
                        base.isExtended() ? 9600 : 3600, 0));
                break;
            case WEAKNESS:
                effects.add(new PotionEffect(PotionEffectType.WEAKNESS,
                        base.isExtended() ? 4800 : 1800, 0));
        }
        return effects;
    }
}
