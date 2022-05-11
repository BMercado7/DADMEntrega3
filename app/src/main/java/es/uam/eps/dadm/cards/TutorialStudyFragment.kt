package es.uam.eps.dadm.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import es.uam.eps.dadm.cards.databinding.FragmentTutorialStudyBinding

class TutorialStudyFragment : Fragment()  {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentTutorialStudyBinding>(
            inflater,
            R.layout.fragment_tutorial_study,
            container,
            false
        )
        return binding.root
    }
}