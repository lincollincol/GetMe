package linc.com.getme.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class GetMeInterfaceSettings(
    internal val selectionType: Int = SELECTION_SINGLE,
    internal val selectionMaxSize: Int = SELECTION_SIZE_DEFAULT,
    internal val enableOverScroll: Boolean = false,
    internal var adapterAnimation: Int = ANIMATION_ADAPTER_DISABLE,
    internal var animationFirstOnly: Boolean = true,
    internal var actionType: Int = 0
) : Parcelable {

    companion object {
        // Selection
        const val SELECTION_SINGLE = 101
        const val SELECTION_MULTIPLE = 111
        const val SELECTION_MIXED = 121
        const val SELECTION_SIZE_DEFAULT = -1

        // Adapter animations
        const val ANIMATION_ADAPTER_DISABLE = 202
        const val ANIMATION_ADAPTER_FADE_IN = 212
        const val ANIMATION_ADAPTER_SCALE_IN = 222
        const val ANIMATION_ADAPTER_SCALE_IN_BOTTOM = 232
        const val ANIMATION_ADAPTER_SLIDE_IN_LEFT = 242
        const val ANIMATION_ADAPTER_SLIDE_IN_RIGHT = 252

        // Item animations
        /*
        const val ANIMATION_DISABLE = 303
        const val ANIMATION_ITEM_FLIP_IN_LEFT_Y = 313
        const val ANIMATION_ITEM_LANDING = 323
        const val ANIMATION_ITEM_SLIDE_IN_LEFT = 333
        const val ANIMATION_ITEM_OVERSHOOT_IN_LEFT = 343
        const val ANIMATION_ITEM_FLIP_IN_TOP_X = 353
        const val ANIMATION_ITEM_OVERSHOOT_IN_RIGTH = 363
        const val ANIMATION_ITEM_SLIDE_IN_RIGHT = 373
        const val ANIMATION_ITEM_FADE_IN = 383
        const val ANIMATION_ITEM_SCALE_IN_BOTTOM = 393
        const val ANIMATION_ITEM_SCALE_IN_LEFT = 3103
        const val ANIMATION_ITEM_SCALE_IN_TOP = 3113
        const val ANIMATION_ITEM_SCALE_IN_RIGHT = 3123
        const val ANIMATION_ITEM_SCALE_IN = 3133
        const val ANIMATION_ITEM_SLIDE_IN_DOWN = 3143
        const val ANIMATION_ITEM_FADE_IN_UP = 3153
        const val ANIMATION_ITEM_FLIP_IN_RIGHT_Y = 3163
        const val ANIMATION_ITEM_FADE_IN_LEFT = 3173
        const val ANIMATION_ITEM_FADE_IN_DOWN = 3183
        const val ANIMATION_ITEM_SLIDE_IN_UP = 3193
        const val ANIMATION_ITEM_FLIP_IN_BOTTOM_X = 3203
        const val ANIMATION_ITEM_FADE_IN_RIGHT = 3213
        */

    }

}