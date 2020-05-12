package linc.com.getme.domain.utils

import java.util.*

internal class StateManager {

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