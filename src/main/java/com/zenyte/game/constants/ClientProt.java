package com.zenyte.game.constants;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Sorted by Kris.
 *
 * @author Tommeh | 28 jul. 2018 | 12:45:15 | @author Kris | 23. sept 2018 : 02:09:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public enum ClientProt {
    NO_TIMEOUT(0, 0),
    OPOBJ5(1, 7),
    EVENT_MOUSE_MOVE(2, -1),
    EVENT_MOUSE_CLICK(3, 6),
    OPLOC4(4, 7),
    UNUSED_1(5, 7),
    IGNORELIST_ADD(6, -1),
    SET_CHATFILTERSETTINGS(7, 3),
    EVENT_CAMERA_POSITION(8, 4),
    OPNPC4(9, 3),
    WINDOW_STATUS(10, 5),
    OPPLAYER8(11, 3),
    CLICKWORLDMAP(12, 4),
    field3074(13, -1),
    MESSAGE_PRIVATE(14, -2),
    CLAN_JOINCHAT_LEAVECHAT(15, -1),
    OPOBJ4(16, 7),
    OPNPCU(17, 11),
    IF_BUTTON3(18, 8),
    FRIENDLIST_DEL(19, -1),
    OPNPC6(20, 2),
    CLAN_KICKUSER(21, -1),
    IF_BUTTON4(22, 8),
    OPLOC1(23, 7),
    field3085(24, -1),
    IF_BUTTON10(25, 8),
    MOVE_MINIMAPCLICK(26, -1),
    field3088(27, 16),
    LOGIN_STATISTICS(28, -1),
    BUTTON_CLICK(29, 4),
    OPPLAYER7(30, 3),
    OPOBJ6(31, 2),
    OPNPC1(32, 3),
    FRIENDLIST_ADD(33, -1),
    field3095(34, -1),
    DETECT_MODIFIED_CLIENT(35, 4),
    IF1_BUTTON3(36, 8),
    OPPLAYER1(37, 3),
    OPOBJ3(38, 7),
    EVENT_APPLET_FOCUS(39, 1),
    field3101(40, 13),
    OPLOC2(41, 7),
    RESUME_P_STRINGDIALOG(42, -1),
    OPHELD4(43, 8),
    IF1_BUTTON5(44, 8),
    UNUSED_2(45, -1),
    MESSAGE_PUBLIC(46, -1),
    OPPLAYERU(47, 11),
    SEND_PING_REPLY(48, 10),
    OPPLAYER6(49, 3),
    OPPLAYER5(50, 3),
    field3080(51, 14),
    CLIENT_CHEAT(52, -1),
    FRIEND_SETRANK(53, -1),
    RESUME_P_NAMEDIALOG(54, -1),
    IF_BUTTON2(55, 8),
    OPOBJU(56, 15),
    IF_BUTTON8(57, 8),
    IF_BUTTOND(58, 16),
    OPNPC2(59, 3),
    OPPLAYERT(60, 11),
    IGNORELIST_DEL(61, -1),
    IF_BUTTON1(62, 8),
    OPNPC5(63, 3),
    IF1_BUTTON4(64, 8),
    OPLOC5(65, 7),
    UNUSED_3(66, -1),
    OPLOC3(67, 7),
    IF_BUTTONT(68, 16),
    BUG_REPORT(69, -2),
    OPNPC3(70, 3),
    REFLECTION_CHECK_REPLY(71, -1),
    TELEPORT(72, 9),
    UNUSED_4(73, 2),
    RESUME_P_COUNTDIALOG(74, 4),
    IF_BUTTON6(75, 8),
    OPHELD5(76, 8),
    UNUSED_5(77, -1),
    IF_BUTTON7(78, 8),
    IDLE_LOGOUT(79, 0),
    OPLOC6(80, 2),
    IF1_BUTTON2(81, 8),
    OPOBJT(82, 15),
    OPHELDD(83, 9),
    MAP_BUILD_COMPLETE(84, 0),
    IF_BUTTON5(85, 8),
    RESUME_P_OBJDIALOG(86, 2),
    OPOBJE(87, 6),
    OPPLAYER4(88, 3),
    OPHELD3(89, 8),
    IF1_BUTTON1(90, 8),
    OPHELD2(91, 8),
    OPPLAYER2(92, 3),
    EVENT_KEYBOARD(93, -2),
    RESUME_PAUSEBUTTON(94, 6),
    OPLOCU(95, 15),
    OPPLAYER3(96, 3),
    MOVE_GAMECLICK(97, -1),
    EXIT_FREECAM(98, 0),
    field3164(99, 22),
    CLOSE_MODAL(100, 0),
    OPOBJ2(101, 7),
    IF_BUTTON9(102, 8),
    OPHELD1(103, 8),
    OPOBJ1(104, 7),
    OPLOCT(105, 15),
    SEND_SNAPSHOT(106, -1),
    OPNPCT(107, 11);

    private static final ClientProt[] values = values();
    private static final Int2ObjectMap<ClientProt> valueMap = new Int2ObjectOpenHashMap<>(values.length);

    static {
        for (final ClientProt constants : values()) {
            if (valueMap.put(constants.opcode, constants) != null) {
                throw new RuntimeException("OVERLAPPING OPCODE: " + constants.opcode);
            }
        }
    }

    private final int opcode;
    private final int size;

    ClientProt(final int opcode, final int size) {
        this.opcode = opcode;
        this.size = size;
    }

    public static ClientProt get(final int opcode) {
        return valueMap.get(opcode);
    }

    /**
     * Gets the size of the packet.
     *
     * @param opcode the packet id.
     * @return size of the packet.
     * @throws IllegalStateException if the packet doesn't exist, throws illegal state exception.
     */
    public static int getSize(final int opcode) throws IllegalStateException {
        final ClientProt packet = valueMap.get(opcode);
        if (packet == null) {
            throw new IllegalStateException("Illegal opcode: " + opcode);
        }
        return packet.getSize();
    }

    public int getOpcode() {
        return this.opcode;
    }

    public int getSize() {
        return this.size;
    }

}
