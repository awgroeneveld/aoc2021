import java.net.URI

fun loadResource(path: String): URI {
    val resource = Thread.currentThread().contextClassLoader.getResource(path).toURI()
    requireNotNull(resource) { "Resource $path not found" }
    return resource
}
