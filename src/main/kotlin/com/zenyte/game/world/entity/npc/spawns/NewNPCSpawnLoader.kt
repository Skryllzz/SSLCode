package com.zenyte.game.world.entity.npc.spawns

import com.moandjiezana.toml.Toml
import com.zenyte.game.util.Direction
import com.zenyte.game.world.entity.npc.NpcId
import com.zenyte.game.world.entity.npc.NullNpcID
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import java.nio.file.Path

/**
 * @author Jire
 */
object NewNPCSpawnLoader {

    @JvmStatic
    fun load() {
        val idNames: Object2IntMap<String> = Object2IntOpenHashMap()
        for (declaredField in NpcId::class.java.declaredFields) {
            val name = declaredField.name
            val idName = "npc.${name.lowercase()}"
            idNames.put(idName, declaredField.getInt(NpcId::class.java))
        }
        for (declaredField in NullNpcID::class.java.declaredFields) {
            val name = declaredField.name
            val idName = "npc.${name.lowercase()}"
            idNames.put(idName, declaredField.getInt(NullNpcID::class.java))
        }

        Path.of("data", "npcs", "spawn").toFile()
            .listFiles()!!
            .forEach {
                val name = it.nameWithoutExtension.substringBefore(".npc.spawn")
                val nameSplit = name.split('_')
                val regionX = nameSplit[0].toInt()
                val regionY = nameSplit[1].toInt()

                val toml = Toml().read(it)
                for (entry in toml.entrySet()) {
                    val key = entry.key
                    if (key != "spawns") continue

                    val value = entry.value
                    val types: MutableList<Toml> = ObjectArrayList()

                    if (value is Toml) {
                        types.add(value)
                    } else if (value is Collection<*>) {
                        @Suppress("UNCHECKED_CAST")
                        types.addAll(value as Collection<Toml>)
                    }

                    for (type in types) {
                        val properties = type.toMap()
                        val plane = (properties["plane"] as Long).toInt()
                        val x = (properties["x"] as Long).toInt()
                        val y = (properties["y"] as Long).toInt()
                        val directionString = properties["direction"] as String
                        val noneDirection = "NONE" == directionString
                        val direction = if (noneDirection) Direction.SOUTH else Direction.valueOf(directionString)
                        val id = idNames.getInt((properties["id"] as String))
                        val radius = if (properties.containsKey("radius"))
                            (properties["radius"] as Long).toInt()
                        else if (noneDirection) 0 else 3
                        val spawn = NPCSpawn(id, x, y, plane, direction, radius)
                        NPCSpawnLoader.DEFINITIONS.add(spawn)
                    }
                }
            }
    }

    @JvmStatic
    fun main(args: Array<String>) = load()

}