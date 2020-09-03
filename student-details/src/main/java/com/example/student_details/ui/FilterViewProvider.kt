package com.example.student_details.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.example.student_details.R

object FilterViewProvider {


    fun getTitle(position: Int): String {
        return when (position) {
            0 -> "Filters"
            else -> "error"
        }
    }

    fun getSizeFilterView(
            container: ViewGroup,
            lifeCycleOwner: LifecycleOwner,
            filterCollectionViewModel: ClassFilterViewModel
    ): View {
        val MAX_COLUMN_PER_ROW = 6
        val sizeFilterView = LayoutInflater.from(container.context).inflate(
                R.layout.filters_layout,
                container,
                false
        )


        container.addView(sizeFilterView)
        return sizeFilterView
    }
//
//    private fun getColorFilterView(
//            container: ViewGroup,
//            lifeCycleOwner: LifecycleOwner,
//            filterCollectionViewModel: FilterCollectionViewModel
//    ): View {
//        val MAX_COLUMN_PER_ROW = 6
//        val colorFilterView = LayoutInflater.from(container.context).inflate(
//                R.layout.filters_layout,
//                container,
//                false
//        ) as RecyclerView
//        colorFilterView.apply {
//            setHasFixedSize(true)
//            adapter = ColorFilterAdapter(filterCollectionViewModel)
//
//            val size = Point()
//            (context as FragmentActivity).windowManager.defaultDisplay.getSize(size)
//            val itemWidth = context.resources.getDimensionPixelSize(R.dimen.filter_size_item_diameter_size)
//
//            val parentWidth = size.x
//
//            val spanCount = Math.min(parentWidth / itemWidth, MAX_COLUMN_PER_ROW)
//            layoutManager = GridLayoutManager(context, spanCount)
//        }
//
//        filterCollectionViewModel.queryBuilder.observe(lifeCycleOwner, Observer {
//            colorFilterView.adapter?.notifyDataSetChanged()
//        })
//        filterCollectionViewModel.collectionNodeResponse.observe(lifeCycleOwner, Observer {
//            colorFilterView.adapter?.notifyDataSetChanged()
//        })
//        container.addView(colorFilterView)
//        return colorFilterView
//    }
//
//
//    private fun getAppliedFilterView(
//            container: ViewGroup,
//            lifeCycleOwner: LifecycleOwner,
//            filterCollectionViewModel: ClassFilterViewModel
//    ): View {
//        val mAppliedFiltersRelativeLayout = LayoutInflater.from(container.context).inflate(
//                R.layout.applied_filters,
//                container,
//                false
//        ) as RelativeLayout
//
////        val mFilterCollectionName = mAppliedFiltersRelativeLayout.findViewById<TextView>(R.id.filtered_collection_name)
////        mFilterCollectionName.text = filterCollectionViewModel.collectionName
//
//        val mExpandableHeightSizeGridView =
//                mAppliedFiltersRelativeLayout.findViewById<View>(R.id.expanded_size_grid_view) as ExpandableHeightGridView?
//        val mExpandableHeightColorGridView =
//                mAppliedFiltersRelativeLayout.findViewById<View>(R.id.expanded_color_grid_view) as ExpandableHeightGridView?
//        val mExpandableHeightBrandGridView =
//                mAppliedFiltersRelativeLayout.findViewById<View>(R.id.expanded_brand_grid_view) as ExpandableHeightGridView?
//
//        val queryParameters = filterCollectionViewModel.queryBuilder.value!!.query
//
//        val appliedSizeAdapter = AppliedFiltersSizeGridAdapter(
//                container.context,
//                filterCollectionViewModel
//        )
//        mExpandableHeightSizeGridView!!.adapter = appliedSizeAdapter
//        setLayoutParamsOfSizeGridView(
//                container.context,
//                queryParameters!!.getIncludeSizes()!!.size,
//                mExpandableHeightSizeGridView
//        )
//
//        val appliedColorAdapter = AppliedFiltersColorGridAdapter(
//                container.context,
//                filterCollectionViewModel
//        )
//        mExpandableHeightColorGridView!!.adapter = appliedColorAdapter
//        setLayoutParamsOfColorGridView(
//                container.context,
//                queryParameters.includeColors!!.size,
//                mExpandableHeightColorGridView
//        )
//
//        val appliedBrandAdapter = AppliedFiltersBrandGridAdapter(
//                container.context,
//                filterCollectionViewModel
//        )
//        mExpandableHeightBrandGridView!!.adapter = appliedBrandAdapter
//        setLayoutParamsOfBrandGridView(
//                container.context,
//                queryParameters.includeBrands!!.size,
//                mExpandableHeightBrandGridView
//        )
//
//        manageUIForAppliedFilters(filterCollectionViewModel.queryBuilder.value!!.query, mAppliedFiltersRelativeLayout)
//
//
//        filterCollectionViewModel.queryBuilder.observe(lifeCycleOwner, Observer {
//            manageUIForAppliedFilters(it.query, mAppliedFiltersRelativeLayout)
//        })
//
//        filterCollectionViewModel.brandChange.observe(lifeCycleOwner, Observer {
//            appliedBrandAdapter.updateAppliedFiltersBrandGridAdapter(container.context)
//            mExpandableHeightBrandGridView.adapter = appliedBrandAdapter
//            setLayoutParamsOfBrandGridView(
//                    container.context,
//                    it.query!!.includeBrands!!.size,
//                    mExpandableHeightBrandGridView
//            )
//        })
//
//        filterCollectionViewModel.sizeChange.observe(lifeCycleOwner, Observer {
//            appliedSizeAdapter.updateAppliedFiltersSizeGridAdapter(container.context)
//            mExpandableHeightSizeGridView.adapter = appliedSizeAdapter
//            setLayoutParamsOfSizeGridView(
//                    container.context,
//                    it.query!!.getIncludeSizes()!!.size,
//                    mExpandableHeightSizeGridView
//            )
//        })
//
//        filterCollectionViewModel.colorChange.observe(lifeCycleOwner, Observer {
//            appliedColorAdapter.updateAppliedFiltersColorGridAdapter(container.context)
//            mExpandableHeightColorGridView.adapter = appliedColorAdapter
//            setLayoutParamsOfColorGridView(
//                    container.context,
//                    it.query!!.includeColors!!.size,
//                    mExpandableHeightColorGridView
//            )
//        })
//
////        filterCollectionViewModel.collectionNodeResponse.observe(lifeCycleOwner, Observer {
////            manageUIForAppliedFilters(filterCollectionViewModel.queryBuilder.value!!.query)
////            appliedSizeAdapter.updateAppliedFiltersSizeGridAdapter(container.context)
////            appliedColorAdapter.updateAppliedFiltersColorGridAdapter(container.context)
////            appliedBrandAdapter.updateAppliedFiltersBrandGridAdapter(container.context)
////        })
//
//        container.addView(mAppliedFiltersRelativeLayout)
//        return mAppliedFiltersRelativeLayout
//    }
//
//    /*
//     *This Function will manage UI after filter update
//     */
//    private fun manageUIForAppliedFilters(
//            queryParameters: QueryParameters?,
//            mAppliedFiltersRelativeLayout: RelativeLayout
//    ) {
//        val mBrandFilterRelativeLayout =
//                mAppliedFiltersRelativeLayout.findViewById<RelativeLayout>(R.id.rl_brand_fliter)
//        val mColorFilterRelativeLayout =
//                mAppliedFiltersRelativeLayout.findViewById<RelativeLayout>(R.id.rl_color_filter)
//        val mSizeFilterRelativeLayout = mAppliedFiltersRelativeLayout.findViewById<RelativeLayout>(R.id.rl_size_filter)
//        val mNoFilterAppliedTextView =
//                mAppliedFiltersRelativeLayout.findViewById<TextView>(R.id.no_applied_filters_text)
//        if (queryParameters != null) {
//            if (isFilterEmpty(queryParameters)) {
//                mNoFilterAppliedTextView!!.visibility = View.VISIBLE
//            }
//            if (queryParameters.includeColors != null && queryParameters.includeColors!!.size > 0) {
//                mColorFilterRelativeLayout!!.visibility = View.VISIBLE
//                mNoFilterAppliedTextView!!.visibility = View.GONE
//            } else {
//                mColorFilterRelativeLayout!!.visibility = View.GONE
//            }
//            if (queryParameters.includeBrands != null && queryParameters.includeBrands!!.size > 0) {
//                mBrandFilterRelativeLayout!!.visibility = View.VISIBLE
//                mNoFilterAppliedTextView!!.visibility = View.GONE
//            } else {
//                mBrandFilterRelativeLayout!!.visibility = View.GONE
//            }
//            if (queryParameters.getIncludeSizes() != null && queryParameters.getIncludeSizes()!!.size > 0) {
//                mSizeFilterRelativeLayout!!.visibility = View.VISIBLE
//                mNoFilterAppliedTextView!!.visibility = View.GONE
//            } else {
//                mSizeFilterRelativeLayout!!.visibility = View.GONE
//            }
//        }
//    }
//
//    /**
//     * Check whether the applied filter empty.
//     *
//     * @return {boolean} - true if there are no filters applied.
//     */
//    private fun isFilterEmpty(parameters: QueryParameters?): Boolean {
//        return parameters == null || (parameters.includeBrands!!.isEmpty() &&
//                parameters.includeColors!!.isEmpty() && parameters.getIncludeSizes()!!.isEmpty()
//                && parameters.includeRatings!!.isEmpty())
//    }
//
//    /**
//     * This Function will set the Params for the Size Grid View
//     *
//     * @param sizeOfItem
//     */
//    private fun setLayoutParamsOfSizeGridView(
//            context: Context,
//            sizeOfItem: Int,
//            mExpandableHeightSizeGridView: ExpandableHeightGridView
//    ) {
//        val layoutParams = mExpandableHeightSizeGridView.layoutParams
//        if (sizeOfItem <= 6) {
//            layoutParams.width = PixelConverterUtils.convertDpToPixel(sizeOfItem * 55.0f, context).toInt()
//            mExpandableHeightSizeGridView.numColumns = sizeOfItem
//        } else {
//            mExpandableHeightSizeGridView.numColumns = 6
//            layoutParams.width = PixelConverterUtils.convertDpToPixel(6 * 55.0f, context).toInt()
//        }
//        mExpandableHeightSizeGridView.layoutParams = layoutParams
//    }
//
//    /**
//     * This Function will set the Params for the Size Grid View
//     *
//     * @param sizeOfItem
//     */
//    private fun setLayoutParamsOfColorGridView(
//            context: Context,
//            sizeOfItem: Int,
//            mExpandableHeightColorGridView: ExpandableHeightGridView
//    ) {
//        val layoutParams = mExpandableHeightColorGridView.layoutParams
//        if (sizeOfItem <= 6) {
//            layoutParams.width = PixelConverterUtils.convertDpToPixel(sizeOfItem * 55.0f, context).toInt()
//            mExpandableHeightColorGridView.numColumns = sizeOfItem
//        } else {
//            mExpandableHeightColorGridView.numColumns = 6
//            layoutParams.width = PixelConverterUtils.convertDpToPixel(6 * 55.0f, context).toInt()
//        }
//        mExpandableHeightColorGridView.layoutParams = layoutParams
//    }
//
//
//    /**x
//     * This Function will set the Params for the Color Grid View
//     *
//     * @param sizeOfItem
//     */
//    private fun setLayoutParamsOfBrandGridView(
//            context: Context,
//            sizeOfItem: Int,
//            mExpandableHeightBrandGridView: ExpandableHeightGridView
//    ) {
//        val layoutParams = mExpandableHeightBrandGridView.layoutParams
//        if (sizeOfItem <= 3) {
//            layoutParams.width = PixelConverterUtils.convertDpToPixel(sizeOfItem * 110.0f, context).toInt()
//            mExpandableHeightBrandGridView.numColumns = sizeOfItem
//        } else {
//            mExpandableHeightBrandGridView.numColumns = 3
//            layoutParams.width = PixelConverterUtils.convertDpToPixel(3 * 110.0f, context).toInt()
//        }
//        mExpandableHeightBrandGridView.layoutParams = layoutParams
//    }
//
//
//    private fun getDummyView(container: ViewGroup): View {
//        container.addView(LayoutInflater.from(container.context).inflate(R.layout.splash_fragment, container, false))
//        return container
//    }
//
//    fun <T : View> create(context: Context, viewClass: Class<T>): T {
//        return viewClass.getConstructor(Context::class.java).newInstance(context)
//    }
}