package com.elmakers.mine.bukkit.plugins.nether.dao;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import com.elmakers.mine.bukkit.persisted.PersistClass;
import com.elmakers.mine.bukkit.persisted.PersistField;
import com.elmakers.mine.bukkit.persisted.Persisted;
import com.elmakers.mine.bukkit.persistence.dao.LocationData;
import com.elmakers.mine.bukkit.persistence.dao.PlayerData;

@PersistClass(schema = "nether", name = "player")
public class NetherPlayer extends Persisted
{
    public enum TeleportState
    {
        NONE, TELEPORTED, TELEPORTING
    }

    static final long       defaultShieldInterval = 10000;

    protected LocationData  home;

    protected BlockVector   lastLocation;

    // Transient
    protected PlayerData    player;

    protected Long          shieldTimer;

    protected PortalArea    sourceArea;

    protected Portal        sourcePortal;

    protected NetherWorld   sourceWorld;

    protected TeleportState state;

    protected PortalArea    targetArea;

    protected BlockVector   targetLocation;

    protected Portal        targetPortal;

    protected NetherWorld   targetWorld;

    public NetherPlayer()
    {

    }

    public NetherPlayer(PlayerData player)
    {
        this.player = player;
        update(player.getPlayer());
    }

    public boolean areShieldsUp()
    {
        if (shieldTimer == null)
        {
            return false;
        }

        if (System.currentTimeMillis() > shieldTimer)
        {
            shieldTimer = null;
        }

        return true;
    }

    @PersistField(contained = true)
    public LocationData getHome()
    {
        return home;
    }

    @PersistField(contained = true)
    public BlockVector getLastLocation()
    {
        return lastLocation;
    }

    @PersistField(id = true)
    public PlayerData getPlayer()
    {
        return player;
    }

    public PortalArea getSourceArea()
    {
        return sourceArea;
    }

    public Portal getSourcePortal()
    {
        return sourcePortal;
    }

    public NetherWorld getSourceWorld()
    {
        return sourceWorld;
    }

    // Transient state data
    public TeleportState getState()
    {
        return state;
    }

    public PortalArea getTargetArea()
    {
        return targetArea;
    }

    public BlockVector getTargetLocation()
    {
        return targetLocation;
    }

    public Portal getTargetPortal()
    {
        return targetPortal;
    }

    public NetherWorld getTargetWorld()
    {
        return targetWorld;
    }

    public void setHome(LocationData home)
    {
        this.home = home;
    }

    public void setLastLocation(BlockVector lastLocation)
    {
        this.lastLocation = lastLocation;
    }

    public void setPlayer(PlayerData player)
    {
        this.player = player;
    }

    public void setSourceArea(PortalArea sourceArea)
    {
        this.sourceArea = sourceArea;
    }

    public void setSourcePortal(Portal sourcePortal)
    {
        this.sourcePortal = sourcePortal;
    }

    public void setSourceWorld(NetherWorld sourceWorld)
    {
        this.sourceWorld = sourceWorld;
    }

    public void setState(TeleportState state)
    {
        if (state == TeleportState.TELEPORTING || state == TeleportState.TELEPORTED)
        {
            shieldTimer = System.currentTimeMillis() + defaultShieldInterval;
        }
        this.state = state;
    }

    public void setTargetArea(PortalArea targetArea)
    {
        this.targetArea = targetArea;
    }

    public void setTargetLocation(BlockVector targetLocation)
    {
        this.targetLocation = targetLocation;
    }

    public void setTargetPortal(Portal targetPortal)
    {
        this.targetPortal = targetPortal;
    }

    public void setTargetWorld(NetherWorld targetWorld)
    {
        this.targetWorld = targetWorld;
    }

    public void update(Player player)
    {
        if (player == null)
        {
            return;
        }

        Location loc = player.getLocation();
        if (home == null)
        {
            home = new LocationData(loc);
        }
        lastLocation = new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
