package org.book2words.screens

import android.app.Fragment
import android.app.ListFragment
import android.os.Bundle
import android.widget.ArrayAdapter
import org.book2words.models.LibraryDictionary

class DictionaryFragment : ListFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = arguments
        val dict: LibraryDictionary = args.getParcelable(DICTIONARY_KEY)
        val file = dict.path
        if (file.exists()) {
            val list = file.readLines(Charsets.UTF_8).toSortedSet().toList();
            listAdapter = ArrayAdapter(
                    activity,
                    android.R.layout.simple_list_item_1, android.R.id.text1,
                    list)
        }

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