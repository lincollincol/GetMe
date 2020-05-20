package linc.com.getme.utils

import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.recyclerview.adapters.*
import linc.com.getme.ui.GetMeInterfaceSettings.Companion.ANIMATION_ADAPTER_FADE_IN
import linc.com.getme.ui.GetMeInterfaceSettings.Companion.ANIMATION_ADAPTER_SCALE_IN
import linc.com.getme.ui.GetMeInterfaceSettings.Companion.ANIMATION_ADAPTER_SCALE_IN_BOTTOM
import linc.com.getme.ui.GetMeInterfaceSettings.Companion.ANIMATION_ADAPTER_SLIDE_IN_LEFT
import linc.com.getme.ui.GetMeInterfaceSettings.Companion.ANIMATION_ADAPTER_SLIDE_IN_RIGHT

internal class RecyclerViewAnimationProvider {

    companion object {

        fun adapterAnimationFromConst(animation: Int, firstOnly: Boolean, adapter: RecyclerView.Adapter<*>) =
            when(animation) {
                ANIMATION_ADAPTER_FADE_IN -> AlphaInAnimationAdapter(adapter)
                ANIMATION_ADAPTER_SCALE_IN -> ScaleInAnimationAdapter(adapter)
                ANIMATION_ADAPTER_SCALE_IN_BOTTOM -> SlideInBottomAnimationAdapter(adapter)
                ANIMATION_ADAPTER_SLIDE_IN_LEFT -> SlideInLeftAnimationAdapter(adapter)
                ANIMATION_ADAPTER_SLIDE_IN_RIGHT -> SlideInRightAnimationAdapter(adapter)
                else -> null  // ANIMATION_DISABLE
            }?.apply {
                setFirstOnly(firstOnly)
            }

        @Deprecated("This is not available in the current GetMe version")
        fun itemAnimatorFromConst(animation: Int) = when(animation) {
            /*
            ANIMATION_ITEM_FLIP_IN_LEFT_Y -> FlipInLeftYAnimator()
            ANIMATION_ITEM_LANDING -> LandingAnimator()
            ANIMATION_ITEM_SLIDE_IN_LEFT -> SlideInLeftAnimator()
            ANIMATION_ITEM_OVERSHOOT_IN_LEFT -> OvershootInLeftAnimator()
            ANIMATION_ITEM_FLIP_IN_TOP_X -> FlipInTopXAnimator()
            ANIMATION_ITEM_OVERSHOOT_IN_RIGTH -> OvershootInRightAnimator()
            ANIMATION_ITEM_SLIDE_IN_RIGHT -> SlideInRightAnimator()
            ANIMATION_ITEM_FADE_IN -> FadeInAnimator()
            ANIMATION_ITEM_SCALE_IN_BOTTOM -> ScaleInBottomAnimator()
            ANIMATION_ITEM_SCALE_IN_LEFT -> ScaleInLeftAnimator()
            ANIMATION_ITEM_SCALE_IN_TOP -> ScaleInTopAnimator()
            ANIMATION_ITEM_SCALE_IN_RIGHT -> ScaleInRightAnimator()
            ANIMATION_ITEM_SCALE_IN -> ScaleInAnimator()
            ANIMATION_ITEM_SLIDE_IN_DOWN -> SlideInDownAnimator()
            ANIMATION_ITEM_FADE_IN_UP -> FadeInUpAnimator()
            ANIMATION_ITEM_FLIP_IN_RIGHT_Y -> FlipInRightYAnimator()
            ANIMATION_ITEM_FADE_IN_LEFT -> FadeInLeftAnimator()
            ANIMATION_ITEM_FADE_IN_DOWN -> FadeInDownAnimator()
            ANIMATION_ITEM_SLIDE_IN_UP -> SlideInUpAnimator()
            ANIMATION_ITEM_FLIP_IN_BOTTOM_X -> FlipInBottomXAnimator()
            ANIMATION_ITEM_FADE_IN_RIGHT -> FadeInRightAnimator()
            */
            else ->  null // ANIMATION_DISABLE
        }

    }

}