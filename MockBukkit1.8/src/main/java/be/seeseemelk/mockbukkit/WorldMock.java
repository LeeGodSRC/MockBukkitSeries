package be.seeseemelk.mockbukkit;

import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A mock world object. Note that it is made to be as simple as possible. It is
 * by no means an efficient implementation.
 */
@SuppressWarnings("deprecation")
public class WorldMock implements World
{
	private Map<Coordinate, BlockMock> blocks = new HashMap<>();
	private final Map<String, String> gameRules = new HashMap<>();
	private Material defaultBlock;
	private int height;
	private int grassHeight;
	private String name = "World";
	private UUID uuid = UUID.randomUUID();
	private Location spawnLocation;

	private long fullTime = 0;
	private int weatherDuration = 0;
	private int thunderDuration = 0;
	private boolean storming = false;
	private boolean autoSave = true;

	private Difficulty difficulty = Difficulty.NORMAL;

	/**
	 * Creates a new mock world.
	 * 
	 * @param defaultBlock The block that is spawned at locations 1 to
	 *            {@code grassHeight}
	 * @param height The height of the world.
	 * @param grassHeight The last {@code y} at which {@code defaultBlock} will
	 *            spawn.
	 */
	public WorldMock(Material defaultBlock, int height, int grassHeight)
	{
		this.defaultBlock = defaultBlock;
		this.height = height;
		this.grassHeight = grassHeight;
		this.initializeGameRules();
	}
	
	/**
	 * Creates a new mock world with a height of 128.
	 * 
	 * @param defaultBlock The block that is spawned at locations 1 to
	 *            {@code grassHeight}
	 * @param grassHeight The last {@code y} at which {@code defaultBlock} will
	 *            spawn.
	 */
	public WorldMock(Material defaultBlock, int grassHeight)
	{
		this(defaultBlock, 128, grassHeight);
	}
	
	/**
	 * Creates a new mock world with a height of 128 and will spawn grass until a
	 * {@code y} of 4.
	 */
	public WorldMock()
	{
		this(Material.GRASS, 4);
	}

	private void initializeGameRules() {
		this.setGameRuleValueInternal("doFireTick", "true");
		this.setGameRuleValueInternal("mobGriefing", "true");
		this.setGameRuleValueInternal("keepInventory", "false");
		this.setGameRuleValueInternal("doMobSpawning", "true");
		this.setGameRuleValueInternal("doMobLoot", "true");
		this.setGameRuleValueInternal("doTileDrops", "true");
		this.setGameRuleValueInternal("doEntityDrops", "true");
		this.setGameRuleValueInternal("commandBlockOutput", "true");
		this.setGameRuleValueInternal("naturalRegeneration", "true");
		this.setGameRuleValueInternal("doDaylightCycle", "true");
		this.setGameRuleValueInternal("doWeatherCycle", "true");
		this.setGameRuleValueInternal("logAdminCommands", "true");
		this.setGameRuleValueInternal("showDeathMessages", "true");
		this.setGameRuleValueInternal("randomTickSpeed", "3");
		this.setGameRuleValueInternal("sendCommandFeedback", "true");
		this.setGameRuleValueInternal("reducedDebugInfo", "false");
	}

	private void setGameRuleValueInternal(String key, String value) {
		this.gameRules.put(key, value);
	}
	
	/**
	 * Makes sure that a certain block exists on the coordinate. Returns that block.
	 * 
	 * @param c Creates a block on the given coordinate.
	 * @return A newly created block at that location.
	 */
	public BlockMock createBlock(Coordinate c)
	{
		if (c.y >= height)
			throw new ArrayIndexOutOfBoundsException("Y larger than height");
		else if (c.y < 0)
			throw new ArrayIndexOutOfBoundsException("Y smaller than 0");
		
		Location location = new Location(this, c.x, c.y, c.z);
		BlockMock block;
		if (c.y == 0)
			block = new BlockMock(Material.BEDROCK, location);
		else if (c.y <= grassHeight)
			block = new BlockMock(defaultBlock, location);
		else
			block = new BlockMock(location);
		
		blocks.put(c, block);
		return block;
	}
	
	@Override
	public BlockMock getBlockAt(int x, int y, int z)
	{
		Coordinate coordinate = new Coordinate(x, y, z);
		if (blocks.containsKey(coordinate))
		{
			return blocks.get(coordinate);
		}
		else
		{
			return createBlock(coordinate);
		}
	}
	
	@Override
	public BlockMock getBlockAt(Location location)
	{
		return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Give a new name to this world.
	 * 
	 * @param name The new name of this world.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public UUID getUID()
	{
		return uuid;
	}
	
	@Override
	public Location getSpawnLocation()
	{
		if (spawnLocation == null)
		{
			setSpawnLocation(0, grassHeight + 1, 0);
		}
		return spawnLocation;
	}
	
	@Override
	public boolean setSpawnLocation(int x, int y, int z)
	{
		if (spawnLocation == null)
		{
			spawnLocation = new Location(this, x, y, z);
		}
		else
		{
			spawnLocation.setX(x);
			spawnLocation.setY(y);
			spawnLocation.setZ(z);
		}
		return true;
	}
	
	@Override
	public List<Entity> getEntities()
	{
		// MockBukkit.assertMocking();
		List<Entity> entities = new ArrayList<>();
		
		Collection<? extends PlayerMock> players = MockBukkit.getMock().getOnlinePlayers();
		players.stream().filter(player -> player.getWorld() == this).collect(Collectors.toCollection(() -> entities));
		
		return entities;
	}
	
	@Override
	public ChunkMock getChunkAt(int x, int z)
	{
		ChunkMock chunk = new ChunkMock(this, x, z);
		return chunk;
	}
	
	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Set<String> getListeningPluginChannels()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public List<MetadataValue> getMetadata(String metadataKey)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean hasMetadata(String metadataKey)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	@Deprecated
	public int getBlockTypeIdAt(int x, int y, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	@Deprecated
	public int getBlockTypeIdAt(Location location)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public int getHighestBlockYAt(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public int getHighestBlockYAt(Location location)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Block getHighestBlockAt(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Block getHighestBlockAt(Location location)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Chunk getChunkAt(Location location)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Chunk getChunkAt(Block block)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean isChunkLoaded(Chunk chunk)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Chunk[] getLoadedChunks()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void loadChunk(Chunk chunk)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean isChunkLoaded(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean isChunkInUse(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void loadChunk(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean loadChunk(int x, int z, boolean generate)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean unloadChunk(Chunk chunk)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean unloadChunk(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean unloadChunk(int x, int z, boolean save)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	@Deprecated
	public boolean unloadChunk(int x, int z, boolean save, boolean safe)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean unloadChunkRequest(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean unloadChunkRequest(int x, int z, boolean safe)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean regenerateChunk(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	@Deprecated
	public boolean refreshChunk(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Item dropItem(Location location, ItemStack item)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Item dropItemNaturally(Location location, ItemStack item)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Arrow spawnArrow(Location location, Vector direction, float speed, float spread)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean generateTree(Location location, TreeType type)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Entity spawnEntity(Location loc, EntityType type)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public LightningStrike strikeLightning(Location loc)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public LightningStrike strikeLightningEffect(Location loc)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public List<LivingEntity> getLivingEntities()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> cls)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Collection<Entity> getEntitiesByClasses(Class<?>... classes)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public List<Player> getPlayers()
	{
		// TODO Auto-generated method stub
		return Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld() == this).collect(Collectors.toList());
	}
	
	@Override
	public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public long getTime()
	{
		return this.getFullTime() % 24000L;
	}

	@Override
	public void setTime(long time)
	{
		long base = this.getFullTime() - this.getFullTime() % 24000L;
		this.setFullTime(base + time % 24000L);
	}

	@Override
	public long getFullTime()
	{
		return this.fullTime;
	}

	@Override
	public void setFullTime(long time)
	{
		this.fullTime = time;
	}

	@Override
	public boolean hasStorm()
	{
		return storming;
	}

	@Override
	public void setStorm(boolean hasStorm)
	{
		storming = hasStorm;
	}

	@Override
	public int getWeatherDuration()
	{
		return weatherDuration;
	}

	@Override
	public void setWeatherDuration(int duration)
	{
		weatherDuration = duration;
	}

	@Override
	public boolean isThundering()
	{
		return thunderDuration > 0;
	}

	@Override
	public void setThundering(boolean thundering)
	{
		thunderDuration = thundering ? 600 : 0;
	}

	@Override
	public int getThunderDuration()
	{
		return thunderDuration;
	}

	@Override
	public void setThunderDuration(int duration)
	{
		thunderDuration = duration;
	}
	
	@Override
	public boolean createExplosion(double x, double y, double z, float power)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean createExplosion(double x, double y, double z, float power, boolean setFire)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean createExplosion(Location loc, float power)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean createExplosion(Location loc, float power, boolean setFire)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Environment getEnvironment()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public long getSeed()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean getPVP()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setPVP(boolean pvp)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public ChunkGenerator getGenerator()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void save()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public List<BlockPopulator> getPopulators()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	@Deprecated
	public FallingBlock spawnFallingBlock(Location location, Material material, byte data)
			throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	@Deprecated
	public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData)
			throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void playEffect(Location location, Effect effect, int data)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void playEffect(Location location, Effect effect, int data, int radius)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public <T> void playEffect(Location location, Effect effect, T data)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public <T> void playEffect(Location location, Effect effect, T data, int radius)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean getAllowAnimals()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean getAllowMonsters()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public Biome getBiome(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setBiome(int x, int z, Biome bio)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public double getTemperature(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public double getHumidity(int x, int z)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public int getMaxHeight()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public int getSeaLevel()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean getKeepSpawnInMemory()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setKeepSpawnInMemory(boolean keepLoaded)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean isAutoSave()
	{
		return this.autoSave;
	}
	
	@Override
	public void setAutoSave(boolean value)
	{
		this.autoSave = value;
	}
	
	@Override
	public void setDifficulty(Difficulty difficulty)
	{
		this.difficulty = difficulty;
	}
	
	@Override
	public Difficulty getDifficulty()
	{
		return this.difficulty;
	}
	
	@Override
	public File getWorldFolder()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public WorldType getWorldType()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public boolean canGenerateStructures()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public long getTicksPerAnimalSpawns()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public long getTicksPerMonsterSpawns()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public int getMonsterSpawnLimit()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setMonsterSpawnLimit(int limit)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public int getAnimalSpawnLimit()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setAnimalSpawnLimit(int limit)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public int getWaterAnimalSpawnLimit()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setWaterAnimalSpawnLimit(int limit)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public int getAmbientSpawnLimit()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void setAmbientSpawnLimit(int limit)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public void playSound(Location location, Sound sound, float volume, float pitch)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public String[] getGameRules()
	{
		return this.gameRules.keySet().toArray(new String[0]);
	}
	
	@Override
	public String getGameRuleValue(String rule)
	{
		return this.gameRules.get(rule);
	}
	
	@Override
	public boolean setGameRuleValue(String rule, String value)
	{
		if (rule == null || value == null) return false;
		if (!isGameRule(rule)) return false;

		this.gameRules.put(rule, value);
		return true;
	}
	
	@Override
	public boolean isGameRule(String rule)
	{
		return this.gameRules.containsKey(rule);
	}
	
	@Override
	public Spigot spigot()
	{
		throw new UnimplementedOperationException();
	}
	
	@Override
	public WorldBorder getWorldBorder()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public LivingEntity spawnCreature(Location loc, EntityType type)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
	@Override
	public LivingEntity spawnCreature(Location loc, CreatureType type)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}
	
}
