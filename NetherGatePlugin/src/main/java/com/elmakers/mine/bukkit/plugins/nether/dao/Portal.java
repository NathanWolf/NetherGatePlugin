package com.elmakers.mine.bukkit.plugins.nether.dao;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import com.elmakers.mine.bukkit.persisted.PersistClass;
import com.elmakers.mine.bukkit.persisted.PersistField;
import com.elmakers.mine.bukkit.persisted.Persisted;
import com.elmakers.mine.bukkit.persistence.dao.BlockList;
import com.elmakers.mine.bukkit.persistence.dao.BoundingBox;
import com.elmakers.mine.bukkit.persistence.dao.LocationData;
import com.elmakers.mine.bukkit.persistence.dao.MaterialList;
import com.elmakers.mine.bukkit.persistence.dao.Orientation;
import com.elmakers.mine.bukkit.plugins.nether.NetherManager;
import com.elmakers.mine.bukkit.utilities.CSVParser;

@PersistClass(schema = "nether", name = "portal")
public class Portal extends Persisted
{
    protected static final String DEFAULT_DESTRUCTIBLES = "0,1,2,3,4,10,11,12,13,14,15,16,21,51,56,78,79,82,87,88,89";

    protected static MaterialList destructible          = null;

    protected static MaterialList needsPlatform         = null;

    protected static void buildFrame(Block centerBlock, BlockFace facing, BlockList blockList)
    {
        BoundingBox leftSide = new BoundingBox(centerBlock.getX() - 2, centerBlock.getY() - 1, centerBlock.getZ() - 1, centerBlock.getX() - 1, centerBlock.getY() + 4, centerBlock.getZ());
        BoundingBox rightSide = new BoundingBox(centerBlock.getX() + 2, centerBlock.getY() - 1, centerBlock.getZ() - 1, centerBlock.getX() + 1, centerBlock.getY() + 4, centerBlock.getZ());
        BoundingBox top = new BoundingBox(centerBlock.getX() - 1, centerBlock.getY() + 3, centerBlock.getZ() - 1, centerBlock.getX() + 1, centerBlock.getY() + 4, centerBlock.getZ());
        BoundingBox bottom = new BoundingBox(centerBlock.getX() - 1, centerBlock.getY() - 1, centerBlock.getZ() - 1, centerBlock.getX() + 1, centerBlock.getY(), centerBlock.getZ());

        leftSide.fill(centerBlock.getWorld(), Material.OBSIDIAN, destructible, blockList);
        rightSide.fill(centerBlock.getWorld(), Material.OBSIDIAN, destructible, blockList);
        top.fill(centerBlock.getWorld(), Material.OBSIDIAN, destructible, blockList);
        bottom.fill(centerBlock.getWorld(), Material.OBSIDIAN, destructible, blockList);
    }

    protected static void buildPlatform(Block centerBlock, BlockList blockList)
    {
        BoundingBox platform = new BoundingBox(centerBlock.getX() - 3, centerBlock.getY() - 1, centerBlock.getZ() - 3, centerBlock.getX() + 2, centerBlock.getY(), centerBlock.getZ() + 2);

        platform.fill(centerBlock.getWorld(), Material.OBSIDIAN, needsPlatform, blockList);
    }

    protected static void clearPortalArea(Block centerBlock, BlockList blockList)
    {
        BoundingBox container = new BoundingBox(centerBlock.getX() - 3, centerBlock.getY(), centerBlock.getZ() - 3, centerBlock.getX() + 2, centerBlock.getY() + 4, centerBlock.getZ() + 2);

        container.fill(centerBlock.getWorld(), Material.AIR, destructible, blockList);
    }

    protected boolean       active;

    protected BlockList     blocks;

    protected PortalArea    container;

    protected NetherPlayer  creator;

    protected int           id;

    protected Date          lastUsed;

    protected NetherPlayer  lastUsedBy;

    protected LocationData  location;

    // transient
    protected NetherManager manager;

    protected String        name;

    protected Portal        target;

    protected PortalType    type;

    protected boolean       updatePending;

    public Portal()
    {

    }

    public Portal(Player creator, Location location, PortalType portalType, NetherManager manager)
    {
        initialize(manager);
        this.location = new LocationData(location);

        // Match the player's facing
        this.location.updateOrientation(creator.getLocation());

        this.creator = getPersistence().get(creator.getName(), NetherPlayer.class);
        this.active = true;
        this.updatePending = false;
        this.type = portalType;
    }

    public void build(boolean fillAir)
    {
        blocks = new BlockList();
        build(fillAir, blocks);
    }

    public void build(boolean fillAir, BlockList blockList)
    {
        World world = location.getWorld();
        Location loc = location.getLocation();
        Block centerBlock = world.getBlockAt(loc);
        Orientation orientation = location.getOrientation();
        BlockFace facing = orientation.getYaw() == 0 ? BlockFace.NORTH : BlockFace.WEST;
        if (fillAir)
        {
            clearPortalArea(centerBlock, blockList);
        }
        updatePending = false;

        if (type.hasPlatform())
        {
            buildPlatform(centerBlock, blockList);
        }

        if (type.hasFrame())
        {
            buildFrame(centerBlock, facing, blockList);
        }

        if (type.hasPortal())
        {
            buildPortalBlocks(centerBlock, facing, blockList);
        }
    }

    protected void buildPortalBlocks(Block centerBlock, BlockFace facing, BlockList blockList)
    {
        manager.disablePhysics();
        BoundingBox container = new BoundingBox(centerBlock.getX() - 1, centerBlock.getY(), centerBlock.getZ() - 1, centerBlock.getX() + 1, centerBlock.getY() + 3, centerBlock.getZ());

        container.fill(centerBlock.getWorld(), Material.PORTAL, destructible, blockList);
    }

    // TODO : get this working!
    // @PersistField(contained=true)
    public BlockList getBlocks()
    {
        return blocks;
    }

    public BoundingBox getBoundingBox()
    {
        BlockVector position = location.getPosition();
        Orientation orientation = location.getOrientation();
        if (orientation.getYaw() == 0)
        {
            BlockVector min = new BlockVector(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            BlockVector max = new BlockVector(position.getBlockX() + 1, position.getBlockY(), position.getBlockZ());
            return new BoundingBox(min, max);
        }

        BlockVector min = new BlockVector(position.getBlockX(), position.getBlockY(), position.getBlockZ());
        BlockVector max = new BlockVector(position.getBlockX(), position.getBlockY(), position.getBlockZ() + 1);
        return new BoundingBox(min, max);
    }

    @PersistField
    public PortalArea getContainer()
    {
        return container;
    }

    @PersistField
    public NetherPlayer getCreator()
    {
        return creator;
    }

    @PersistField(id = true, auto = true)
    public int getId()
    {
        return id;
    }

    @PersistField
    public Date getLastUsed()
    {
        return lastUsed;
    }

    @PersistField
    public NetherPlayer getLastUsedBy()
    {
        return lastUsedBy;
    }

    @PersistField(contained = true)
    public LocationData getLocation()
    {
        return location;
    }

    @PersistField
    public String getName()
    {
        return name;
    }

    @PersistField
    public Portal getTarget()
    {
        return target;
    }

    @PersistField
    public PortalType getType()
    {
        return type;
    }

    public void initialize(NetherManager manager)
    {
        this.manager = manager;
        if (destructible == null)
        {
            destructible = new MaterialList();
            needsPlatform = new MaterialList();

            needsPlatform.add(Material.WATER);
            needsPlatform.add(Material.STATIONARY_WATER);
            needsPlatform.add(Material.LAVA);
            needsPlatform.add(Material.STATIONARY_LAVA);

            CSVParser csv = new CSVParser();
            destructible = csv.parseMaterials(DEFAULT_DESTRUCTIBLES);
        }
    }

    @PersistField
    public boolean isActive()
    {
        return active;
    }

    @PersistField
    public boolean isUpdatePending()
    {
        return updatePending;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public void setBlocks(BlockList blocks)
    {
        this.blocks = blocks;
    }

    public void setContainer(PortalArea container)
    {
        this.container = container;
    }

    public void setCreator(NetherPlayer creator)
    {
        this.creator = creator;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setLastUsed(Date lastUsed)
    {
        this.lastUsed = lastUsed;
    }

    public void setLastUsedBy(NetherPlayer lastUsedBy)
    {
        this.lastUsedBy = lastUsedBy;
    }

    public void setLocation(LocationData location)
    {
        this.location = location;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setTarget(Portal target)
    {
        this.target = target;
    }

    public void setType(PortalType type)
    {
        this.type = type;
    }

    public void setUpdatePending(boolean updatePending)
    {
        this.updatePending = updatePending;
    }

    public void use(NetherPlayer player)
    {

    }
}
