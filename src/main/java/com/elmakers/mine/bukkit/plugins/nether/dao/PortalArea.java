package com.elmakers.mine.bukkit.plugins.nether.dao;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import com.elmakers.mine.bukkit.persisted.PersistClass;
import com.elmakers.mine.bukkit.persisted.PersistField;
import com.elmakers.mine.bukkit.persisted.Persisted;
import com.elmakers.mine.bukkit.persistence.dao.BoundingBox;
import com.elmakers.mine.bukkit.persistence.dao.MaterialList;

@PersistClass(schema = "nether", name = "area")
public class PortalArea extends Persisted
{
    public static int           aboveGroundPadding   = 16;
    public static int           bedrockPadding       = 1;
    public static int           ceilingMaxVariance   = 16;

    public static int           ceilingPadding       = 4;
    public static int           ceilingPercentChange = 25;
    public static int           defaultFloor         = 4;
    public static int           defaultRatio         = 16;
    public static int           defaultSize          = 16;
    public static MaterialList  destructable         = null;
    public static int           emptyBuffer          = 8;
    // heightmap config
    public static int           floorMaxVariance     = 8;
    public static int           floorPadding         = 4;
    public static int           floorPercentChange   = 5;
    public static int           lavaPadding          = 1;

    public static int           lightstoneHeight     = 3;
    public static int           maxHeight            = 64;
    public static int           maxSearch            = 32;
    public static int           minHeight            = 32;
    public static int           poolPadding          = 4;

    public static int           poolSize             = 4;

    private static final Random random               = new Random();

    public static int getCeilingPadding()
    {
        return ceilingPadding + bedrockPadding + lightstoneHeight + aboveGroundPadding;
    }

    public static int getFloorPadding()
    {
        return floorPadding + bedrockPadding + poolPadding;
    }

    protected NetherPlayer creator;

    protected List<Portal> externalPortals;

    protected int          id;

    protected BoundingBox  internalArea;

    protected List<Portal> internalPortals;

    protected String       name;

    protected double       scaleRatio;

    protected BlockVector  targetCenter;

    protected NetherWorld  targetWorld;

    protected NetherWorld  world;

    public PortalArea()
    {
        if (destructable == null)
        {
            destructable = new MaterialList();
            destructable.add(Material.STONE);
            destructable.add(Material.GRASS);
            destructable.add(Material.DIRT);
            destructable.add(Material.COBBLESTONE);
            destructable.add(Material.SAND);
            destructable.add(Material.STONE);
            destructable.add(Material.GRAVEL);
            destructable.add(Material.WATER);
            destructable.add(Material.STATIONARY_WATER);
            destructable.add(Material.COAL_ORE);
            destructable.add(Material.DIAMOND_ORE);
            destructable.add(Material.LAPIS_ORE);
            destructable.add(Material.REDSTONE_ORE);
            destructable.add(Material.GOLD_ORE);
            destructable.add(Material.NETHERRACK);
            destructable.add(Material.GLOWSTONE);
            destructable.add(Material.IRON_ORE);
        }
    }

    public void create(World world)
    {
        // TODO : Bring this back? Maybe in Spells?
        /*
         * // Create bedrock box BlockFace[] box = {BlockFace.NORTH,
         * BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN,
         * BlockFace.UP}; for (BlockFace face : box) { BoundingBox faceArea =
         * internalArea.getFace(face, bedrockPadding, 1); faceArea.fill(world,
         * Material.BEDROCK, destructable); }
         * 
         * // Create lava walls BlockFace[] walls = {BlockFace.NORTH,
         * BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}; for (BlockFace face
         * : walls) { BoundingBox faceArea = internalArea.getFace(face,
         * lavaPadding, 1 - bedrockPadding); faceArea.fill(world,
         * Material.STATIONARY_LAVA, destructable); }
         * 
         * // Create netherrack ceiling BoundingBox ceiling =
         * internalArea.getFace(BlockFace.UP, ceilingPadding, 1 - bedrockPadding
         * - ceilingPadding); ceiling.fill(world, Material.NETHERRACK);
         * 
         * // Create netherrack floor BoundingBox floor =
         * internalArea.getFace(BlockFace.DOWN, floorPadding, 1 - bedrockPadding
         * - floorPadding); floor.fill(world, Material.NETHERRACK);
         * 
         * // Create heightmaps ceiling = internalArea.getFace(BlockFace.UP,
         * ceilingMaxVariance, 1 - bedrockPadding - ceilingPadding -
         * ceilingMaxVariance); floor = internalArea.getFace(BlockFace.DOWN,
         * floorMaxVariance, 1 - bedrockPadding - floorPadding -
         * floorMaxVariance);
         * 
         * // Leave room for lava ceiling.getMax().setX(ceiling.getMax().getX()
         * - 1); ceiling.getMax().setZ(ceiling.getMax().getZ() - 1);
         * ceiling.getMin().setX(ceiling.getMin().getX() - 1);
         * ceiling.getMin().setZ(ceiling.getMin().getZ() - 1);
         * 
         * floor.getMax().setX(ceiling.getMax().getX() - 1);
         * floor.getMax().setZ(ceiling.getMax().getZ() - 1);
         * floor.getMin().setX(ceiling.getMin().getX() - 1);
         * floor.getMin().setZ(ceiling.getMin().getZ() - 1);
         * 
         * 
         * byte[][] ceilingMap = generateHeightMap(ceiling,
         * ceilingPercentChange); byte[][] floorMap = generateHeightMap(floor,
         * floorPercentChange);
         * 
         * int xOffset = floor.getMin().getBlockX(); int yOffset =
         * floor.getMin().getBlockY(); int zOffset = floor.getMin().getBlockZ();
         * 
         * // Fill interior int xSize = ceiling.getSizeX(); int zSize =
         * ceiling.getSizeZ(); int ySize = internalArea.getSizeY(); for (int
         * mapX = 0; mapX < xSize; mapX++) { for (int mapZ = 0; mapZ < zSize;
         * mapZ++) { for (int dY = ySize; dY >=0; dY--) { Block block =
         * world.getBlockAt(xOffset + mapX, yOffset + dY, zOffset + mapZ); if
         * (destructable.contains(block.getType())) continue;
         * 
         * // Create lava pools if (block.getType() == Material.LAVA ||
         * block.getType() == Material.STATIONARY_LAVA) { createPool(floorMap,
         * mapX, mapZ); }
         * 
         * if (dY < floorMaxVariance && dY < floorMap[mapX][mapZ]) {
         * block.setType(Material.NETHERRACK); continue; }
         * 
         * if (dY > ySize - ceilingMaxVariance && dY > ySize -
         * ceilingMap[mapX][mapZ]) { if (ySize - dY > ceilingMaxVariance -
         * lightstoneHeight) { block.setType(Material.GLOWSTONE); } else {
         * block.setType(Material.NETHERRACK); } continue; }
         * 
         * block.setType(Material.AIR); } } }
         */
    }

    protected void createPool(byte[][] map, int mapX, int mapZ)
    {
        // Only really need to go forward, since the old parts of the heightmap
        // don't matter.
        int maxHeight = poolSize * poolSize;
        float ratio = (float) poolPadding / maxHeight;
        for (int x = 0; x < poolSize; x++)
        {
            for (int z = 0; z < poolSize; z++)
            {
                map[x][z] = (byte) (ratio * x * z);
            }
        }
    }

    protected byte[][] generateHeightMap(BoundingBox area, int percentChange)
    {
        int xSize = area.getSizeX();
        int ySize = area.getSizeY();
        int zSize = area.getSizeZ();
        byte[][] heightMap = new byte[xSize][zSize];

        // Start out somewhere random:
        heightMap[0][0] = (byte) random.nextInt(ySize);
        for (int x = 0; x < xSize; x++)
        {
            for (int z = 0; z < zSize; z++)
            {
                byte current = heightMap[x][z];

                if (x > 0 && z > 0 && x < xSize - 2)
                {
                    current = (byte) ((current + heightMap[x - 1][z] + heightMap[x][z - 1] + heightMap[x + 1][z]) / 4);
                }
                else if (x > 0 && z > 0)
                {
                    current = (byte) ((current + heightMap[x - 1][z] + heightMap[x][z - 1]) / 3);
                }
                else if (x > 0)
                {
                    current = (byte) ((current + heightMap[x - 1][z]) / 2);
                }
                else if (z > 0)
                {
                    current = (byte) ((current + heightMap[x][z - 1]) / 2);
                }

                int percent = random.nextInt(100);
                if (percentChange > percent)
                {
                    if (current >= ySize)
                    {
                        current--;
                    }
                    else if (current == 0)
                    {
                        current++;
                    }
                    else if (percent % 2 == 0)
                    {
                        current++;
                    }
                    else
                    {
                        current--;
                    }
                }

                if (x < xSize - 2)
                {
                    heightMap[x + 1][z] = current;
                }
                if (z < zSize - 2)
                {
                    heightMap[x][z + 1] = current;
                }
            }
        }

        return heightMap;
    }

    @PersistField
    public NetherPlayer getCreator()
    {
        return creator;
    }

    public BoundingBox getExternalArea()
    {
        // TODO - use BoundingBox logic
        BlockVector location = internalArea.getCenter();
        int sizeX = internalArea.getSizeX();
        int sizeZ = internalArea.getSizeZ();
        int minY = 0;
        int maxY = 128;
        int minX = location.getBlockX() - (int) (sizeX * scaleRatio / 2);
        int maxX = location.getBlockX() + (int) (sizeX * scaleRatio / 2);
        int minZ = location.getBlockZ() - (int) (sizeZ * scaleRatio / 2);
        int maxZ = location.getBlockZ() + (int) (sizeZ * scaleRatio / 2);

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @PersistField
    public List<Portal> getExternalPortals()
    {
        return externalPortals;
    }

    @PersistField(id = true, auto = true)
    public int getId()
    {
        return id;
    }

    @PersistField(contained = true, name = "internal")
    public BoundingBox getInternalArea()
    {
        return internalArea;
    }

    @PersistField
    public List<Portal> getInternalPortals()
    {
        return internalPortals;
    }

    @PersistField
    public String getName()
    {
        return name;
    }

    @PersistField
    public double getRatio()
    {
        return scaleRatio;
    }

    @PersistField(contained = true)
    public BlockVector getTargetCenter()
    {
        return targetCenter;
    }

    @PersistField
    public NetherWorld getTargetWorld()
    {
        return targetWorld;
    }

    @PersistField
    public NetherWorld getWorld()
    {
        return world;
    }

    public void setCreator(NetherPlayer creator)
    {
        this.creator = creator;
    }

    public void setExternalPortals(List<Portal> portals)
    {
        this.externalPortals = portals;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setInternalArea(BoundingBox internalArea)
    {
        this.internalArea = internalArea;
    }

    public void setInternalPortals(List<Portal> portals)
    {
        this.internalPortals = portals;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setRatio(double ratio)
    {
        this.scaleRatio = ratio;
    }

    public void setTargetCenter(BlockVector targetCenter)
    {
        this.targetCenter = targetCenter;
    }

    public void setTargetWorld(NetherWorld targetWorld)
    {
        this.targetWorld = targetWorld;
    }

    public void setWorld(NetherWorld world)
    {
        this.world = world;
    }
}
