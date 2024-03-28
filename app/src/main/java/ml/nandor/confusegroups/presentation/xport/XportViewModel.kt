package ml.nandor.confusegroups.presentation.xport

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroupToAddTo
import ml.nandor.confusegroups.domain.usecase.AddCardsFromTextUseCase
import ml.nandor.confusegroups.domain.usecase.DeleteDeckUseCase
import ml.nandor.confusegroups.domain.usecase.GetDeckSizesUseCase
import ml.nandor.confusegroups.domain.usecase.InsertCardUseCase
import ml.nandor.confusegroups.domain.usecase.InsertDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsGroupedFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListDecksUseCase
import ml.nandor.confusegroups.domain.usecase.RenameDeckUseCase
import ml.nandor.confusegroups.domain.usecase.UpdateCardUseCase
import timber.log.Timber
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class XportViewModel @Inject constructor(
    private val listCardsFromDeckUseCase:ListCardsFromDeckUseCase,
    private val listCardsGroupedFromDeckUseCase: ListCardsGroupedFromDeckUseCase
): ViewModel() {

    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _exportContent:MutableState<String> = mutableStateOf("a\nb")
    val exportContent:State<String> = _exportContent

    private val _cardsGrouped:MutableState<List<ConfuseGroupToAddTo>> = mutableStateOf(listOf())

    fun loadExportString(deckName: String?){
        if (deckName == null)
            return

        listCardsGroupedFromDeckUseCase(deckName).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _cardsGrouped.value = it.data!!
                }
            }
        }.launchIn(viewModelScope)
    }

    val exportString = derivedStateOf {
        val sb = StringBuilder()

        for (group in _cardsGrouped.value.sortedBy { it.confuseGroup?.displayName?: "ZZZ" }){
            sb.append("# ")
            sb.append(group.confuseGroup?.displayName ?: "Ungrouped")
            sb.append("\n")
            for (card in group.associatedNotes.sortedBy { it.question+it.answer }){
                sb.append(card.question)
                sb.append("-")
                sb.append(card.answer)
                sb.append("\n")
            }
            sb.append("\n")

        }
        sb.toString()
    }


    fun setCompareString(s: String){
        _exportContent.value = s
    }

    val diffed = derivedStateOf {
        // gonna handwrite a short algorithm for now
        // should import a diff library later
        // this algorithm doesn't take into account orders, groups, duplicates
        // but it's good enough for now
        // todo
        val leftLines = exportString.value.split("\n")
        val rightLines = _exportContent.value.split("\n")

        val sb = StringBuilder()

        for (line in leftLines){
            if (!rightLines.contains(line)){
                sb.append("- ")
                sb.append(line)
                sb.append("\n")

            }

        }

        for (line in rightLines){
            if (!leftLines.contains(line)){
                sb.append("+ ")
                sb.append(line)
                sb.append("\n")

            }

        }

        sb.toString()

    }

}