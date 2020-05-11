package linc.com.getme.utils

import java.util.*

class StateManager {

    private val currentPathState = Stack<String>().apply {
        push("root")
    }

    fun goTo(path: String) {
        currentPathState.push(path)
    }

    fun goBack() {
        currentPathState.pop()
    }

    fun getLast(): String = currentPathState.peek()

    fun hasState(): Boolean = !currentPathState.empty()

    fun clear() = currentPathState.clear()

    fun reverse() = currentPathState.reverse()

}