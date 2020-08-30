package com.joron.parkingmanager.util

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Joro on 30/08/2020
 */
class NotifiableList<E, ViewHolder : RecyclerView.ViewHolder?>(private val adapter: RecyclerView.Adapter<ViewHolder>) : ArrayList<E>() {

    override fun addAll(elements: Collection<E>): Boolean {
        clear()
        return super.addAll(elements)
    }

    fun addAndNotify(elements: Collection<E>) {
        addAll(elements)
        adapter.notifyDataSetChanged()
    }

}