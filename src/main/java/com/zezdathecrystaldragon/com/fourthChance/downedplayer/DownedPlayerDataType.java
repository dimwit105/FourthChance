package com.zezdathecrystaldragon.com.fourthChance.downedplayer;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.nio.ByteBuffer;

public class DownedPlayerDataType implements PersistentDataType<byte[], DownedPlayer>
{

    @Override
    public Class<byte[]> getPrimitiveType() {return byte[].class;}

    @Override
    public Class<DownedPlayer> getComplexType() {return DownedPlayer.class;}

    @Override
    public byte[] toPrimitive(DownedPlayer dp, PersistentDataAdapterContext persistentDataAdapterContext)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[33]);
        bb.putLong(dp.id.getMostSignificantBits());
        bb.putLong(dp.id.getLeastSignificantBits());
        bb.putInt(dp.reviveCount);
        bb.putInt(dp.reviveForgiveProgress);
        bb.putDouble(dp.minimumDownedHealth);
        bb.put((byte) (dp.downed ? 1 : 0));
        return bb.array();
    }

    @Override
    public DownedPlayer fromPrimitive(byte[] bytes, PersistentDataAdapterContext persistentDataAdapterContext) {
        return new DownedPlayer(bytes);
    }
}
