package linc.com.getme.domain.utils

import java.util.*

internal class StateManager {

    private val currentPathState = Stack<String>().apply {
        println("ROOT_PUSH")
        push("root")
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

    fun copyState(states: Stack<String>) {
        currentPathState.apply {
            clear()
            addAll(states)
        }
    }

}