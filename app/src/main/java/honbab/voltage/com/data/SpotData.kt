package honbab.voltage.com.data

data class SpotData(
        val id: Long = counter++,
        val name: String,
        val city: String,
        val url: String
) {
    companion object {
        private var counter = 0L
    }
}
