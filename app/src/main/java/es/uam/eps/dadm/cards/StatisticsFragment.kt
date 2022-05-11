package es.uam.eps.dadm.cards

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import es.uam.eps.dadm.cards.databinding.FragmentStatisticsBinding
import java.time.LocalDateTime


class StatisticsFragment : Fragment() {
    private var cardsL: MutableList<Card> = mutableListOf()
    private var decksL: MutableList<Deck> = mutableListOf()
    private var nDueCards: Int = 0
    lateinit var binding: FragmentStatisticsBinding
    private lateinit var pieChart: PieChart

    private val viewModelStudy: StudyViewModel by lazy {
        ViewModelProvider(this).get(StudyViewModel::class.java)
    }
    private val statisticsViewModel: StatisticsViewModel by lazy {
        ViewModelProvider(this).get(StatisticsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_statistics,
            container,
            false)


        binding.studyViewModel = viewModelStudy
        binding.statisticsViewModel = statisticsViewModel
        pieChart = binding.piechart

        // Obtenemos todos los datos de Live Data:

        // Tarjetas Estudiadas:
        statisticsViewModel.nCardStudied.observe(viewLifecycleOwner){
            binding.invalidateAll()
        }

        statisticsViewModel.decks.observe(viewLifecycleOwner) { list ->
            for (deckWithCards in list){
                decksL.add(deckWithCards.deck)
                cardsL.addAll(deckWithCards.cards)
            }

            val entries: MutableList<PieEntry> = mutableListOf()

            entries.add(PieEntry(easyCards().toFloat(),"Easy"))
            entries.add(PieEntry(doubtCards().toFloat(),"Doubt"))
            entries.add(PieEntry(hardCards().toFloat(),"Hard"))

            nDueCards = numberOfDueCards()

            val decksId : MutableList<Long> = mutableListOf()
            for (decks in decksL){
                add()
            }

            for(id in decksId){
                statisticsViewModel.loadDeckId(id.toString())
                statisticsViewModel.deckWithCards.observe(viewLifecycleOwner) {
                    val message = "Deck named ${it[0].deck.name} has ${it[0].cards.size} cards"
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                }
            }
            setupPieChart()
            loadPieChartData(entries)
            pieChart.invalidate()
        }

        return binding.root
    }

    private fun setupPieChart(){
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setCenterTextSize(24f)
        pieChart.description.isEnabled = false

        val l = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true
    }

    private fun loadPieChartData(entries: MutableList<PieEntry> ) {
        //val entries: MutableList<PieEntry> = mutableListOf()

        val dataSet = PieDataSet(entries, "")
        val colors : MutableList<Int> = mutableListOf()

        for(color in ColorTemplate.MATERIAL_COLORS){
            colors.add(color)
        }
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        pieChart.data = data
        pieChart.invalidate()

    }

    private fun numberOfDueCards() = cardsL.filter { it.isDue(LocalDateTime.now()) }.size

    private fun easyCards() = cardsL.filter { it.quality == 5 }.size

    private fun doubtCards() = cardsL.filter { it.quality == 3 }.size

    private fun hardCards() = cardsL.filter { it.quality == 0 }.size
}

private fun add() {

}

