package com.hbl.amc

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.hbl.amc.databinding.ActivityMainDrawerBinding


class MainDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainDrawerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setDrawerMenu()
    }

    private fun setDrawerMenu() {

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val menuButton: ImageView = findViewById(R.id.menu_button)

        navView.setNavigationItemSelectedListener(this)

        val radius = resources.getDimension(R.dimen.margin_50)
        val navViewBackground = navView.background as MaterialShapeDrawable

        navViewBackground.shapeAppearanceModel = navViewBackground.shapeAppearanceModel
            .toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val myProfile = navView.findViewById<LinearLayoutCompat>(R.id.nav_my_profile)
        val marketWatch = navView.findViewById<LinearLayoutCompat>(R.id.nav_market_watch)
        val investorEducation = navView.findViewById<LinearLayoutCompat>(R.id.nav_investor_education)
        val products = navView.findViewById<LinearLayoutCompat>(R.id.nav_products)
        val contactUs = navView.findViewById<LinearLayoutCompat>(R.id.nav_contact_us)

        val myProfileContainer = navView.findViewById<LinearLayoutCompat>(R.id.nav_my_profile_container)
        val marketWatchContainer = navView.findViewById<LinearLayoutCompat>(R.id.nav_market_watch_container)
        val investorEducationContainer = navView.findViewById<LinearLayoutCompat>(R.id.nav_investor_education_container)
        val myProfileArrow = navView.findViewById<ImageView>(R.id.img_my_profile_arrow)
        val marketWatchArrow = navView.findViewById<ImageView>(R.id.img_market_watch_arrow)
        val investorEducationArrow = navView.findViewById<ImageView>(R.id.img_investor_education_arrow)

        myProfile.setOnClickListener {
            if (myProfileContainer.isVisible) {
                myProfileContainer.visibility = View.GONE
                myProfileArrow.setImageResource(R.drawable.ic_arrow_down)
            } else {
                myProfileContainer.visibility = View.VISIBLE
                myProfileArrow.setImageResource(R.drawable.ic_arrow_up)
            }
        }

        marketWatch.setOnClickListener {
            if (marketWatchContainer.isVisible) {
                marketWatchContainer.visibility = View.GONE
                marketWatchArrow.setImageResource(R.drawable.ic_arrow_down)
            } else {
                marketWatchContainer.visibility = View.VISIBLE
                marketWatchArrow.setImageResource(R.drawable.ic_arrow_up)
            }
        }

        investorEducation.setOnClickListener {
            if (investorEducationContainer.isVisible) {
                investorEducationContainer.visibility = View.GONE
                investorEducationArrow.setImageResource(R.drawable.ic_arrow_down)
            } else {
                investorEducationContainer.visibility = View.VISIBLE
                investorEducationArrow.setImageResource(R.drawable.ic_arrow_up)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }
}
