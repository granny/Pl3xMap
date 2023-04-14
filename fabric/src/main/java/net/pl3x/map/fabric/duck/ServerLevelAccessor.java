package net.pl3x.map.fabric.duck;

import net.minecraft.world.level.storage.LevelStorageSource;

public interface ServerLevelAccessor {
    LevelStorageSource.LevelStorageAccess getStorage();
}
