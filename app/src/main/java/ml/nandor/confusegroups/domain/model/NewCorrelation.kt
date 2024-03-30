package ml.nandor.confusegroups.domain.model

data class NewCorrelation(
    val leftCard: AtomicNote,
    val rightCard: AtomicNote,
    var timesLeftAvoidedRight: Int = 0,
    var timesRightAvoidedLeft: Int = 0,
    var timesLeftFellForRight: Int = 0,
    var timesRightFellForLeft: Int = 0,
){
    fun getCorrelationValue():Double{
        val left = (timesLeftFellForRight+1).toDouble()/(timesLeftAvoidedRight+1)
        val right = (timesRightFellForLeft+1).toDouble()/(timesRightAvoidedLeft+1)

        return (left+right)/2.0f
    }
}
