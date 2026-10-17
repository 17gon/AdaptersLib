package net.craftoriya.adaptersLib.model.command

import net.craftoriya.adaptersLib.model.math.Vec3I

enum class ArgKind { LITERAL, VALUE, X, Y, Z }

class CommandNode(
    val name: String,
    val kind: ArgKind,
    val values: () -> Collection<String>,
    val children: List<CommandNode>
)

data class CommandContext(val target: Vec3I?, val position: Vec3I?)

class CommandSpec(private val roots: List<CommandNode>) {
    fun suggest(args: List<String>, ctx: CommandContext): List<String> {
        if (args.isEmpty()) return emptyList()
        var level = roots
        for (a in args.dropLast(1)) {
            val node = level.firstOrNull { it.kind == ArgKind.LITERAL && it.name.equals(a, ignoreCase = true) }
                ?: level.firstOrNull { it.kind != ArgKind.LITERAL }
                ?: return emptyList()
            level = node.children
        }
        val prefix = args.last()
        val out = LinkedHashSet<String>()
        val at = ctx.target ?: ctx.position
        for (n in level) {
            when (n.kind) {
                ArgKind.LITERAL -> out += n.name
                ArgKind.VALUE -> out += n.values()
                ArgKind.X -> at?.let { out += it.x.toString() }
                ArgKind.Y -> at?.let { out += it.y.toString() }
                ArgKind.Z -> at?.let { out += it.z.toString() }
            }
        }
        return out.filter { it.startsWith(prefix, ignoreCase = true) }
    }
}

class CommandBuilder {
    private val nodes = ArrayList<CommandNode>()

    fun literal(name: String, children: CommandBuilder.() -> Unit = {}) {
        nodes += CommandNode(name, ArgKind.LITERAL, { emptyList() }, CommandBuilder().apply(children).build())
    }

    fun value(name: String, values: () -> Collection<String>, children: CommandBuilder.() -> Unit = {}) {
        nodes += CommandNode(name, ArgKind.VALUE, values, CommandBuilder().apply(children).build())
    }

    fun xyz(children: CommandBuilder.() -> Unit = {}) {
        val z = CommandNode("z", ArgKind.Z, { emptyList() }, CommandBuilder().apply(children).build())
        val y = CommandNode("y", ArgKind.Y, { emptyList() }, listOf(z))
        nodes += CommandNode("x", ArgKind.X, { emptyList() }, listOf(y))
    }

    fun build(): List<CommandNode> = nodes
}

fun commandSpec(block: CommandBuilder.() -> Unit): CommandSpec = CommandSpec(CommandBuilder().apply(block).build())