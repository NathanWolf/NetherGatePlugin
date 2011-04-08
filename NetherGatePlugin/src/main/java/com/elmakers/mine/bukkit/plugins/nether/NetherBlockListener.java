package com.elmakers.mine.bukkit.plugins.nether;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class NetherBlockListener extends BlockListener
{
    protected NetherManager manager;

    public NetherBlockListener(NetherManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        if (!manager.allowPhysics(event.getBlock()))
        {
            event.setCancelled(true);
        }
    }

}
