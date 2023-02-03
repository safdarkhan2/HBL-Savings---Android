package com.hbl.amc.ui.FundPrices

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.R
import com.hbl.amc.databinding.FundPricesChildRecyclerItemBinding
import com.hbl.amc.databinding.FundPricesParentRecyclerItemBinding
import com.hbl.amc.ui.Notification.Notification
import com.hbl.amc.ui.Notification.NotificationAdapter
import com.hbl.amc.ui.Notification.NotificationHeader
import com.hbl.amc.ui.Notification.NotificationHeaderAdapter

class FundPricesExpandableAdapter(
    var context: Context,
    var requirementsModelList: MutableList<FundPricesExpandableModel>
) : RecyclerView.Adapter<FundPricesExpandableAdapter.BaseViewHolder>() {
    private var isFirstItemExpanded: Boolean = true
    private var actionLock = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            FundPricesExpandableModel.PARENT -> {
                val itemBinding =
                    FundPricesParentRecyclerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                FundPricesParentViewHolder(itemBinding)
            }

            FundPricesExpandableModel.CHILD -> {
                val itemBinding =
                    FundPricesChildRecyclerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                FundPricesChildViewHolder(itemBinding, parent.context)
            }

            else -> {
                val itemBinding =
                    FundPricesParentRecyclerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                FundPricesParentViewHolder(itemBinding)
            }
        }
    }

    override fun getItemCount(): Int = requirementsModelList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val row = requirementsModelList[position]
        when (row.type) {
            FundPricesExpandableModel.PARENT -> {
                holder.bind(row)
            }
            FundPricesExpandableModel.CHILD -> {
                holder.bind(row)
            }
        }

    }


    override fun getItemViewType(position: Int): Int = requirementsModelList[position].type

    private fun expandRow(holder: RecyclerView.ViewHolder) {
        if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
            val row = requirementsModelList[holder.bindingAdapterPosition]
            var nextPosition = holder.bindingAdapterPosition
            when (row.type) {
                FundPricesExpandableModel.PARENT -> {
                    for (child in row.requirementParent.fundsPricesDetail) {
                        requirementsModelList.add(
                            ++nextPosition,
                            FundPricesExpandableModel(FundPricesExpandableModel.CHILD, child)
                        )
                    }
                    notifyDataSetChanged()
                }
                FundPricesExpandableModel.CHILD -> {
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun collapseRow(holder: RecyclerView.ViewHolder) {
        if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
            val row = requirementsModelList[holder.bindingAdapterPosition]
            var nextPosition = holder.bindingAdapterPosition + 1
            when (row.type) {
                FundPricesExpandableModel.PARENT -> {
                    outerloop@ while (true) {
                        //  println("Next Position during Collapse $nextPosition size is ${shelfModelList.size} and parent is ${shelfModelList[nextPosition].type}")

                        if (nextPosition == requirementsModelList.size || requirementsModelList[nextPosition].type == FundPricesExpandableModel.PARENT) {
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

    inner class FundPricesParentViewHolder(private val itemBinding: FundPricesParentRecyclerItemBinding) :
        BaseViewHolder(itemBinding.root) {
        override fun bind(row: FundPricesExpandableModel) {
            itemBinding.fundPriceTitle.text = row.requirementParent.title
            itemBinding.mainFundPricesItemLayout.setOnClickListener {
                if (row.isExpanded) {
                    row.isExpanded = false
                    itemBinding.arrow.setImageResource(R.drawable.ic_arrow_down)
                    collapseRow(this)
//                        holder.layout.setBackgroundColor(Color.WHITE)


                } else {
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

    inner class FundPricesChildViewHolder(private val itemBinding: FundPricesChildRecyclerItemBinding, private val context: Context) :
        BaseViewHolder(itemBinding.root) {
        //        internal var layout = itemView.country_item_child_container
//        internal var stateName : TextView = itemView.state_name
//        internal var capitalImage = itemView.capital_name
        override fun bind(row: FundPricesExpandableModel) {
            itemBinding.recyclerview.layoutManager =
                LinearLayoutManager(context)
            val adapter = NotificationHeaderAdapter()
            itemBinding.recyclerview.adapter = adapter
           // addDummyData()
            adapter.updateData(addDummyData(context))

            //TODO bind inner recyclerview with other adapter
        }

    }

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(row: FundPricesExpandableModel)
    }

    fun addDummyData(context: Context): List<NotificationHeader>{
        var list = mutableListOf<NotificationHeader>()
        var notificationList = mutableListOf<Notification>()
        notificationList.add(Notification(1, "just now", "Fixed Income Fund", context.getString(R.string.dummy_text)))
        notificationList.add(Notification(2, "1 min ago", "Fixed Income Fund", context.getString(R.string.dummy_text)))
        notificationList.add(Notification(3, "1 hour ago", "Fixed Income Fund", context.getString(R.string.dummy_text)))
        list.add(NotificationHeader(1, "Today", notificationList))
        list.add(NotificationHeader(1, "2 September 2021", notificationList))
        list.add(NotificationHeader(1, "28 August 2021", notificationList))
        list.add(NotificationHeader(1, "25 August 2021", notificationList))
        return list
    }
}