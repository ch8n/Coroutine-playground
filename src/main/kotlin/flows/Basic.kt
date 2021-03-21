package flows

class Ch8nFlow<T>(doStuff: Ch8nFlow<T>.() -> Unit) {

    var stuff: ((value: T) -> Unit)? = null
    fun collect(doStuff: (value: T) -> Unit) {
        stuff = doStuff
    }

    fun emit(value: T) {
        //this.collect(doStuff = stuff)
    }
}


fun main() {
    val flow = Ch8nFlow<Int> {
        emit(1)
    }

    flow.collect {
        println(it)
    }
}
