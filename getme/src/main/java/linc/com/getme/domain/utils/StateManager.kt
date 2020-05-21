package linc.com.getme.domain.utils

import java.util.*

internal class StateManager {

    private val currentPathState = Stack<String>().apply {
        push(ROOT)
    }

    fun goTo(path: String) {
        currentPathState.push(path)
    }

    fun goBack() {
        currentPathState.pop()
    }

    fun getLast(): String = currentPathState.peek()

    fun getAllStates(): Stack<String> = currentPathState

    fun hasState(): Boolean = !currentPathState.empty()

    fun clear() = currentPathState.clear()

    fun reverse() = currentPathState.reverse()

    companion object {
        const val ROOT = "root"
    }

}