package lambda

internal object TestUtils {
    internal fun readResourceFile(path: String) =
        this::class.java.getResource("/$path")
            ?.readText(charset = Charsets.UTF_8) ?: error("Did not find resource: $path")
}
