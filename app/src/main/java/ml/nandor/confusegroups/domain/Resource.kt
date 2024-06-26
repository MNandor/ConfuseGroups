package ml.nandor.confusegroups.domain

// Wrapper class around any object
// Stores not just the data but a state
// And possibly an error message
sealed class Resource<T>(val data: T? = null, val message: String? = null, val progressPercent: Int? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)

    class Progress<T>(progressPercent: Int?, data: T? = null, ):Resource<T>(data, null, progressPercent)

}