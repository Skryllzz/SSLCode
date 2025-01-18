package com.zenyte.game.packet;

import com.zenyte.Constants;

/**
 * @author Tommeh | 28 jul. 2018 | 14:10:53
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public enum ServerProt {
    REBUILD_NORMAL(0, -2),
    LOC_DEL(1, 2),
    PLAYER_INFO(2, -2),
    field3195(3, -2),
    CAM_MOVETO(4, 6),
    UPDATE_RUNWEIGHT(5, 2),
    CAM_LOOKAT(6, 6),
    NPC_INFO_SMALL_VIEWPORT_2(7, -2),
    RESET_CLIENT_VARCACHE(8, 0),
    NPC_INFO_SMALL(9, -2),
    GROUND_OBJECT_OPTION_FLAGS(10, 4),
    CAM_RESET(11, 0),
    UPDATE_ZONE_FULL_FOLLOWS(12, 2),
    UPDATE_ZONE_PARTIAL_FOLLOWS(13, 2),
    field3273(14, 0),
    REFLECTION_CHECKER(15, -2),
    CAM_SHAKE(16, 4),
    TRADING_POST_UPDATE(17, 20),
    RUNCLIENTSCRIPT(18, -2),
    UPDATE_INV_STOP_TRANSMIT(19, 2),
    TRADING_POST_RESULTS(20, -2),
    UPDATE_FRIENDCHAT_CHANNEL_FULL_NEW(21, -2),
    CHAT_FILTER_SETTINGS_PRIVATECHAT(22, 1),
    LOC_ADD_CHANGE(23, 5),
    OBJ_DEL(24, 7),
    MESSAGE_PRIVATE(25, -2),
    VARP_LARGE(26, 6),
    UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER(27, -1),
    field3220(28, 9),
    CLANSETTINGS_DELTA(29, -2),
    REBUILD_REGION(30, -2),
    OBJ_ADD(31, 14),
    MESSAGE_GAME(32, -1),
    OPEN_URL(33, -2),
    LOGOUT(34, 1),
    MAP_ANIM(35, 6),
    field3200(36, 0),
    UPDATE_STAT(37, 6),
    OBJ_COUNT(38, 7),
    MIDI_SONG(39, 2),
    IF_SETPOSITION(40, 8),
    MAPPROJ_ANIM(41, 15),
    PROJANIM_SPECIFIC(42, 17),
    TOGGLE_OCULUS_ORB(43, 4),
    UPDATE_INV_FULL(44, -2),
    SYNTH_SOUND(45, 5),
    IF_SETEVENTS(46, 12),
    IF_SETOBJECT(47, 10),
    IF_OPENTOP(48, 2),
    field3248(49, 0),
    MIDI_JINGLE(50, 5),
    UPDATE_REBOOT_TIMER(51, 2),
    CLANSETTINGS_FULL(52, -2),
    NPC_INFO_LARGE_VIEWPORT_2(53, -2),
    UPDATE_INV_CLEAR(54, 4),
    AREA_SOUND(55, 5),
    SET_PLAYER_OP(56, -1),
    LOGOUT_FULL(57, 0),
    IF_SETCOLOR(58, 6),
    GRAPHICSOBJECT_SPAWN(59, 8),
    IF_SETPLAYERHEAD(60, 4),
    UPDATE_SITESETTINGS(61, -1),
    NPC_SPOTANIM(62, 8),
    NPC_INFO_LARGE(63, -2),
    IF_CLOSESUB(64, 4),
    MINIMAP_TOGGLE(65, 1),
    IF_SETANGLE(66, 10),
    IF_SETMODEL(67, 6),
    LOGOUT_TRANSFER(68, -1),
    SEND_PING(69, 8),
    UPDATE_INV_PARTIAL(70, -2),
    TRIGGER_ONDIALOGABORT(71, 0),
    CLANCHANNEL_FULL(72, -2),
    UPDATE_FRIENDLIST(73, -2),
    UPDATE_UID192(74, 28),
    UPDATE_RUNENERGY(75, 2),
    CLANCHANNEL_DELTA(76, -2),
    field3269(77, 6),
    HINT_ARROW(78, 6),
    SYNC_CLIENT_VARCACHE(79, 0),
    IF1_MODELROTATE(80, 8),
    MESSAGE_CLANCHANNEL_SYSTEM(81, -1),
    UPDATE_FRIENDCHAT_CHANNEL_FULL(82, -2),
    IF_SETTEXT(83, -2),
    MESSAGE_CLANCHANNEL(84, -1),
    HEAT_MAP(85, 1),
    MESSAGE_PRIVATE_ECHO(86, -2),
    field3225(87, -1),
    IF_SETANIM(88, 6),
    IF_OPENSUB(89, 7),
    SET_MAP_FLAG(90, 2),
    NPC_SET_SEQUENCE(91, 5),
    CHAT_FILTER_SETTINGS(92, 2),
    field3285(93, -2),
    PLAYER_SPOTANIM(94, 8),
    ENTER_FREECAM(95, 1),
    UPDATE_IGNORELIST(96, -2),
    VARP_SMALL(97, 3),
    DYNAMICOBJECT_SPAWN(98, 6),
    RESET_ANIMS(99, 0),
    IF_SETSCROLLPOS(100, 6),
    LOC_COMBINE(101, 14),
    IF_SETHIDE(102, 5),
    field3295(103, 4),
    IF_SETNPCHEAD(104, 6),
    UPDATE_ZONE_PARTIAL_ENCLOSED(105, -2),
    field3298(106, 17),
    field3299(107, 16),
    FRIENDLIST_LOADED(108, 0),
    LOC_ANIM(109, 4),
    IF_MOVESUB(110, 8),
    MESSAGE_FRIENDCHANNEL(111, -1);

    private final int opcode;
    private final int size;

    ServerProt(final int opcode, final int size) {
        this.opcode = opcode;
        this.size = size;
    }

    public int getInitialSize() {
        return size >= 0 ? size : 16;
    }

    public int getCapacity() {
        return size >= 0 ? size : size == -1 ? 255 : Constants.MAX_SERVER_BUFFER_SIZE;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public int getSize() {
        return this.size;
    }
}
