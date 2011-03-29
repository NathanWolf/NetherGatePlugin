package com.elmakers.mine.bukkit.plugins.nether;

import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;

public class NetherWorldListener extends WorldListener
{
    protected NetherManager manager;

    public NetherWorldListener(NetherManager m)
    {
        manager = m;
    }

    @Override
    public void onChunkLoad(ChunkLoadEvent event)
    {
        manager.onChunkLoaded(event.getChunk());
    }
}
