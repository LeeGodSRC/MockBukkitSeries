package be.seeseemelk.mockbukkit.potion;

import org.bukkit.potion.PotionEffectType;

import static org.bukkit.potion.PotionEffectType.registerPotionEffectType;

public class PotionsMock {

    public static void registerDefaultPotionEffectTypes() {
        for (PotionEffectType type : PotionEffectType.values())
        {
            // We probably already registered all Potion Effects
            // otherwise this would be null
            if (type != null)
            {
                // This is not perfect, but it works.
                return;
            }
        }

        register(1, "SPEED", false);
        register(2, "SLOW", false);
        register(3, "FAST_DIGGING", false);
        register(4, "SLOW_DIGGING", false);
        register(5, "INCREASE_DAMAGE", false);
        register(6, "HEAL", true);
        register(7, "HARM", true);
        register(8, "JUMP", false);
        register(9, "CONFUSION", false);
        register(10, "REGENERATION", false);
        register(11, "DAMAGE_RESISTANCE", false);
        register(12, "FIRE_RESISTANCE", false);
        register(13, "WATER_BREATHING", false);
        register(14, "INVISIBILITY", false);
        register(15, "BLINDNESS", false);
        register(16, "NIGHT_VISION", false);
        register(17, "HUNGER", false);
        register(18, "WEAKNESS", false);
        register(19, "POISON", false);
        register(20, "WITHER", false);
        register(21, "HEALTH_BOOST", false);
        register(22, "ABSORPTION", false);
        register(23, "SATURATION", true);
        PotionEffectType.stopAcceptingRegistrations();
    }

    private static void register(int id, String name, boolean instant) {
        if (PotionEffectType.getByName(name) == null) {
            registerPotionEffectType(new MockPotionEffectType(id, name, instant));
        }
    }

}
