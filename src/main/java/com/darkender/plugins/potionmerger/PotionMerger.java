package com.darkender.plugins.potionmerger;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PotionMerger extends JavaPlugin implements Listener
{
    private static final int MIN = 1200;
    private static NamespacedKey mergedPotionKey;
    private Map<PotionEffectType, String> translatableNames;
    
    @Override
    public void onEnable()
    {
        mergedPotionKey = new NamespacedKey(this, "merged-potion");
        getServer().getPluginManager().registerEvents(this, this);
        
        // Now *this* is EPIC
        translatableNames = new HashMap<>();
        translatableNames.put(PotionEffectType.ABSORPTION, "effect.minecraft.absorption");
        translatableNames.put(PotionEffectType.BAD_OMEN, "effect.minecraft.bad_omen");
        translatableNames.put(PotionEffectType.BLINDNESS, "effect.minecraft.blindness");
        translatableNames.put(PotionEffectType.CONDUIT_POWER, "effect.minecraft.conduit_power");
        translatableNames.put(PotionEffectType.CONFUSION, "effect.minecraft.nausea");
        translatableNames.put(PotionEffectType.DAMAGE_RESISTANCE, "effect.minecraft.resistance");
        translatableNames.put(PotionEffectType.DOLPHINS_GRACE, "effect.minecraft.dolphins_grace");
        translatableNames.put(PotionEffectType.FAST_DIGGING, "effect.minecraft.haste");
        translatableNames.put(PotionEffectType.FIRE_RESISTANCE, "effect.minecraft.fire_resistance");
        translatableNames.put(PotionEffectType.GLOWING, "effect.minecraft.glowing");
        translatableNames.put(PotionEffectType.HARM, "effect.minecraft.instant_damage");
        translatableNames.put(PotionEffectType.HEAL, "effect.minecraft.instant_health");
        translatableNames.put(PotionEffectType.HEALTH_BOOST, "effect.minecraft.health_boost");
        translatableNames.put(PotionEffectType.HERO_OF_THE_VILLAGE, "effect.minecraft.hero_of_the_village");
        translatableNames.put(PotionEffectType.HUNGER, "effect.minecraft.hunger");
        translatableNames.put(PotionEffectType.INCREASE_DAMAGE, "effect.minecraft.strength");
        translatableNames.put(PotionEffectType.INVISIBILITY, "effect.minecraft.invisibility");
        translatableNames.put(PotionEffectType.JUMP, "effect.minecraft.jump_boost");
        translatableNames.put(PotionEffectType.LEVITATION, "effect.minecraft.levitation");
        translatableNames.put(PotionEffectType.LUCK, "effect.minecraft.luck");
        translatableNames.put(PotionEffectType.NIGHT_VISION, "effect.minecraft.night_vision");
        translatableNames.put(PotionEffectType.POISON, "effect.minecraft.poison");
        translatableNames.put(PotionEffectType.REGENERATION, "effect.minecraft.regeneration");
        translatableNames.put(PotionEffectType.SATURATION, "effect.minecraft.saturation");
        translatableNames.put(PotionEffectType.SLOW, "effect.minecraft.slowness");
        translatableNames.put(PotionEffectType.SLOW_DIGGING, "effect.minecraft.mining_fatigue");
        translatableNames.put(PotionEffectType.SLOW_FALLING, "effect.minecraft.slow_falling");
        translatableNames.put(PotionEffectType.SPEED, "effect.minecraft.speed");
        translatableNames.put(PotionEffectType.UNLUCK, "effect.minecraft.unluck");
        translatableNames.put(PotionEffectType.WATER_BREATHING, "effect.minecraft.water_breathing");
        translatableNames.put(PotionEffectType.WEAKNESS, "effect.minecraft.weakness");
        translatableNames.put(PotionEffectType.WITHER, "effect.minecraft.wither");

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            @Override
            public void run()
            {
                checkPotions();
            }
        }, 0L, 5L);
    }
    
    @EventHandler
    public void onHandItem(PlayerItemHeldEvent event)
    {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if(item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(mergedPotionKey, PersistentDataType.BYTE))
        {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            TextComponent base = new TextComponent("");
            for(int i = 0; i < potionMeta.getCustomEffects().size(); i++)
            {
                PotionEffect effect = potionMeta.getCustomEffects().get(i);
                if(translatableNames.containsKey(effect.getType()))
                {
                    base.addExtra(new TranslatableComponent(translatableNames.get(effect.getType())));
                }
                else
                {
                    base.addExtra(new TextComponent(effect.getType().getName().toLowerCase().replaceAll("_", " ")));
                }
                
                if(i != potionMeta.getCustomEffects().size() - 1)
                {
                    base.addExtra(new TextComponent(", "));
                }
            }
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, base);
        }
    }
    
    private void checkPotions()
    {
        for(World world : Bukkit.getWorlds())
        {
            Map<Block, Map<Material, Set<Item>>> inCauldrons = new HashMap<>();
            for(Item item : world.getEntitiesByClass(Item.class))
            {
                Material itemMaterial = item.getItemStack().getType();
                if(!hasPotionEffects(itemMaterial))
                {
                    continue;
                }
                Block cauldron = item.getLocation().getBlock();
                if(cauldron.getType() != Material.CAULDRON)
                {
                    continue;
                }
                Levelled levelled = (Levelled) cauldron.getBlockData();
                if(levelled.getLevel() != levelled.getMaximumLevel())
                {
                    continue;
                }
            
                if(!inCauldrons.containsKey(cauldron))
                {
                    inCauldrons.put(cauldron, new HashMap<>());
                }
                Map<Material, Set<Item>> items = inCauldrons.get(cauldron);
                if(!items.containsKey(itemMaterial))
                {
                    items.put(itemMaterial, new HashSet<>());
                }
                items.get(itemMaterial).add(item);
            }
        
            for(Map.Entry<Block, Map<Material, Set<Item>>> cauldron : inCauldrons.entrySet())
            {
                for(Map.Entry<Material, Set<Item>> items : cauldron.getValue().entrySet())
                {
                    if(items.getValue().size() >= 2)
                    {
                        Map<PotionEffectType, PotionEffect> merged = new HashMap<>();
                        for(Item item : items.getValue())
                        {
                            PotionMeta potionMeta = (PotionMeta) item.getItemStack().getItemMeta();
                            boolean equal = true;
                            if(potionMeta.getBasePotionData().getType() != PotionType.UNCRAFTABLE)
                            {
                                PotionEffect base = getPotionEffect(potionMeta);
                                if(base != null)
                                {
                                    potionMeta.addCustomEffect(base, false);
                                }
                            }
                            for(PotionEffect effect : potionMeta.getCustomEffects())
                            {
                                if(merged.containsKey(effect.getType()))
                                {
                                    PotionEffect mergedEffect = merged.get(effect.getType());
                                    if(effect.getDuration() <= mergedEffect.getDuration() &&
                                            effect.getAmplifier() <= mergedEffect.getAmplifier())
                                    {
                                        continue;
                                    }
                                    merged.put(effect.getType(), new PotionEffect(effect.getType(),
                                            Math.max(effect.getDuration(), mergedEffect.getDuration()),
                                            Math.max(effect.getAmplifier(), mergedEffect.getAmplifier()),
                                            mergedEffect.isAmbient(),
                                            mergedEffect.hasParticles(),
                                            mergedEffect.hasIcon()));
                                    equal = false;
                                }
                                else
                                {
                                    merged.put(effect.getType(), effect);
                                    equal = false;
                                }
                            }
                            if(!equal)
                            {
                                item.remove();
                            }
                        }
                    
                        ItemStack mergedItem = new ItemStack(items.getKey(), 1);
                        PotionMeta potionMeta = (PotionMeta) mergedItem.getItemMeta();
                        for(Map.Entry<PotionEffectType, PotionEffect> mergedEntry : merged.entrySet())
                        {
                            potionMeta.addCustomEffect(mergedEntry.getValue(), true);
                        }
                        potionMeta.setDisplayName(ChatColor.AQUA + "Merged Potion");
                        potionMeta.getPersistentDataContainer().set(mergedPotionKey, PersistentDataType.BYTE, (byte) 1);
                        mergedItem.setItemMeta(potionMeta);
                        Block cauldronBlock = cauldron.getKey();
                        Location loc = cauldronBlock.getLocation().add(0.5, 0.5, 0.5);
                        cauldronBlock.getWorld().dropItem(loc, mergedItem);
                        cauldronBlock.getWorld().playSound(loc, Sound.BLOCK_BREWING_STAND_BREW, 1.0F, 1.0F);
                        cauldronBlock.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 1);
                    }
                }
            }
        }
    }
    
    private boolean hasPotionEffects(Material material)
    {
        return material == Material.SPLASH_POTION || material == Material.POTION ||
               material == Material.LINGERING_POTION || material == Material.TIPPED_ARROW;
    }
    
    // From https://www.spigotmc.org/threads/how-to-get-the-exact-potion-effect-a-potion-item-will-give-off-in-the-playerconsumeitemevent.410806/#post-3645238
    private PotionEffect getPotionEffect(PotionMeta pm)
    {
        PotionType pt = pm.getBasePotionData().getType();
        PotionEffectType pet = pt.getEffectType();
        boolean extended = pm.getBasePotionData().isExtended();
        boolean upgraded = pm.getBasePotionData().isUpgraded();
        boolean irregular = this.isIrregular(pet);
        boolean negative = this.isNegative(pet);
        
        
        if(!extended && !upgraded && !irregular)
        {
            return negative ? new PotionEffect(pet, (int) (MIN * 1.5), 0) : new PotionEffect(pet, MIN * 3, 0);
        }
        else if(!extended && upgraded && !irregular)
        {
            return negative ? new PotionEffect(pet, 400, 3) : new PotionEffect(pet, (int) (MIN * 1.5D), 1); // hard code slowness 4 in because its the only negative semi-irregular potion effect
        }
        else if(extended && !upgraded && !irregular)
        {
            return negative ? new PotionEffect(pet, MIN * 4, 0) : new PotionEffect(pet, MIN * 8, 0);
        }
        else if(pt.equals(PotionType.REGEN) || pt.equals(PotionType.POISON))
        {
            return extended ? new PotionEffect(pet, (int) (MIN * 1.5), 0) : upgraded ? negative ? new PotionEffect(pet, (int) (21.6 * 20), 1) : new PotionEffect(pet, 22 * 20, 1) : new PotionEffect(pet, 45 * 20, 0);
        }
        else if(pt.equals(PotionType.INSTANT_DAMAGE) || pt.equals(PotionType.INSTANT_HEAL))
        {
            return upgraded ? new PotionEffect(pet, 1, 1) : new PotionEffect(pet, 1, 0);
        }
        else if(pt.equals(PotionType.LUCK))
        {
            return new PotionEffect(pet, 5 * MIN, 0);
        }
        else if(pt.equals(PotionType.TURTLE_MASTER))
        {
            return null; // make sure in your method you do something about this. Since turtle master gives two potion effects, you have to handle this outside of this method.
            // nah
        }
        
        return new PotionEffect(pet, MIN, 0);
    }
    
    
    private boolean isNegative(PotionEffectType pet)
    {
        for(PotionType type : this.getNegativePotions())
        {
            if(type.getEffectType().equals(pet)) return true;
        }
        return false;
    }
    
    
    private PotionType[] getNegativePotions()
    {
        PotionType[] list = {PotionType.INSTANT_DAMAGE, PotionType.POISON, PotionType.SLOWNESS, PotionType.WEAKNESS, PotionType.SLOW_FALLING}; // Slow falling is not a negative put has stats effects simular to a negative potion.
        return list;
    }
    
    
    private boolean isIrregular(PotionEffectType pet)
    {
        
        for(PotionType type : this.getIrregularPotions())
        {
            if(type.getEffectType().equals(pet)) return true;
        }
        
        return false;
    }
    
    private PotionType[] getIrregularPotions()
    {
        PotionType[] list = {PotionType.REGEN, PotionType.LUCK, PotionType.POISON, PotionType.TURTLE_MASTER, PotionType.INSTANT_DAMAGE, PotionType.INSTANT_HEAL};
        return list;
    }
    
    private boolean isUnusable(PotionType type)
    {
        for(PotionType pt : this.getUnusable())
        {
            if(pt.equals(type)) return true;
        }
        return false;
    }
    
    private PotionType[] getUnusable()
    {
        PotionType[] list = {PotionType.AWKWARD, PotionType.WATER, PotionType.THICK, PotionType.MUNDANE, PotionType.UNCRAFTABLE};
        return list;
    }
}
