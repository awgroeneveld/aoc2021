import java.net.URI

fun loadResource(path: String): URI {
    val resource = Thread.currentThread().contextClassLoader.getResource(path).toURI()
    requireNotNull(resource) { "Resource $path not found" }
    return resource
}

inline fun<reified T> Pair<Int,Int>.createArray(initialValue:T) = Array(this.first){ Array(this.second){initialValue}}

