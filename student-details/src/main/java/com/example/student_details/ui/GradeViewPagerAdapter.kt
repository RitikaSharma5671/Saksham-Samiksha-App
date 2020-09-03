package com.example.student_details.ui

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class GradeViewPagerAdapter(val lifecycleOwner: ClassFilterFragment, val filterCollectionViewModel: ClassFilterViewModel) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return FilterViewProvider.getSizeFilterView(container, lifecycleOwner, filterCollectionViewModel)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return 1
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return FilterViewProvider.getTitle(position)
    }
}