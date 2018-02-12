package taxi.eskar.eskartaxi.util

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import timber.log.Timber

class CardOwnerTextWatcher : TextWatcher {

    private companion object {
        private const val PATTERN_OWNER = "[A-Z]+[ ]?[A-Z]+"
        private const val ST = 0
    }

    private val regex = Regex(PATTERN_OWNER)
    private var previousText = ""

    override fun beforeTextChanged(src: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(src: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(src: Editable) {

    }
}