package StreamTest;

import com.google.common.cache.LoadingCache;
//import org.apache.flink.api.common.typeutils.CompositeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSchemaCompatibility;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.base.MapSerializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.util.Preconditions;

import java.io.IOException;
import java.util.Map;

public abstract class LoadcacheSerializerSnapshot<K, V> implements TypeSerializerSnapshot<LoadingCache<K, V>> {
    /*private static final int CURRENT_VERSION = 1;
    private CompositeSerializerSnapshot nestedKeyValueSerializerSnapshot;

    public LoadcacheSerializerSnapshot() {
    }

    public LoadcacheSerializerSnapshot(TypeSerializer<K> keySerializer, TypeSerializer<V> valueSerializer) {
        Preconditions.checkNotNull(keySerializer);
        Preconditions.checkNotNull(valueSerializer);
        this.nestedKeyValueSerializerSnapshot = new CompositeSerializerSnapshot(new TypeSerializer[]{keySerializer, valueSerializer});
    }

    @Override
    public int getCurrentVersion() {
        return 1;
    }

    @Override
    public void writeSnapshot(DataOutputView dataOutputView) throws IOException {
        this.nestedKeyValueSerializerSnapshot.writeCompositeSnapshot(dataOutputView);
    }

    @Override
    public void readSnapshot(int i, DataInputView dataInputView, ClassLoader classLoader) throws IOException {
        this.nestedKeyValueSerializerSnapshot = CompositeSerializerSnapshot.readCompositeSnapshot(dataInputView, classLoader);
    }

    @Override
    public TypeSerializer<LoadingCache<K, V>> restoreSerializer() {
        return new LoadcacheSerializer(this.nestedKeyValueSerializerSnapshot.getRestoreSerializer(0), this.nestedKeyValueSerializerSnapshot.getRestoreSerializer(1));
    }

    @Override
    public TypeSerializerSchemaCompatibility<LoadingCache<K, V>> resolveSchemaCompatibility(TypeSerializer<LoadingCache<K, V>> newSerializer) {
        Preconditions.checkState(this.nestedKeyValueSerializerSnapshot != null);
        if (newSerializer instanceof LoadcacheSerializer) {
            LoadcacheSerializer<K, V> serializer = (LoadcacheSerializer)newSerializer;
            return this.nestedKeyValueSerializerSnapshot.resolveCompatibilityWithNested(TypeSerializerSchemaCompatibility.compatibleAsIs(), new TypeSerializer[]{serializer.getKeySerializer(), serializer.getValueSerializer()});
        } else {
            return TypeSerializerSchemaCompatibility.incompatible();
        }
    }*/
}
