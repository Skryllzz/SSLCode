package com.zenyte.game;

import com.zenyte.network.io.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class HardwareInfo {
    private final int[] cpuFeatures = new int[3];
    private int osId;
    private int osVersion;
    private int javaVendorId;
    private int javaVersionMajor;
    private int javaVersionMinor;
    private int javaVersionUpdate;
    private int heap;
    private int logicalProcessors;
    private int physicalMemory;
    private int clockSpeed;
    private int graphicCardReleaseMonth;
    private int graphicCardReleaseYear;
    private int cpuCount;
    private int cpuBrandType;
    private int cpuModel;
    private String graphicCardManufacture;
    private String graphicCardName;
    private String dxVersion;
    private String cpuManufacture;
    private String cpuName;
    private boolean arch64Bit;
    private boolean isApplet;

    public HardwareInfo(final ByteBuf buffer) {
        decode(buffer);
    }

    @Override
    public int hashCode() {
        return clockSpeed + (osId << 12) + (osVersion << 16) + (logicalProcessors << 24);
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof HardwareInfo o)) {
            return false;
        }
        return osId == o.osId && arch64Bit == o.arch64Bit && osVersion == o.osVersion && javaVendorId == o.javaVendorId && javaVersionMajor == o.javaVersionMajor && javaVersionMinor == o.javaVersionMinor && javaVersionUpdate == o.javaVersionUpdate && isApplet == o.isApplet && heap == o.heap && logicalProcessors == o.logicalProcessors && physicalMemory == o.physicalMemory && clockSpeed == o.clockSpeed;
    }

    /**
     * Decode's hardware information.
     */
    @SuppressWarnings("unused")
    private void decode(final ByteBuf buffer) {
        final short version = buffer.readUnsignedByte();
		if (version != 9) {
			throw new RuntimeException("Unsupported version: " + version);
		}
        osId = buffer.readUnsignedByte();
        arch64Bit = buffer.readUnsignedByte() == 1;
        osVersion = buffer.readUnsignedShort();
        javaVendorId = buffer.readUnsignedByte();
        javaVersionMajor = buffer.readUnsignedByte();
        javaVersionMinor = buffer.readUnsignedByte();
        javaVersionUpdate = buffer.readUnsignedByte();
        isApplet = buffer.readUnsignedByte() == 0;
        heap = buffer.readUnsignedShort();
        logicalProcessors = buffer.readUnsignedByte(); // only if > java1.3
        physicalMemory = ByteBufUtil.readMedium(buffer);
        clockSpeed = buffer.readUnsignedShort();
        graphicCardManufacture = ByteBufUtil.readJAGString(buffer);
        graphicCardName = ByteBufUtil.readJAGString(buffer);
        final String empty3 = ByteBufUtil.readJAGString(buffer);
        dxVersion = ByteBufUtil.readJAGString(buffer);
        graphicCardReleaseMonth = buffer.readUnsignedByte();
        graphicCardReleaseYear = buffer.readUnsignedShort();
        cpuManufacture = ByteBufUtil.readJAGString(buffer);
        cpuName = ByteBufUtil.readJAGString(buffer);
        cpuCount = buffer.readUnsignedByte();
        cpuBrandType = buffer.readUnsignedByte();
        for (int index = 0; index < cpuFeatures.length; index++) {
            cpuFeatures[index] = buffer.readInt();
        }
        final int cpuModel = buffer.readInt();
        ByteBufUtil.readJAGString(buffer);//unknown
        ByteBufUtil.readJAGString(buffer);//unknown
    }

    public int getOsId() {
        return this.osId;
    }

    public int getOsVersion() {
        return this.osVersion;
    }

    public int getJavaVendorId() {
        return this.javaVendorId;
    }

    public int getJavaVersionMajor() {
        return this.javaVersionMajor;
    }

    public int getJavaVersionMinor() {
        return this.javaVersionMinor;
    }

    public int getJavaVersionUpdate() {
        return this.javaVersionUpdate;
    }

    public int getHeap() {
        return this.heap;
    }

    public int getLogicalProcessors() {
        return this.logicalProcessors;
    }

    public int getPhysicalMemory() {
        return this.physicalMemory;
    }

    public int getClockSpeed() {
        return this.clockSpeed;
    }

    public int getGraphicCardReleaseMonth() {
        return this.graphicCardReleaseMonth;
    }

    public int getGraphicCardReleaseYear() {
        return this.graphicCardReleaseYear;
    }

    public int getCpuCount() {
        return this.cpuCount;
    }

    public int getCpuBrandType() {
        return this.cpuBrandType;
    }

    public int getCpuModel() {
        return this.cpuModel;
    }

    public String getGraphicCardManufacture() {
        return this.graphicCardManufacture;
    }

    public String getGraphicCardName() {
        return this.graphicCardName;
    }

    public String getDxVersion() {
        return this.dxVersion;
    }

    public String getCpuManufacture() {
        return this.cpuManufacture;
    }

    public String getCpuName() {
        return this.cpuName;
    }

    public int[] getCpuFeatures() {
        return this.cpuFeatures;
    }

    public boolean isArch64Bit() {
        return this.arch64Bit;
    }

    public boolean isApplet() {
        return this.isApplet;
    }
}
