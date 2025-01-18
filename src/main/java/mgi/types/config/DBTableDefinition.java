package mgi.types.config;

import com.zenyte.Game;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;
import net.runelite.cache.util.ScriptVarType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBTableDefinition implements Definitions, Cloneable {
	private static final Logger log = LoggerFactory.getLogger(DBTableDefinition.class);
	private static DBTableDefinition[] definitions;

	public static DBTableDefinition get(final int id) {
		if (id < 0 || id >= definitions.length) throw new IllegalArgumentException();
		return definitions[id];
	}

	private DBTableDefinition(final int id, final ByteBuffer buffer) {
		this.id = id;
		decode(buffer);
	}

	private final int id;
	private ScriptVarType[][] types;
	private Object[] defaultColumnValues;

	@Override
	public void load() {
		final Cache cache = Game.getCacheMgi();
		final Archive configs = cache.getArchive(ArchiveType.CONFIGS);
		final Group dbtables = configs.findGroupByID(GroupType.DBTABLE);
		definitions = new DBTableDefinition[dbtables.getHighestFileId()];
		for (int id = 0; id < dbtables.getHighestFileId(); id++) {
			final File file = dbtables.findFileByID(id);
			if (file == null) {
				continue;
			}
			final ByteBuffer buffer = file.getData();
			if (buffer == null) {
				continue;
			}
			if (buffer.remaining() < 1) {
				continue;
			}
			definitions[id] = new DBTableDefinition(id, buffer);
		}
	}

	public DBTableDefinition clone() throws CloneNotSupportedException {
		return (DBTableDefinition) super.clone();
	}

	@Override
	public void decode(final ByteBuffer buffer) {
		while (true) {
			final int opcode = buffer.readUnsignedByte();
			if (opcode == 0) {
				return;
			}
			decode(buffer, opcode);
		}
	}

	@Override
	public void decode(final ByteBuffer buffer, int opcode) {
		if (opcode == 1) {
			int numColumns = buffer.readUnsignedByte();
			ScriptVarType[][] types = new ScriptVarType[numColumns][];
			Object[][] defaultValues = null;

			for(int setting = buffer.readUnsignedByte(); setting != 255; setting = buffer.readUnsignedByte()) {
				int columnId = setting & 127;
				boolean hasDefault = (setting & 128) != 0;
				ScriptVarType[] columnTypes = new ScriptVarType[buffer.readUnsignedByte()];

				for(int i = 0; i < columnTypes.length; ++i) {
					columnTypes[i] = ScriptVarType.forId(buffer.readUnsignedSmart());
				}

				types[columnId] = columnTypes;
				if (hasDefault) {
					if (defaultValues == null) {
						defaultValues = new Object[types.length][];
					}

					defaultValues[columnId] = decodeColumnFields(buffer, columnTypes);
				}
			}

			this.types = types;
			this.defaultColumnValues = defaultValues;
			return;
		}
		throw new IllegalStateException("Opcode: " + opcode);
	}

	private Object[] decodeColumnFields(ByteBuffer stream, ScriptVarType[] types) {
		int fieldCount = stream.readUnsignedSmart();
		Object[] values = new Object[fieldCount * types.length];

		for(int fieldIndex = 0; fieldIndex < fieldCount; ++fieldIndex) {
			for(int typeIndex = 0; typeIndex < types.length; ++typeIndex) {
				ScriptVarType type = types[typeIndex];
				int valuesIndex = fieldIndex * types.length + typeIndex;
				if (type == ScriptVarType.STRING) {
					values[valuesIndex] = stream.readString();
				} else {
					values[valuesIndex] = stream.readInt();
				}
			}
		}

		return values;
	}

	@Override
	public ByteBuffer encode() {
		final ByteBuffer buffer = new ByteBuffer(1132);
		buffer.writeByte(0);
		return buffer;
	}

	@Override
	public void pack() {
		final Cache cache = Game.getCacheMgi();
		cache.getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.DBTABLE).addFile(new File(id, encode()));
	}

	public DBTableDefinition(int id) {
		this.id = id;
	}

	public DBTableDefinition() {
		this.id = 0;
	}

	public int getId() {
		return id;
	}

	public Object[] getDefaultColumnValues() {
		return defaultColumnValues;
	}

	public ScriptVarType[][] getTypes() {
		return types;
	}

}
