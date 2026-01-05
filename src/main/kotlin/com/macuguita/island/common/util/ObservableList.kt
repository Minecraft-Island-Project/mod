package com.macuguita.island.common.util

class ObservableList<T>(
    private val list: MutableList<T> = mutableListOf(),
    private val onChange: () -> Unit
) : MutableList<T> by list {

    override fun add(element: T): Boolean {
        val result = list.add(element)
        if (result) onChange()
        return result
    }

    override fun remove(element: T): Boolean {
        val result = list.remove(element)
        if (result) onChange()
        return result
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val result = list.addAll(elements)
        if (result) onChange()
        return result
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val result = list.removeAll(elements)
        if (result) onChange()
        return result
    }

    override fun clear() {
        if (list.isNotEmpty()) {
            list.clear()
            onChange()
        }
    }

    override fun set(index: Int, element: T): T {
        val old = list.set(index, element)
        onChange()
        return old
    }

    override fun add(index: Int, element: T) {
        list.add(index, element)
        onChange()
    }

    override fun removeAt(index: Int): T {
        val old = list.removeAt(index)
        onChange()
        return old
    }
}

