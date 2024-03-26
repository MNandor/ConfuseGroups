package ml.nandor.confusegroups.domain.model

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup

data class ConfuseGroupToAddTo (
    val confuseGroup: ConfuseGroup?,
    val associatedNotes: List<AtomicNote>

)