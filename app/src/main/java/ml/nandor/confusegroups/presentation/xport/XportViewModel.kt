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
import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.ConfuseGroupToAddTo
import ml.nandor.confusegroups.domain.usecase.AddCardsFromTextUseCase
import ml.nandor.confusegroups.domain.usecase.CreateConfuseGroupUseCase
import ml.nandor.confusegroups.domain.usecase.DeleteDeckUseCase
import ml.nandor.confusegroups.domain.usecase.GetDeckSizesUseCase
import ml.nandor.confusegroups.domain.usecase.InsertCardUseCase
import ml.nandor.confusegroups.domain.usecase.InsertDeckUseCase
import ml.nandor.confusegroups.domain.usecase.JoinConfuseGroupUseCase
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
    private val listCardsGroupedFromDeckUseCase: ListCardsGroupedFromDeckUseCase,
    private val joinConfuseGroupUseCase: JoinConfuseGroupUseCase,
    private val createConfuseGroupUseCase: CreateConfuseGroupUseCase
): ViewModel() {

    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _importableText:MutableState<String> = mutableStateOf("a\nb")
    val importableText:State<String> = _importableText

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

        listCardsFromDeckUseCase(deckName).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _cardsInDeck.value = it.data!!
                }
            }

        }.launchIn(viewModelScope)
    }

    private val _cardsInDeck:MutableState<List<AtomicNote>> = mutableStateOf(listOf())

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
        _importableText.value = s
    }

    val diffed = derivedStateOf {
        // gonna handwrite a short algorithm for now
        // should import a diff library later
        // this algorithm doesn't take into account orders, groups, duplicates
        // but it's good enough for now
        // todo
        val leftLines = exportString.value.split("\n")
        val rightLines = _importableText.value.split("\n")

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

    fun importFromText(){
        Timber.d("Begin importing!")

        val lines = _importableText.value.split("\n")

        var groupName = ""
        val linesSoFar = arrayListOf<Pair<String, String>>()

        for (_line in lines){
            val line = _line.trim()

            if (line.isBlank()){
                continue
            }

            if (line.startsWith("# ")){
                individualImport(groupName, linesSoFar)
                linesSoFar.clear()
                groupName = line.removePrefix("# ")
                continue
            }

            if (!line.contains("-")){
                Timber.d("Missing dash in $groupName")
                continue
            }

            // todo this should be a usecase



            val split = if (line.contains("--")){
                line.split("--")
            }
            else if (line.contains("-")){
                line.split("-")
            } else return

            linesSoFar.add(Pair(split[0], split[1]))


        }

        individualImport(groupName, linesSoFar)

    }

    private fun individualImport(groupname: String, notes:List<Pair<String, String>>){
        if (groupname.isNullOrEmpty())
            return
        if (notes.size == 0)
            return
        Timber.d("Into # $groupname - ${notes.size} possible additions")

        val group = searchGroup(groupname) ?: ConfuseGroupToAddTo(createGroup(groupname), listOf())

        group.confuseGroup ?: return

        for (_note in notes){
            // todo offer to create note instead
            // currently, if this doesn't correspond to an existing note, we just don't add the note to the deck
            val note = searchNote(_note.first, _note.second)?: continue

            // todo check if already a member
            Timber.d("${group.confuseGroup} < $note")

            if (group.associatedNotes.contains(note)){
                Timber.d("^Nevermind, already in")
                continue
            }

            joinConfuseGroupUseCase(Pair(note.id, group.confuseGroup.id)).launchIn(viewModelScope)

        }

    }

    private fun searchNote(question: String, answer:String): AtomicNote? {
        if (_cardsInDeck.value.size == 0){
            Timber.d("Load cards from deck first")
        }

        val returnables =  _cardsInDeck.value.filter { it.question == question && it.answer == answer }
        if (returnables.size > 1){
            Timber.d("Duplicates in groups $returnables")
            return null
        } else if (returnables.size == 0){
            Timber.d("None found in groups")
            return null
        } else{
            return returnables[0]
        }
    }

    private fun searchGroup(groupName:String): ConfuseGroupToAddTo? {

        val returnables = _cardsGrouped.value.filter { it.confuseGroup?.displayName == groupName }
        if (returnables.size > 1){
            Timber.d("Duplicates in notes $returnables")
            return null
        } else if (returnables.size == 0){
            Timber.d("None found in notes")
            return null
        } else{
            return returnables[0]
        }
    }

    private fun createGroup(groupName:String): ConfuseGroup {
        val groupID = Util.getCardName()

        val group = ConfuseGroup(groupID, groupName)

        createConfuseGroupUseCase(group).launchIn(viewModelScope)
        return group

    }


}