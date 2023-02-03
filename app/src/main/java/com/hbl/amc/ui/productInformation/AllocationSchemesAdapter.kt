package com.hbl.amc.ui.productInformation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.R
import com.hbl.amc.databinding.ItemAllocationSchemeBinding
import com.hbl.amc.domain.model.AllocationScheme
import com.hbl.amc.ui.CUSTOMIZE_ALLOCATION
import com.hbl.amc.ui.LIFECYCLE_ALLOCATION
import com.hbl.amc.utils.PerfectDecimal
import kotlin.properties.Delegates

class AllocationSchemesAdapter(
    private val context: Context,
    private val onSelectOrUnSelectAllocation: (position: Int, isSelected: Boolean) -> Unit,
    private val onSubFundChange: (position: Int, allocationScheme: AllocationScheme) -> Unit
) : RecyclerView.Adapter<AllocationSchemesAdapter.AllocationSchemesViewHolder>() {

    private var allocationSchemesList: List<AllocationScheme> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllocationSchemesViewHolder {
        val itemBinding = ItemAllocationSchemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = AllocationSchemesViewHolder(itemBinding)

        itemBinding.radioButton.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onSelectOrUnSelectAllocation.invoke(holder.adapterPosition, !allocationSchemesList[holder.adapterPosition].isSelected)
            }
        }

        itemBinding.imgVolatilityArrow.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                if (itemBinding.volatilityContainer.isVisible || itemBinding.lifecycleAllocationContainer.isVisible) {
                    itemBinding.volatilityContainer.visibility = View.GONE
                    itemBinding.lifecycleAllocationContainer.visibility = View.GONE
                    itemBinding.dividerVolatility.visibility = View.VISIBLE
                    itemBinding.imgVolatilityArrow.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    itemBinding.dividerVolatility.visibility = View.GONE
                    itemBinding.imgVolatilityArrow.setImageResource(R.drawable.ic_arrow_up)

                    if (allocationSchemesList[holder.adapterPosition].code == LIFECYCLE_ALLOCATION) {
                        itemBinding.lifecycleAllocationContainer.visibility = View.VISIBLE
                    } else {
                        itemBinding.volatilityContainer.visibility = View.VISIBLE
                    }
                }
            }
        }

        itemBinding.equitySubfundEdittext.addTextChangedListener {
            val allocationScheme = allocationSchemesList[holder.adapterPosition]
            val str: String = itemBinding.equitySubfundEdittext.text.toString()
            if (str.isEmpty()) {
                allocationScheme.equitySubFund = 0
                onSubFundChange(holder.adapterPosition, allocationScheme)
                return@addTextChangedListener
            }

            val str2 = PerfectDecimal(str, 3, 2)
            if (str2 != str) {
                itemBinding.equitySubfundEdittext.setText(str2)
                itemBinding.equitySubfundEdittext.setSelection(str2!!.length)
            }

            //error validation
            val equityFund = itemBinding.equitySubfundEdittext.text!!.toString().toInt()
            var debitFund = 0
            var marketFund = 0

            if (allocationScheme.isEditMinDebtSubFund) {
                if (itemBinding.debitSubfundEdittext.text.toString().isNotEmpty()) {
                    debitFund = itemBinding.debitSubfundEdittext.text.toString().toInt()
                }
            } else {
                debitFund = allocationScheme.minDebtSubFund
            }

            if (allocationScheme.isEditMinMoneyMarketSubFund) {
                if (itemBinding.marketSubfundEdittext.text.toString().isNotEmpty()) {
                    marketFund = itemBinding.marketSubfundEdittext.text.toString().toInt()
                }
            } else {
                marketFund = allocationScheme.minMoneyMarketSubFund
            }

            val remainingAmountForEquity = 100 - (debitFund + marketFund)

            when {
                equityFund < allocationScheme.minEquitySubFund -> {
                    itemBinding.equitySubfundEdittext.error = "Minimum amount should be ${allocationScheme.minEquitySubFund}"
                }
                equityFund > 100 -> {
                    itemBinding.equitySubfundEdittext.error = "Please enter value upto 100"
                }
                equityFund != remainingAmountForEquity -> {
//                    Toast.makeText(context, "Total of the allocations should be 100", Toast.LENGTH_LONG).show()
                }
                else -> {
                    itemBinding.equitySubfundEdittext.error = null
                }
            }

            allocationScheme.equitySubFund = equityFund
            onSubFundChange(holder.adapterPosition, allocationScheme)
        }

        itemBinding.debitSubfundEdittext.addTextChangedListener {
            val allocationScheme = allocationSchemesList[holder.adapterPosition]
            val str: String = itemBinding.debitSubfundEdittext.text.toString()
            if (str.isEmpty()) {
                allocationScheme.debitSubFund = 0
                onSubFundChange(holder.adapterPosition, allocationScheme)
                return@addTextChangedListener
            }

            val str2 = PerfectDecimal(str, 3, 2)
            if (str2 != str) {
                itemBinding.debitSubfundEdittext.setText(str2)
                itemBinding.debitSubfundEdittext.setSelection(str2!!.length)
            }

            //error validation
            var equityFund = 0
            val debitFund = itemBinding.debitSubfundEdittext.text!!.toString().toInt()
            var marketFund = 0

            if (allocationScheme.isEditMinEquitySubFund) {
                if (itemBinding.equitySubfundEdittext.text.toString().isNotEmpty()) {
                    equityFund = itemBinding.equitySubfundEdittext.text.toString().toInt()
                }
            } else {
                equityFund = allocationScheme.minEquitySubFund
            }

            if (allocationScheme.isEditMinMoneyMarketSubFund) {
                if (itemBinding.marketSubfundEdittext.text.toString().isNotEmpty()) {
                    marketFund = itemBinding.marketSubfundEdittext.text.toString().toInt()
                }
            } else {
                marketFund = allocationScheme.minMoneyMarketSubFund
            }

            val remainingAmountForDebit = 100 - (equityFund + marketFund)

            when {
                debitFund < allocationScheme.minDebtSubFund -> {
                    itemBinding.debitSubfundEdittext.error = "Minimum amount should be ${allocationScheme.minDebtSubFund}"
                }
                debitFund > 100 -> {
                    itemBinding.debitSubfundEdittext.error = "Please enter value upto 100"
                }
                debitFund != remainingAmountForDebit -> {
//                    Toast.makeText(context, "Total of the allocations should be 100", Toast.LENGTH_LONG).show()
                }
                else -> {
                    itemBinding.debitSubfundEdittext.error = null
                }
            }

            allocationScheme.debitSubFund = debitFund
            onSubFundChange(holder.adapterPosition, allocationScheme)
        }

        itemBinding.marketSubfundEdittext.addTextChangedListener {
            val allocationScheme = allocationSchemesList[holder.adapterPosition]
            val str: String = itemBinding.marketSubfundEdittext.text.toString()
            if (str.isEmpty()) {
                allocationScheme.moneyMarketSubFund = 0
                onSubFundChange(holder.adapterPosition, allocationScheme)
                return@addTextChangedListener
            }

            val str2 = PerfectDecimal(str, 3, 2)
            if (str2 != str) {
                itemBinding.marketSubfundEdittext.setText(str2)
                itemBinding.marketSubfundEdittext.setSelection(str2!!.length)
            }

            //error validation
            var equityFund = 0
            var debitFund = 0
            val marketFund = itemBinding.marketSubfundEdittext.text!!.toString().toInt()

            if (allocationScheme.isEditMinEquitySubFund) {
                if (itemBinding.equitySubfundEdittext.text.toString().isNotEmpty()) {
                    equityFund = itemBinding.equitySubfundEdittext.text.toString().toInt()
                }
            } else {
                equityFund = allocationScheme.minEquitySubFund
            }

            if (allocationScheme.isEditMinDebtSubFund) {
                if (itemBinding.debitSubfundEdittext.text.toString().isNotEmpty()) {
                    debitFund = itemBinding.debitSubfundEdittext.text.toString().toInt()
                }
            } else {
                debitFund = allocationScheme.minDebtSubFund
            }

            val remainingAmountForMarket = 100 - (equityFund + debitFund)

            when {
                marketFund < allocationScheme.minMoneyMarketSubFund -> {
                    itemBinding.marketSubfundEdittext.error = "Minimum amount should be ${allocationScheme.minMoneyMarketSubFund}"
                }
                marketFund > 100 -> {
                    itemBinding.marketSubfundEdittext.error = "Please enter value upto 100"
                }
                marketFund != remainingAmountForMarket -> {
//                    Toast.makeText(context, "Total of the allocations should be 100", Toast.LENGTH_LONG).show()
                }
                else -> {
                    itemBinding.marketSubfundEdittext.error = null
                }
            }

            allocationScheme.moneyMarketSubFund = marketFund
            onSubFundChange(holder.adapterPosition, allocationScheme)
        }

        return holder
    }

    override fun onBindViewHolder(holder: AllocationSchemesViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val allocationScheme = allocationSchemesList[position]
            holder.bind(allocationScheme)
        }
    }

    override fun getItemCount() = allocationSchemesList.size

    fun updateData(allocationSchemes: List<AllocationScheme>) {
        allocationSchemesList = allocationSchemes
        notifyDataSetChanged()
    }

    class AllocationSchemesViewHolder (private val itemBinding: ItemAllocationSchemeBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(allocationScheme: AllocationScheme) {
            itemBinding.radioButton.text = allocationScheme.name
            itemBinding.radioButton.isChecked = allocationScheme.isSelected

            if (allocationScheme.code == LIFECYCLE_ALLOCATION) {
                itemBinding.equitySubFundLifecycleTitle.text = allocationScheme.equitySubFundTitle
                itemBinding.debitSubFundLifecycleTitle.text = allocationScheme.debtSubFundTitle
                itemBinding.marketSubFundLifecycleTitle.text = allocationScheme.moneyMarketSubFundTitle
                itemBinding.equitySubFundLifecycleTxt.text = "Allocation ${allocationScheme.minEquitySubFund}%"
                itemBinding.debitSubFundLifecycleTxt.text = "Allocation ${allocationScheme.minDebtSubFund}%"
                itemBinding.marketSubFundLifecycleTxt.text = "Allocation ${allocationScheme.minMoneyMarketSubFund}%"
            } else {
                if (!allocationScheme.isEditMinEquitySubFund) {
                    itemBinding.equitySubFund.visibility = View.VISIBLE
                    itemBinding.equitySubfundInput.visibility = View.GONE
                    itemBinding.equitySubfundHint.visibility = View.GONE
                    itemBinding.equitySubFundTitle.text = allocationScheme.equitySubFundTitle
                    itemBinding.equitySubFundTxt.text = "Nil or Allocation ${allocationScheme.minEquitySubFund}%"
                } else {
                    itemBinding.equitySubFund.visibility = View.GONE
                    itemBinding.equitySubfundInput.visibility = View.VISIBLE
                    itemBinding.equitySubfundHint.visibility = View.VISIBLE
                    itemBinding.equitySubfundHint.text = "(Minimum Allocation ${allocationScheme.minEquitySubFund}%)"
                    itemBinding.equitySubfundInput.hint = allocationScheme.equitySubFundTitle
                    if (allocationScheme.equitySubFund > 0) {
                        itemBinding.equitySubfundEdittext.setText(allocationScheme.equitySubFund.toString())
                    }
                }

                if (!allocationScheme.isEditMinDebtSubFund) {
                    itemBinding.debitSubFund.visibility = View.VISIBLE
                    itemBinding.debitSubfundInput.visibility = View.GONE
                    itemBinding.debitSubfundHint.visibility = View.GONE
                    itemBinding.debitSubFundTitle.text = allocationScheme.debtSubFundTitle
                    itemBinding.debitSubFundTxt.text = "Nil or Allocation ${allocationScheme.minDebtSubFund}%"
                } else {
                    itemBinding.debitSubFund.visibility = View.GONE
                    itemBinding.debitSubfundInput.visibility = View.VISIBLE
                    itemBinding.debitSubfundHint.visibility = View.VISIBLE
                    itemBinding.debitSubfundHint.text = "(Minimum Allocation ${allocationScheme.minDebtSubFund}%)"
                    itemBinding.debitSubfundInput.hint = allocationScheme.debtSubFundTitle
                    if (allocationScheme.debitSubFund > 0) {
                        itemBinding.debitSubfundEdittext.setText(allocationScheme.debitSubFund.toString())
                    }
                }

                if (!allocationScheme.isEditMinMoneyMarketSubFund) {
                    itemBinding.moneyMarketSubFund.visibility = View.VISIBLE
                    itemBinding.marketSubfundInput.visibility = View.GONE
                    itemBinding.marketSubfundHint.visibility = View.GONE
                    itemBinding.moneyMarketSubFundTitle.text = allocationScheme.moneyMarketSubFundTitle
                    itemBinding.moneyMarketSubFundTxt.text = "Nil or Allocation ${allocationScheme.minMoneyMarketSubFund}%"
                } else {
                    itemBinding.moneyMarketSubFund.visibility = View.GONE
                    itemBinding.marketSubfundInput.visibility = View.VISIBLE
                    itemBinding.marketSubfundHint.visibility = View.VISIBLE
                    itemBinding.marketSubfundHint.text = "(Minimum Allocation ${allocationScheme.minMoneyMarketSubFund}%)"
                    itemBinding.marketSubfundInput.hint = allocationScheme.moneyMarketSubFundTitle
                    if (allocationScheme.moneyMarketSubFund > 0) {
                        itemBinding.marketSubfundEdittext.setText(allocationScheme.moneyMarketSubFund.toString())
                    }
                }

                if (allocationScheme.code == CUSTOMIZE_ALLOCATION) {
                    itemBinding.equitySubFund.visibility = View.GONE
                    itemBinding.debitSubFund.visibility = View.GONE
                    itemBinding.moneyMarketSubFund.visibility = View.GONE
                    itemBinding.equitySubfundInput.visibility = View.VISIBLE
                    itemBinding.debitSubfundInput.visibility = View.VISIBLE
                    itemBinding.marketSubfundInput.visibility = View.VISIBLE
                    itemBinding.equitySubfundHint.visibility = View.VISIBLE
                    itemBinding.debitSubfundHint.visibility = View.VISIBLE
                    itemBinding.marketSubfundHint.visibility = View.VISIBLE
                    itemBinding.equitySubfundHint.text = "(0-100%)"
                    itemBinding.debitSubfundHint.text = "(0-100%)"
                    itemBinding.marketSubfundHint.text = "(0-100%)"
                }
            }

            if (allocationScheme.isSelected) {
                itemBinding.dividerVolatility.visibility = View.GONE
                itemBinding.imgVolatilityArrow.setImageResource(R.drawable.ic_arrow_up)

                if (allocationScheme.code == LIFECYCLE_ALLOCATION) {
                    itemBinding.lifecycleAllocationContainer.visibility = View.VISIBLE
                } else {
                    itemBinding.volatilityContainer.visibility = View.VISIBLE
                }
            } else {
                itemBinding.dividerVolatility.visibility = View.VISIBLE
                itemBinding.imgVolatilityArrow.setImageResource(R.drawable.ic_arrow_down)
                itemBinding.lifecycleAllocationContainer.visibility = View.GONE
                itemBinding.volatilityContainer.visibility = View.GONE
            }
        }
    }
}