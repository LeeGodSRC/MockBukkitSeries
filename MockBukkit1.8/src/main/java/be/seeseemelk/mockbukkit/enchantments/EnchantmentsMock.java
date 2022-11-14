package be.seeseemelk.mockbukkit.enchantments;

import org.bukkit.enchantments.Enchantment;

public final class EnchantmentsMock
{
	private EnchantmentsMock() {}

	public static void registerDefaultEnchantments()
	{
		register(0, "PROTECTION_ENVIRONMENTAL");
		register(1, "PROTECTION_FIRE");
		register(2, "PROTECTION_FALL");
		register(3, "PROTECTION_EXPLOSIONS");
		register(4, "PROTECTION_PROJECTILE");
		register(5, "OXYGEN");
		register(6, "WATER_WORKER");
		register(7, "THORNS");
		register(8, "DEPTH_STRIDER");
		register(16, "DAMAGE_ALL");
		register(17, "DAMAGE_UNDEAD");
		register(18, "DAMAGE_ARTHROPODS");
		register(19, "KNOCKBACK");
		register(20, "FIRE_ASPECT");
		register(21, "LOOT_BONUS_MOBS");
		register(32, "DIG_SPEED");
		register(33, "SILK_TOUCH");
		register(34, "DURABILITY");
		register(35, "LOOT_BONUS_BLOCKS");
		register(48, "ARROW_DAMAGE");
		register(49, "ARROW_KNOCKBACK");
		register(50, "ARROW_FIRE");
		register(51, "ARROW_INFINITE");
		register(61, "LUCK");
		register(62, "LURE");
	}

	private static void register(int id, String name)
	{
		if (Enchantment.getById(id) != null)
			return;

		Enchantment.registerEnchantment(new EnchantmentMock(id, name));
	}
}
