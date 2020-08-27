package com.joron.parkingmanager.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Joro on 27/08/2020
 */
class EmptyRecyclerView(context: Context, attributeSet: AttributeSet) :
    RecyclerView(context, attributeSet) {
    var emptyView: View? = null
        set(value) {
            field = value
            initEmptyView()
        }

    private fun initEmptyView() = emptyView?.let {
        emptyView?.visibility =
            if ((adapter == null || adapter?.itemCount == 0)) View.VISIBLE else View.GONE
        this.visibility =
            if ((adapter != null || adapter?.itemCount ?: 0 > 0)) View.VISIBLE else View.GONE
    }

    protected val dataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            initEmptyView()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter = getAdapter()
        super.setAdapter(adapter)
        oldAdapter?.unregisterAdapterDataObserver(dataObserver)
        adapter?.registerAdapterDataObserver(dataObserver)
    }

}