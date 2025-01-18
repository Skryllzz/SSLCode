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

public class DBRowDefinition implements Definitions, Cloneable {
	private static final Logger log = LoggerFactory.getLogger(DBRowDefinition.class);
	public static DBRowDefinition[] definitions;

	public static DBRowDefinition get(final int id) {
		if (id < 0 || id >= definitions.length) throw new IllegalArgumentException();
		return definitions[id];
	}

	private DBRowDefinition(final int id, final ByteBuffer buffer) {
		this.id = id;
		decode(buffer);
	}

	private final int id;
	private int tableId;
	private ScriptVarType[][] columnTypes;
	private Object[][] columnValues;

	@Override
	public void load() {
		final Cache cache = Game.getCacheMgi();
		final Archive configs = cache.getArchive(ArchiveType.CONFIGS);
		final Group dbrow = configs.findGroupByID(GroupType.DBROW);
		definitions = new DBRowDefinition[dbrow.getHighestFileId()];
		for (int id = 0; id < dbrow.getHighestFileId(); id++) {
			final File file = dbrow.findFileByID(id);
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
			definitions[id] = new DBRowDefinition(id, buffer);
		}
	}

	public DBRowDefinition clone() throws CloneNotSupportedException {
		return (DBRowDefinition) super.clone();
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
		switch (opcode) {
			case 3:
				int numColumns = buffer.readUnsignedByte();
				ScriptVarType[][] types = new ScriptVarType[numColumns][];
				Object[][] columnValues = new Object[numColumns][];

				for (int columnId = buffer.readUnsignedByte(); columnId != 255; columnId = buffer.readUnsignedByte()) {
					ScriptVarType[] columnTypes = new ScriptVarType[buffer.readUnsignedByte()];

					for (int i = 0; i < columnTypes.length; ++i) {
						columnTypes[i] = ScriptVarType.forId(buffer.readUnsignedSmart());
					}

					types[columnId] = columnTypes;
					columnValues[columnId] = decodeColumnFields(buffer, columnTypes);
				}

				this.columnTypes = types;
				this.columnValues = columnValues;
				break;
			case 4:
				this.tableId = buffer.readVarInt2();
				break;
			default:
				throw new IllegalStateException("Opcode: " + opcode);
		}
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
		cache.getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.DBROW).addFile(new File(id, encode()));
	}

	public DBRowDefinition(int id) {
		this.id = id;
	}

	public DBRowDefinition() {
		this.id = 0;
	}

	public int getId() {
		return id;
	}

	public int getTableId() {
		return tableId;
	}

	public Object[][] getColumnValues() {
		return columnValues;
	}

}
