package com.hbl.amc.ui.FundPrices

class FundPricesExpandableModel {
    companion object{
        const val PARENT = 1
        const val CHILD = 2

    }
    lateinit var requirementParent: FundPricesParent.FundPricesChild
    var type : Int
    lateinit var requirementChild : String
    var isExpanded : Boolean
    private var isCloseShown : Boolean

    constructor( type : Int, fundPricesParent: FundPricesParent.FundPricesChild, isExpanded : Boolean = false,isCloseShown : Boolean = false){
        this.type = type
        this.requirementParent = fundPricesParent
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }


    constructor(type : Int, fundPricesChild : String, isExpanded : Boolean = false,isCloseShown : Boolean = false){
        this.type = type
        this.requirementChild = fundPricesChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }
}

data class FundPricesParent(
    val fundPricesList: List<FundPricesChild>
) {
    data class FundPricesChild(
        val title: String,
        //TODO Need to create another model for remaining fields ( instead of fundsPrices Detail)
        val fundsPricesDetail: List<String>,
        val id : Int
    )
}