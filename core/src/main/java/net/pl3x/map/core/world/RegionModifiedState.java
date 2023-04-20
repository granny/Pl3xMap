package net.pl3x.map.core.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RegionModifiedState {
    private final Map<Long, Long> regionModifiedStates = new ConcurrentHashMap<>(); // <pos, modified>
    private final File file;

    public RegionModifiedState(@NonNull World world) {
        this.file = world.getTilesDirectory().resolve(".rms").toFile();

        if (this.file.exists()) {
            try (DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(this.file)))) {
                int size = in.readInt();
                for (int i = 0; i < size; i++) {
                    this.regionModifiedStates.put(in.readLong(), in.readLong());
                }
            } catch (Throwable ignore) {
            }
        }
    }

    public void set(long regionPos, long modified) {
        this.regionModifiedStates.put(regionPos, modified);
    }

    public long get(long regionPos) {
        Long modified = this.regionModifiedStates.get(regionPos);
        return modified == null ? -1 : modified;
    }

    public void save() {
        try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(this.file)))) {
            out.writeInt(this.regionModifiedStates.size());
            for (Map.Entry<Long, Long> entry : this.regionModifiedStates.entrySet()) {
                out.writeLong(entry.getKey());
                out.writeLong(entry.getValue());
            }
            out.flush();
        } catch (Throwable ignore) {
        }
    }
}
