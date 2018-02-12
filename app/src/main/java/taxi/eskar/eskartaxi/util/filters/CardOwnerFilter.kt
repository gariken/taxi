package taxi.eskar.eskartaxi.util.filters

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned


class CardOwnerFilter : InputFilter {

    private companion object {
        private const val PATTERN_OWNER = "[A-Z]*[ ]?[A-Z]*"
    }

    private val regex = Regex(PATTERN_OWNER)

    override fun filter(src: CharSequence, start: Int, end: Int, dst: Spanned, dstart: Int, dend: Int): CharSequence? {
        return if (src is SpannableStringBuilder) {
            if (src.matches(regex).not()) {
                src.delete(start, end)
            } else src
        } else {
            val filteredStringBuilder = StringBuilder(src)
            if (filteredStringBuilder.matches(regex).not()) {
                filteredStringBuilder.delete(start, end)
            } else filteredStringBuilder
        }
    }

}