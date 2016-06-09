package org.book2words.screens

import android.app.Fragment
import android.app.ListFragment
import android.os.Bundle
import android.widget.ArrayAdapter
import org.book2words.R
import org.book2words.core.FileStorage
import org.book2words.dao.LibraryDictionary
import java.io.FileInputStream
import java.util.*

class DictionaryFragment : ListFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = arguments
        val book: LibraryDictionary = args.getParcelable(DICTIONARY_KEY)
        val list = TreeSet<String>()
        if (book.id != -1L) {
            val file = FileStorage.createDictionaryFile(book)
            if (file.exists()) {
                val bos = FileInputStream(file).bufferedReader(Charsets.UTF_8)
                bos.forEachLine {
                    list.add(it)
                }
                bos.close()
            }
        } else {
            val strings = resources.getStringArray(R.array.worlds_english)
            list.addAll(strings)
        }
        listAdapter = ArrayAdapter(
                activity,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                ArrayList(list))
    }

    companion object {

        private val DICTIONARY_KEY = "book"

        fun create(root: LibraryDictionary?): Fragment {
            val fragment = DictionaryFragment()

            if (root != null) {
                val args = Bundle()
                args.putParcelable(DICTIONARY_KEY, root)
                fragment.arguments = args
            }
            return fragment
        }
    }
}