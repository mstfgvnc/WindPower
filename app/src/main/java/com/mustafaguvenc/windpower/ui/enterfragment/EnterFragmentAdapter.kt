package com.mustafaguvenc.windpower.ui.enterfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mustafaguvenc.windpower.R
import com.mustafaguvenc.windpower.databinding.ItemTurbinesBinding
import com.mustafaguvenc.windpower.model.TurbineModel
import javax.inject.Inject

class EnterFragmentAdapter
@Inject constructor (val turbineList : ArrayList<TurbineModel>)
    : RecyclerView.Adapter<EnterFragmentAdapter.EnterFragmentViewHolder>(){


    class EnterFragmentViewHolder(var view : ItemTurbinesBinding) : RecyclerView.ViewHolder(view.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnterFragmentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemTurbinesBinding>(inflater,
            R.layout.item_turbines,parent,false)
        return EnterFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnterFragmentViewHolder, position: Int) {
        holder.view.turbines=turbineList[position]
        holder.view.turbineName.text="Türbin - "+ (position+1).toString()
    }

    override fun getItemCount(): Int {
        return turbineList.size
    }

    fun updateTurbineList(newTurbineList: List<TurbineModel >) {

        turbineList.clear()
        turbineList.addAll(newTurbineList)
        notifyDataSetChanged()
    }
}