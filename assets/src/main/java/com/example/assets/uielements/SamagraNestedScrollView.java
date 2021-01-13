package com.example.assets.uielements;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.widget.NestedScrollView;

public class SamagraNestedScrollView extends NestedScrollView {
        private INestedScrollListener mScrollListener; // A listener instance to listen status of child scroll view

        public SamagraNestedScrollView(Context context) {
            super(context);
        }

        public SamagraNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context,attrs,defStyleAttr);
        }

        public SamagraNestedScrollView(Context context,AttributeSet attrs) {
            super(context,attrs);
        }

        // true if we can scroll (not locked)
        // false if we cannot scroll (locked)
        private boolean mScrollable = true;

        /**
         * Set scrolling enabled or disabled
         *
         * @param enabled - true to enable the scrolling
         */
        public void setScrollingEnabled(boolean enabled) {
            mScrollable = enabled;
        }

        /**
         * Returns whether scrolling is enabled or disabled
         *
         * @return
         */
        public boolean isScrollable() {
            return mScrollable;
        }

        /**
         * Set INestedScrollistener instance to listen the NestedScrolling events
         *
         * @param listener
         */
        public void setNestedScrollListener(INestedScrollListener listener) {
            mScrollListener = listener;
        }

        /**
         * {@inheritDoc}
         * The method will handle, whether to consume touch event based on whether scrolling is enabled or disabled
         *
         * @param ev
         * @return
         */
        @Override
        public boolean onTouchEvent(MotionEvent ev) {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // if we can scroll pass the event to the superclass
                    if (mScrollable) return super.onTouchEvent(ev);
                    // only continue to handle the touch event if scrolling enabled
                    return mScrollable; // mScrollable is always false at this point
                default:
                    return super.onTouchEvent(ev);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            // Don't do anything with intercepted touch events if
            // we are not scrollable
            if (!mScrollable) return false;
            else return super.onInterceptTouchEvent(ev);
        }


        /**
         * {@inheritDoc}
         * This method will be called, when recycler view within product page
         */
        @Override
        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            if (mScrollListener != null) {
                if (velocityY < 0) {
                    // this statement should only run when reviews list is on top
                    if (!mScrollListener.onPreNestedFling(target,velocityY)) {
                        fling((int) velocityY);
                        return true;
                    }
                }
            }
            return super.onNestedPreFling(target,velocityX,velocityY);
        }


        @Override
        public boolean onStartNestedScroll(View child,View target,int nestedScrollAxes) {
            Log.d(getClass().getSimpleName(),"onStartNestetdScroll() called");
            if (mScrollListener != null) {
                if (mScrollListener.onNestedScrollStart(target)) {
                    return false;
                }
            }
            return super.onStartNestedScroll(child,target,nestedScrollAxes);
        }

        public void scrollByWithAnimation(int scrollX, int scrollY,long duration) {
            int currentScrollX = getScrollX();
            int currentScrollY = getScrollY();
            int x = currentScrollX+scrollX;
            int y = currentScrollY + scrollY;
            ObjectAnimator xTranslate = ObjectAnimator.ofInt(this,"scrollX",x);
            ObjectAnimator yTranslate = ObjectAnimator.ofInt(this,"scrollY",y);

            AnimatorSet animators = new AnimatorSet();
            animators.setDuration(duration);
            animators.playTogether(xTranslate,yTranslate);
            animators.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationRepeat(Animator arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animator arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationCancel(Animator arg0) {
                    // TODO Auto-generated method stub

                }
            });
            animators.start();
        }

        public void scrollToWihAnimation(int x, int y, long duration) {
            scrollByWithAnimation(x-getScrollX(), y-getScrollY(), duration);
        }

        @Override
        protected void onScrollChanged(int l,int t,int oldl,int oldt) {
            super.onScrollChanged(l,t,oldl,oldt);
            if(mScrollListener != null) {
                mScrollListener.onScrollChanged(l,t,oldl,oldt);
            }
        }

        /**
         * Interface which is used to determine whether to consume event by parent scroll view or nested scrollview
         */
        public interface INestedScrollListener {
            /**
             * This method will be called by onStartNestedScroll(), which says that when child scrollview tries to consume scrolling,
             * this parent scrollview will decide, whether to allow child scrollview to consume event or not. One who implemented
             * this interface will decide the condition on the basis of which, either parent scrollview will be scrolled or child scrollview
             * will be scrolled
             *
             * @param targetView
             * @return - true, if child view should scroll otherwise false to enable parent to scroll
             */
            boolean onNestedScrollStart (View targetView);

            /**
             * This method will be called by onNestedPreFling(). Based on return value of this method, it will be decide, whether to
             * allow child view to act on fling or not. If child view is not allowed, then this parent scrollview will be fling
             *
             * @param targetView - child scrollview which requested to fling
             * @param velocityY  - velocity in vertical direction for fling
             * @return - returns true, if child view should fling, otherwise false
             */
            boolean onPreNestedFling (View targetView, float velocityY);

            /**
             * This method will be called when there is some scrolling occured.
             * @param  fromLeft - amount of scroll in pixels scrolled from left.
             * @param fromTop - amount of scroll in pixels scrolled from top.
             *                @param oldFromLeft - Amount of scroll
             *                @param oldFromTop
             *
             */
            void onScrollChanged(int fromLeft, int fromTop, int oldFromLeft, int oldFromTop);
        }
    }
