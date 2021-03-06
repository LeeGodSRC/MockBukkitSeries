package be.seeseemelk.mockbukkit.inventory.meta;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.enchantments.Enchantment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

class EnchantedBookMetaMockTest
{

	private Enchantment testEnchantment;
	private Enchantment testEnchantment2;

	@Before
	public void setUp()
	{
		MockBukkit.mock();
		testEnchantment = new EnchantmentMock("Test Enchantment");
		testEnchantment2 = new EnchantmentMock("Second Test Enchantment");
	}

	@After
	public void tearDown()
	{
		MockBukkit.unload();
	}

	@Test
	public void testStoredEnchantsDefaultFalse()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();
		assertFalse(meta.hasStoredEnchants());
		assertFalse(meta.hasStoredEnchant(testEnchantment));
	}

	@Test
	public void testStoredEnchantsWithEnchantment()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();
		assertTrue(meta.addStoredEnchant(testEnchantment, 1, false));
		assertTrue(meta.hasStoredEnchants());
		assertTrue(meta.hasStoredEnchant(testEnchantment));
	}

	@Test
	public void testEnchantmentLevelWithEnchantment()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();
		assertTrue(meta.addStoredEnchant(testEnchantment, 3, false));
		assertEquals(3, meta.getStoredEnchantLevel(testEnchantment));
	}

	@Test
	public void testAddEnchantmentUnsafely()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();

		assertTrue(meta.addStoredEnchant(testEnchantment, 100, true));
		assertEquals(100, meta.getStoredEnchantLevel(testEnchantment));

		assertTrue(meta.addStoredEnchant(testEnchantment2, -50, true));
		assertEquals(-50, meta.getStoredEnchantLevel(testEnchantment2));
	}

	@Test
	public void testAlreadyExistingEnchantment()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();

		assertTrue(meta.addStoredEnchant(testEnchantment, 1, false));

		// Adding the same level should fail
		assertFalse(meta.addStoredEnchant(testEnchantment, 1, false));

		// Adding a different level should work
		assertTrue(meta.addStoredEnchant(testEnchantment, 2, false));
	}

	@Test
	public void testAddEnchantmentButFail()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();

		assertFalse(meta.addStoredEnchant(testEnchantment, 100, false));
		assertFalse(meta.hasStoredEnchant(testEnchantment));

		assertFalse(meta.addStoredEnchant(testEnchantment, -100, false));
		assertFalse(meta.hasStoredEnchant(testEnchantment));
	}

	@Test
	public void testAddConflictingEnchantments()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();

		meta.addStoredEnchant(testEnchantment, 1, false);
		assertTrue(meta.hasConflictingStoredEnchant(testEnchantment));
		assertFalse(meta.hasConflictingStoredEnchant(testEnchantment2));
	}

	@Test
	public void testRemoveStoredEnchantment()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();

		assertTrue(meta.addStoredEnchant(testEnchantment, 1, false));
		assertTrue(meta.removeStoredEnchant(testEnchantment));

		assertFalse(meta.hasStoredEnchant(testEnchantment));
		assertFalse(meta.hasStoredEnchants());

		// Removing it again should return false
		assertFalse(meta.removeStoredEnchant(testEnchantment));
	}

	@Test
	public void testGetStoredEnchantments()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(testEnchantment, 1);

		assertTrue(meta.addStoredEnchant(testEnchantment, 1, false));

		Map<Enchantment, Integer> storedEnchantments = meta.getStoredEnchants();
		assertEquals(enchantments, storedEnchantments);
	}

	@Test
	public void testEquals()
	{
		EnchantedBookMetaMock meta = new EnchantedBookMetaMock();
		assertEquals(meta, meta);
		assertNotEquals(meta, new ItemMetaMock());

		EnchantedBookMetaMock meta2 = new EnchantedBookMetaMock();
		assertEquals(meta, meta2);

		meta.addStoredEnchant(testEnchantment, 1, false);
		assertNotEquals(meta, meta2);

		meta2.addStoredEnchant(testEnchantment, 1, false);
		assertEquals(meta, meta2);
	}

}
