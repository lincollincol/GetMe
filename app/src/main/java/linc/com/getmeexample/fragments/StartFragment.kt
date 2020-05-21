package linc.com.getmeexample.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import linc.com.getmeexample.ExampleGetMeActivity
import linc.com.getmeexample.MainActivity

import linc.com.getmeexample.R


class StartFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.defaultGetMe).setOnClickListener(this)
        view.findViewById<Button>(R.id.customGetMeItemLayout).setOnClickListener(this)
        view.findViewById<Button>(R.id.customGetMeStyle).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeMainContent).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeExceptContent).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeOnlyDirectories).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeSingleSelection).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeFromPath).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeMaximumSelectionSize).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeAdapterAnimation).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeOverScroll).setOnClickListener(this)
        view.findViewById<Button>(R.id.getMeFromActivity).setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.defaultGetMe -> openFragment(ExampleGetMeFragment.DEFAULT_GETME)
            R.id.customGetMeItemLayout -> openFragment(ExampleGetMeFragment.CUSTOM_ITEM_LAYOUT_GETME)
            R.id.customGetMeStyle -> openFragment(ExampleGetMeFragment.CUSTOM_STYLE_GETME)
            R.id.getMeMainContent -> openFragment(ExampleGetMeFragment.MAIN_CONTENT_GETME)
            R.id.getMeExceptContent -> openFragment(ExampleGetMeFragment.EXCEPT_CONTENT_GETME)
            R.id.getMeOnlyDirectories -> openFragment(ExampleGetMeFragment.ONLY_DIRECTORIES_GETME)
            R.id.getMeSingleSelection -> openFragment(ExampleGetMeFragment.SINGLE_SELECTION_GETME)
            R.id.getMeFromPath -> openFragment(ExampleGetMeFragment.FROM_PATH_GETME)
            R.id.getMeMaximumSelectionSize -> openFragment(ExampleGetMeFragment.MAX_SELECTION_SIZE_GETME)
            R.id.getMeAdapterAnimation -> openFragment(ExampleGetMeFragment.ADAPTER_ANIMATION_GETME)
            R.id.getMeOverScroll -> openFragment(ExampleGetMeFragment.OVERSCROLL_GETME)
            R.id.getMeFromActivity -> {
                startActivity(Intent(this.context, ExampleGetMeActivity::class.java))
            }
        }
    }

    private fun openFragment(type: Int) {
        fragmentManager?.beginTransaction()
            ?.replace(
                R.id.fragmentContainer,
                ExampleGetMeFragment.newInstance(type)
            )
            ?.addToBackStack(null)
            ?.commit()
    }

}
