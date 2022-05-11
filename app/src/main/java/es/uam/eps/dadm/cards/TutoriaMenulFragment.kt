package es.uam.eps.dadm.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.databinding.FragmentTutorialMenuBinding

class TutoriaMenulFragment : Fragment()  {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentTutorialMenuBinding>(
            inflater,
            R.layout.fragment_tutorial_menu,
            container,
            false
        )
        binding.nextTutorialStudy.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_tutorialFragment_to_tutorialStudyFragment)
        }
        return binding.root
    }
}