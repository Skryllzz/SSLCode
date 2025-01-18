package org.jire.zenytersps.idtransform

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.ArrayCreationExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.visitor.ModifierVisitor
import com.github.javaparser.ast.visitor.Visitable
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration
import com.github.javaparser.resolution.types.ResolvedReferenceType
import com.zenyte.game.world.entity.npc.NPC
import com.zenyte.game.world.entity.npc.NpcId
import com.zenyte.game.world.entity.npc.NullNpcID
import com.zenyte.game.world.`object`.NullObjectID
import com.zenyte.game.world.`object`.ObjectId
import com.zenyte.game.world.`object`.WorldObject
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.jire.zenytersps.idtransform.NPCIDTransform.npc
import org.jire.zenytersps.idtransform.ObjectIDTransform.obj
import kotlin.jvm.optionals.getOrNull

/**
 * @author Jire
 */
class IDTransformModifierVisitor(
    val cu: CompilationUnit,

    val npcIDToVars: Int2ObjectMap<String>,
    val saveNPC: ObjectSet<CompilationUnit>,

    val objIDToVars: Int2ObjectMap<String>,
    val saveObj: ObjectSet<CompilationUnit>,

    val worldObjectType: ResolvedReferenceType,
    val ionDeclr: ResolvedReferenceTypeDeclaration,
) : ModifierVisitor<Void>() {

    fun npcVarName(oldID: Int): String? {
        val new = npc(oldID)?.id ?: return null
        //if (oldID == new) return null
        val varName = npcIDToVars.get(new)
            ?: return new.toString()
        val idClass = if (varName.startsWith("NULL"))
            NullNpcID::class
        else NpcId::class
        return "${idClass.simpleName}.$varName"
    }

    fun objVarName(oldID: Int): String? {
        val new = obj(oldID)?.id ?: return null
        //if (oldID == new) return null
        val varName = objIDToVars.get(new)
            ?: return new.toString()
        val idClass = if (varName.startsWith("NULL"))
            NullObjectID::class
        else ObjectId::class
        return "${idClass.simpleName}.$varName"
    }

    override fun visit(n: ObjectCreationExpr, arg: Void?): Visitable {
        if (n.type.isClassOrInterfaceType
            && worldObjectType.isAssignableBy(n.type.resolve())
        ) {
            n.arguments.firstOrNull()?.ifIntegerLiteralExpr { intLiteral ->
                val oldID = intLiteral.asNumber().toInt()
                val varName = objVarName(oldID) ?: return@ifIntegerLiteralExpr
                val before = intLiteral.toString()
                intLiteral.value = varName
                println("changed world object OBJ \"$before\" to \"$intLiteral\"")
                saveObj.add(cu)
            }
        }
        return super.visit(n, arg)
    }

    override fun visit(n: MethodDeclaration, arg: Void?): Visitable {
        if (n.type.isArrayType && n.nameAsString == "getNPCs") {
            val at = n.type.asArrayType()
            if (at.elementType.isPrimitiveType) {
                n.body.ifPresent { block ->
                    block.findAll(ArrayCreationExpr::class.java) {
                        it.elementType.isPrimitiveType
                    }.forEach { ace ->
                        //println(ace)
                        ace.initializer.ifPresent { aie ->
                            aie.values.filter { it.isIntegerLiteralExpr }.forEach expr@{ expr ->
                                val intLiteral = expr.asIntegerLiteralExpr()
                                val oldID = intLiteral.asNumber().toInt()
                                val varName = npcVarName(oldID) ?: return@expr
                                val before = expr.toString()
                                intLiteral.value = varName
                                println("changed NPC \"$before\" to \"$expr\"")
                                saveNPC.add(cu)
                            }
                        }
                    }
                }
            }
        } else if (n.type.isArrayType && n.nameAsString == "getObjects") {
            val at = n.type.asArrayType()
            if (at.elementType.isClassOrInterfaceType) {
                n.body.ifPresent { block ->
                    var isNpc = false
                    n.parentNode.ifPresent { parent ->
                        if (parent is ClassOrInterfaceDeclaration
                            && parent.resolve().canBeAssignedTo(ionDeclr)
                        ) {
                            isNpc = true
                        }
                    }

                    block.findAll(ArrayCreationExpr::class.java) {
                        it.elementType.isClassOrInterfaceType
                    }.forEach { ace ->
                        ace.initializer.ifPresent { aie ->
                            aie.values.filter { it.isIntegerLiteralExpr }.forEach expr@{ expr ->
                                val intLiteral = expr.asIntegerLiteralExpr()
                                val oldID = intLiteral.asNumber().toInt()
                                val varName = (if (isNpc) npcVarName(oldID) else objVarName(oldID))
                                    ?: return@expr
                                val before = expr.toString()
                                intLiteral.value = varName
                                println("changed ${if (isNpc) "NPC" else "OBJ"} \"$before\" to \"$expr\" parent is ${(n.parentNode.getOrNull() as? ClassOrInterfaceDeclaration)?.fullyQualifiedName?.getOrNull()}")
                                (if (isNpc) saveNPC else saveObj).add(cu)
                            }
                        }
                    }
                }
            }
        }
        return super.visit(n, arg)
    }

    override fun visit(n: IfStmt, arg: Void?): Visitable {
        if (n.condition.isBinaryExpr) {
            val be = n.condition.asBinaryExpr()
            if (be.operator == BinaryExpr.Operator.EQUALS || be.operator == BinaryExpr.Operator.NOT_EQUALS) {
                if (be.left.isMethodCallExpr && be.right.isIntegerLiteralExpr) {
                    val le = be.left.asMethodCallExpr()
                    if ("getId" == le.nameAsString && le.arguments.isEmpty()) {
                        val declaringType = le.resolve().declaringType()
                        if (declaringType.isClass) {
                            val dtC = declaringType.asClass()
                            if (dtC.qualifiedName == NPC::class.java.name) {
                                val intLiteral = be.right.asIntegerLiteralExpr()
                                val oldID = intLiteral.asNumber().toInt()
                                val varName = npcVarName(oldID) ?: return super.visit(n, arg)
                                val before = intLiteral.toString()
                                intLiteral.value = varName
                                println("IF-STMT changed NPC \"$before\" to \"$intLiteral\"")
                                saveNPC.add(cu)
                            } else if (dtC.qualifiedName == WorldObject::class.java.name) {
                                val intLiteral = be.right.asIntegerLiteralExpr()
                                val oldID = intLiteral.asNumber().toInt()
                                val varName = objVarName(oldID) ?: return super.visit(n, arg)
                                val before = intLiteral.toString()
                                intLiteral.value = varName
                                println("IF-STMT changed obj \"$before\" to \"$intLiteral\"")
                                saveObj.add(cu)
                            }
                        }
                    }
                }
                //println("left=${be.left}(${be.left.javaClass}), right=${be.right}(${be.right.javaClass})")
            }
        }
        return super.visit(n, arg)
    }

}