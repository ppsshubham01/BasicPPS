package com.example.basic.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.basic.adapter.onBordingViewPagerAdapter
import com.example.basic.R
import com.example.basic.screens.Walkthrough0
import com.example.basic.screens.Walkthrough1
import com.example.basic.screens.Walkthrough2
import com.example.basic.utils.PreferenceManager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator


class OnBoardingViewpagerFragment : Fragment() {

    private lateinit var viewPager: ViewPager2

    private lateinit var dotsIndicator:DotsIndicator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_viewpager2, container, false)
        viewPager = view.findViewById(R.id.viewPagerr)
        dotsIndicator=view.findViewById(R.id.dots_indicator)

        val fragmentList= arrayListOf<Fragment>(
            Walkthrough0(),
            Walkthrough1(),
            Walkthrough2()
        )
        val adapter= onBordingViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        viewPager.adapter=adapter
        dotsIndicator.attachTo(viewPager)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            //Some implementation
//        }.attach()
        return view
    }
}