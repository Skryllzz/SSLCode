package org.jire.zenytersps.idtransform

import com.github.javaparser.ParserConfiguration
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.visitor.ModifierVisitor
import com.github.javaparser.ast.visitor.Visitable
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration
import com.github.javaparser.resolution.model.typesystem.ReferenceTypeImpl
import com.github.javaparser.resolution.types.ResolvedReferenceType
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import com.github.javaparser.utils.CodeGenerationUtils
import com.github.javaparser.utils.SourceRoot
import com.zenyte.game.item.ItemOnNPCAction
import com.zenyte.game.world.entity.npc.NpcId
import com.zenyte.game.world.entity.npc.NullNpcID
import com.zenyte.game.world.`object`.NullObjectID
import com.zenyte.game.world.`object`.ObjectId
import com.zenyte.game.world.`object`.WorldObject
import com.zenyte.plugins.PluginManager
import com.zenyte.plugins.renewednpc.ShopNPCHandler
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.runelite.cache.fs.Store
import org.jire.zenytersps.idtransform.NPCIDTransform.new
import org.jire.zenytersps.idtransform.NPCIDTransform.oldNPCs
import org.jire.zenytersps.idtransform.ObjectIDTransform.new
import org.jire.zenytersps.idtransform.ObjectIDTransform.newObjects
import org.jire.zenytersps.idtransform.ObjectIDTransform.obj
import org.jire.zenytersps.idtransform.ObjectIDTransform.oldObjects
import java.io.File

/**
 * @author Jire
 */
object IDTransform {

    val old by lazy {
        Store(File("data/cache-179")).apply { load() }
    }

    val new by lazy {
        Store(File("data/cache-211")).apply { load() }
    }

    @JvmStatic
    fun idToVars(targetClass: Class<*>): Int2ObjectMap<String> {
        val packageName = targetClass.packageName
        val className = targetClass.simpleName
        val classFileName = "${className}.java"

        val map: Int2ObjectMap<String> = Int2ObjectOpenHashMap()

        val sr =
            SourceRoot(CodeGenerationUtils.mavenModuleRoot(targetClass).resolve("../../src/main/java/").normalize())
        val cu = sr.parse(packageName, classFileName)

        val requiredKeywords = listOf(Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC, Modifier.Keyword.FINAL)

        cu.accept(object : ModifierVisitor<Void>() {
            override fun visit(n: FieldDeclaration, arg: Void?): Visitable {
                val mods = n.modifiers
                val keywords = mods.map { it.keyword }
                for (keyword in requiredKeywords)
                    if (!keywords.contains(keyword))
                        return super.visit(n, arg)

                val vars = n.variables
                if (vars.size == 1) {
                    val v = vars[0]
                    v.initializer.ifPresent {
                        if (it.isIntegerLiteralExpr) {
                            val intExpr = it.asIntegerLiteralExpr()
                            val int = intExpr.asNumber().toInt()
                            map.put(int, v.nameAsString)
                        }
                    }
                }
                return super.visit(n, arg)
            }
        }, null)

        return map
    }

    fun transform() {
        val srPath = CodeGenerationUtils
            .mavenModuleRoot(PluginManager::class.java)
            .resolve("../../src/main/java/").normalize()

        val reflectionTypeSolver = ReflectionTypeSolver(false)
        /*val tr = CombinedTypeSolver().apply {
            add(reflectionTypeSolver)
            add(JavaParserTypeSolver(srPath))
        }*/
        val jsr = JavaSymbolSolver(reflectionTypeSolver/*tr*/)
        StaticJavaParser.getParserConfiguration().setSymbolResolver(jsr)

        val sr = SourceRoot(srPath).apply {
            parserConfiguration.setSymbolResolver(jsr)
            parserConfiguration.languageLevel = ParserConfiguration.LanguageLevel.JAVA_17
        }

        val woCu = sr.parse("com.zenyte.game.world.object", "${WorldObject::class.simpleName}.java")
        val worldObjectType: ResolvedReferenceType = ReferenceTypeImpl(woCu.primaryType.get().resolve())

        val ionCu = sr.parse("com.zenyte.game.item", "${ItemOnNPCAction::class.simpleName}.java")
        val ionDeclr: ResolvedReferenceTypeDeclaration =
            ionCu.getInterfaceByName(ItemOnNPCAction::class.simpleName).get().resolve()
        //transformShops(sr, npcIDToVars)

        val results = sr.tryToParseParallelized("")

        val npcIDToVars = idToVars(NullNpcID::class.java)
        npcIDToVars.putAll(idToVars(NpcId::class.java))
        val objIDToVars = idToVars(NullObjectID::class.java)
        objIDToVars.putAll(idToVars(ObjectId::class.java))

        val saveNPC: ObjectSet<CompilationUnit> = ObjectOpenHashSet(results.size)
        val saveObj: ObjectSet<CompilationUnit> = ObjectOpenHashSet(results.size)

        for (result in results) result.ifSuccessful { cu ->
            val visitor = IDTransformModifierVisitor(
                cu,
                npcIDToVars, saveNPC,
                objIDToVars, saveObj,
                worldObjectType, ionDeclr
            )
            cu.accept(visitor, null)
        }

        //sr.saveAll()
        for (cu in saveNPC) cu.storage.ifPresent {
            cu.addImport(NpcId::class.java)
            cu.addImport(NullNpcID::class.java)
            it.save()
        }
        for (cu in saveObj) cu.storage.ifPresent {
            cu.addImport(ObjectId::class.java)
            cu.addImport(NullObjectID::class.java)
            it.save()
        }
    }

    fun transformShops(
        sr: SourceRoot, npcIDToVars: Int2ObjectMap<String>,
        startPackage: String = ShopNPCHandler::class.java.packageName,
        fileName: String = "${ShopNPCHandler::class.java.simpleName}.java"
    ) = sr.tryToParse(startPackage, fileName).ifSuccessful { cu ->
        val visitor = ShopIDTransformModifierVisitor(npcIDToVars)
        cu.accept(visitor, null)
        cu.storage.ifPresent {
            cu.addImport(NpcId::class.java)
            it.save()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        if (false) {
            oldObjects.objects.filter { it.name.contains("Kings") }.forEach { println(it) }
            println("bald")
            newObjects.objects.filter { it.name.contains("Kings") }.forEach { println(it) }

            println("bro")

            println(obj(10230))
            /*println(oldObjects.getObject(3831).name)
            println(newObjects.getObject(40421).name)
            println(newObjects.getObject(3831).name)
            println(obj(10230)?.name)*/
            return
        }
        if (true) {
            transform()
            return
        }

        var matches = 0

        for (oldNPC in oldNPCs.npcs) {
            @Suppress("UNUSED_VARIABLE") val newNPC = oldNPC.new() ?: continue
            //println("Matched ${oldNPC.id} (\"${oldNPC.name}\") with ${newNPC.id} (\"${newNPC.name}\")")
            matches++
        }

        println("NPCS matched $matches / ${oldNPCs.npcs.size}")
        matches = 0

        for (oldObject in oldObjects.objects) {
            @Suppress("UNUSED_VARIABLE") val newObject = oldObject.new() ?: continue
            //println("Matched ${oldObject.id} (\"${oldObject.name}\") with ${newObject.id} (\"${newObject.name}\")")
            matches++
        }

        println("Objects matched $matches / ${oldObjects.objects.size}")
    }

    fun Array<String?>.matches(other: Array<String?>): Boolean {
        if (contentEquals(other)) return true
        val reduced = reduce()
        val otherReduced = other.reduce()
        return reduced.toTypedArray().contentEquals(otherReduced.toTypedArray())
        /*        println("reduced=${reduced.joinToString(",")} VS other=${otherReduced.joinToString(",")}")
                return if (reduced.size < otherReduced.size)
                    otherReduced.containsAll(reduced)
                else reduced.containsAll(otherReduced)*/
    }

    fun Array<String?>.reduce() = mapNotNull {
        it?.replace("-", "")
            ?.replace("'", "")
            ?.replace(" ", "")
            ?.lowercase()
    }

}