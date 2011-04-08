package com.elmakers.mine.bukkit.plugins.nether.dao;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.BlockVector;

import com.elmakers.mine.bukkit.persisted.PersistClass;
import com.elmakers.mine.bukkit.persisted.PersistField;
import com.elmakers.mine.bukkit.persisted.Persisted;
import com.elmakers.mine.bukkit.persistence.dao.BoundingBox;
import com.elmakers.mine.bukkit.persistence.dao.WorldData;
import com.elmakers.mine.bukkit.plugins.nether.NetherManager;

@PersistClass(schema = "nether", name = "world")
public class NetherWorld extends Persisted
{
    protected BlockVector  centerOffset;

    // Transient
    protected List<Portal> portals;

    protected double       scale;

    protected PortalArea   targetArea;

    protected BlockVector  targetOffset;

    protected NetherWorld  targetWorld;

    protected WorldData    world;

    public NetherWorld()
    {

    }

    public NetherWorld(WorldData world)
    {
        this.world = world;
        targetArea = null;

        targetOffset = NetherManager.origin;
        centerOffset = NetherManager.origin;
    }

    /**
     * Adds a Portal to this world.
     * 
     * You *should* only call this if this Portal's Location is in this world!
     * 
     * Also, this does not check for duplicates- it's just a quick add to the
     * transient list, meant to stay in sync with the Portal Location world
     * values.
     * 
     * @param portal
     *            the portal to add to this world
     */
    public void addPortal(Portal portal)
    {
        if (portals == null)
        {
            portals = new ArrayList<Portal>();
        }
        portals.add(portal);
    }

    public void bind(NetherWorld currentWorld)
    {
        currentWorld.targetWorld = this;
        targetWorld = currentWorld;

        // Save changes to current world target
        getPersistence().put(currentWorld);
    }

    public Portal findPortalAt(BlockVector position)
    {
        if (portals == null)
        {
            return null;
        }
        for (Portal portal : portals)
        {
            BoundingBox portalBlocks = portal.getBoundingBox();
            if (portalBlocks.contains(position))
            {
                return portal;
            }
        }

        return null;
    }

    @PersistField(contained = true)
    public BlockVector getCenterOffset()
    {
        return centerOffset;
    }

    @PersistField
    public double getScale()
    {
        return scale;
    }

    @PersistField
    public PortalArea getTargetArea()
    {
        return targetArea;
    }

    @PersistField(contained = true)
    public BlockVector getTargetOffset()
    {
        return targetOffset;
    }

    @PersistField
    public NetherWorld getTargetWorld()
    {
        return targetWorld;
    }

    @PersistField(id = true)
    public WorldData getWorld()
    {
        return world;
    }

    public void populatePortals(List<Portal> allPortals)
    {
        if (portals == null)
        {
            return;
        }

        portals = new ArrayList<Portal>();
        for (Portal portal : allPortals)
        {
            if (portal.getLocation().getWorld() == world)
            {
                portals.add(portal);
            }
        }
    }

    public void setCenterOffset(BlockVector centerOffset)
    {
        this.centerOffset = centerOffset;
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }

    public void setTargetArea(PortalArea targetArea)
    {
        this.targetArea = targetArea;
    }

    public void setTargetOffset(BlockVector targetOffset)
    {
        this.targetOffset = targetOffset;
    }

    public void setTargetWorld(NetherWorld targetWorld)
    {
        this.targetWorld = targetWorld;
    }

    public void setWorld(WorldData world)
    {
        this.world = world;
    }
}
