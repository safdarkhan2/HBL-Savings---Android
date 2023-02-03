package com.hbl.amc.ui

import android.content.Context
import android.graphics.Color
import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.R
import com.hbl.amc.databinding.ExpandableChildItemBinding
import com.hbl.amc.databinding.ExpandableParentItemBinding

class RequirementsExpandableAdapter(var context: Context, var requirementsModelList:MutableList<ReqExpandableModel>) :  RecyclerView.Adapter<RequirementsExpandableAdapter.BaseViewHolder>() {
    private var isFirstItemExpanded : Boolean = true
    private var actionLock = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            ReqExpandableModel.PARENT -> {
                val itemBinding =
                    ExpandableParentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RequirementParentViewHolder(itemBinding)
            }

            ReqExpandableModel.CHILD -> {
                val itemBinding =
                    ExpandableChildItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RequirementChildViewHolder(itemBinding)
            }

            else -> {
                val itemBinding =
                    ExpandableParentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RequirementParentViewHolder(itemBinding)
            }
        }
    }

    override fun getItemCount(): Int = requirementsModelList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val row = requirementsModelList[position]
        when(row.type){
            ReqExpandableModel.PARENT -> {
                holder.bind(row)
            }
            ReqExpandableModel.CHILD -> {
                holder.bind(row)
            }
        }

    }


    override fun getItemViewType(position: Int): Int = requirementsModelList[position].type

    private fun expandRow(holder: RecyclerView.ViewHolder){
        if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
            val row = requirementsModelList[holder.bindingAdapterPosition]
            var nextPosition = holder.bindingAdapterPosition
            when (row.type) {
                ReqExpandableModel.PARENT -> {
                    for (child in row.requirementParent.subRequirements) {
                        requirementsModelList.add(
                            ++nextPosition,
                            ReqExpandableModel(ReqExpandableModel.CHILD, child)
                        )
                    }
                    notifyDataSetChanged()
                }
                ReqExpandableModel.CHILD -> {
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun collapseRow(holder: RecyclerView.ViewHolder){
        if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
            val row = requirementsModelList[holder.bindingAdapterPosition]
            var nextPosition = holder.bindingAdapterPosition + 1
            when (row.type) {
                ReqExpandableModel.PARENT -> {
                    outerloop@ while (true) {
                        //  println("Next Position during Collapse $nextPosition size is ${shelfModelList.size} and parent is ${shelfModelList[nextPosition].type}")

                        if (nextPosition == requirementsModelList.size || requirementsModelList[nextPosition].type == ReqExpandableModel.PARENT) {
                            /* println("Inside break $nextPosition and size is ${closedShelfModelList.size}")
                         closedShelfModelList[closedShelfModelList.size-1].isExpanded = false
                         println("Modified closedShelfModelList ${closedShelfModelList.size}")*/
                            break@outerloop
                        }

                        requirementsModelList.removeAt(nextPosition)
                    }

                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class RequirementParentViewHolder(private val itemBinding : ExpandableParentItemBinding) : BaseViewHolder(itemBinding.root) {
    override fun bind (row : ReqExpandableModel) {
        itemBinding.requirementTv.text = row.requirementParent.requirement
        itemBinding.countTv.text = row.requirementParent.id.toString()
        itemBinding.mainReqLayout.setOnClickListener {
            if (row.isExpanded) {
                row.isExpanded = false
                itemBinding.arrow.setImageResource(R.drawable.ic_arrow_down)
                collapseRow(this)
//                        holder.layout.setBackgroundColor(Color.WHITE)


            }else{
//                holder.layout.setBackgroundColor(Color.GRAY)
                row.isExpanded = true
                itemBinding.arrow.setImageResource(R.drawable.ic_arrow_up)
//                holder.upArrowImg.visibility = View.VISIBLE
//                holder.closeImage.visibility = View.GONE
                expandRow(this)
            }
        }
        /*holder.upArrowImg.setOnClickListener{
            if(row.isExpanded){
                row.isExpanded = false
                collapseRow(position)
                holder.layout.setBackgroundColor(Color.WHITE)
                holder.upArrowImg.visibility = View.GONE
                holder.closeImage.visibility = View.VISIBLE

            }
        }*/
    }
    /*internal var layout = itemView.country_item_parent_container
        internal var countryName : TextView = itemView.country_name
        internal var closeImage = itemView.close_arrow
        internal var upArrowImg = itemView.up_arrow*/

    }

    inner class RequirementChildViewHolder(private val itemBinding: ExpandableChildItemBinding) :
        BaseViewHolder(itemBinding.root) {
        //        internal var layout = itemView.country_item_child_container
//        internal var stateName : TextView = itemView.state_name
//        internal var capitalImage = itemView.capital_name
        override fun bind(row: ReqExpandableModel) {
            itemBinding.subRequirementTv.text = row.requirementChild
        }

    }

    abstract inner class BaseViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(row: ReqExpandableModel)
    }
}