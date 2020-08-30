package com.joron.parkingmanager.util

/**
 * Created by Joro on 30/08/2020
 */
class CleanableArrayList<E> : ArrayList<E>() {

    override fun addAll(elements: Collection<E>): Boolean {
        clear()
        return super.addAll(elements)
    }

}