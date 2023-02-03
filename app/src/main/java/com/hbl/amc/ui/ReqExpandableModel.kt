package com.hbl.amc.ui

class ReqExpandableModel {
    companion object{
        const val PARENT = 1
        const val CHILD = 2
        /* const val BUTTONLAYOUT = 3
         const val REMOVELIST = 4
         const val EMPTYLAYOUT = 5*/
    }
    lateinit var requirementParent: OnboardingRequirement.Requirement
    var type : Int
    lateinit var requirementChild : String
    var isExpanded : Boolean
    //  var isPauseShown : Boolean
    private var isCloseShown : Boolean
    // var isLocked : Boolean

    constructor( type : Int, countryParent: OnboardingRequirement.Requirement, isExpanded : Boolean = false,isCloseShown : Boolean = false){
        this.type = type
        this.requirementParent = countryParent
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }


    constructor(type : Int, countryChild : String, isExpanded : Boolean = false,isCloseShown : Boolean = false){
        this.type = type
        this.requirementChild = countryChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }
}

data class OnboardingRequirement(
    val requirements: List<Requirement>
) {
    data class Requirement(
        val requirement: String, // India
        val subRequirements: List<String>,
        val id : Int
    )
}