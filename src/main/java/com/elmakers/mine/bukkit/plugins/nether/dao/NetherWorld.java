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

@PersistClass(schema="nether", name="world")
public class NetherWorld extends Persisted
{
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
	
	public void bind(NetherWorld currentWorld)
	{
		currentWorld.targetWorld = this;
		targetWorld = currentWorld;
		
		// Save changes to current world target
		getPersistence().put(currentWorld);
	}
	
	@PersistField(id=true)
	public WorldData getWorld()
	{
		return world;
	}
	
	public void setWorld(WorldData world)
	{
		this.world = world;
	}
	
	@PersistField
	public PortalArea getTargetArea()
	{
		return targetArea;
	}

	public void setTargetArea(PortalArea targetArea)
	{
		this.targetArea = targetArea;
	}

	@PersistField
	public NetherWorld getTargetWorld()
	{
		return targetWorld;
	}
	
	public void setTargetWorld(NetherWorld targetWorld)
	{
		this.targetWorld = targetWorld;
	}
	
	@PersistField(contained=true)
	public BlockVector getTargetOffset()
	{
		return targetOffset;
	}
	
	public void setTargetOffset(BlockVector targetOffset)
	{
		this.targetOffset = targetOffset;
	}
	
	@PersistField(contained=true)
	public BlockVector getCenterOffset()
	{
		return centerOffset;
	}
	
	public void setCenterOffset(BlockVector centerOffset)
	{
		this.centerOffset = centerOffset;
	}
	
	@PersistField
	public double getScale()
	{
		return scale;
	}
	
	public void setScale(double scale)
	{
		this.scale = scale;
	}
	
	public void populatePortals(List<Portal> allPortals)
	{
		if (portals == null) return;
		
		portals = new ArrayList<Portal>();
		for (Portal portal : allPortals)
		{
			if (portal.getLocation().getWorld() == world)
			{
				portals.add(portal);
			}
		}
	}
	
	/**
	 * Adds a Portal to this world.
	 * 
	 * You *should* only call this if this Portal's Location
	 * is in this world!
	 * 
	 * Also, this does not check for duplicates- it's just a 
	 * quick add to the transient list, meant to stay in sync
	 * with the Portal Location world values.
	 * 
	 * @param portal the portal to add to this world
	 */
	public void addPortal(Portal portal)
	{
		if (portals == null)
		{
			portals = new ArrayList<Portal>();
		}
		portals.add(portal);
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
	
	protected WorldData 	world;
	protected NetherWorld	targetWorld;
	protected PortalArea	targetArea;
	protected BlockVector	targetOffset;
	protected BlockVector	centerOffset;
	protected double		scale;
	
	// Transient
	protected List<Portal>	portals;
}
