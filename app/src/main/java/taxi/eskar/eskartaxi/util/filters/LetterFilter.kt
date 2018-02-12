package taxi.eskar.eskartaxi.util.filters

import android.text.InputFilter
import android.text.Spanned

class LetterFilter : InputFilter {
    override fun filter(src: CharSequence, start: Int, end: Int, dst: Spanned,
                        dstart: Int, dend: Int): CharSequence {
        if (src == "") {
            return src;
        }

        if (src.toString().matches(Regex("[a-zA-Zа-яА-Я]+"))) {
            return src;
        }

        return "";
    }
}