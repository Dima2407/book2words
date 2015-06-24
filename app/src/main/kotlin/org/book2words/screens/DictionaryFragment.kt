package org.book2words.screens

import android.app.Fragment
import android.app.ListFragment
import android.os.Bundle
import android.widget.ArrayAdapter
import org.book2words.R
import org.book2words.dao.LibraryDictionary
import java.io.FileInputStream
import java.util.ArrayList
import java.util.TreeSet

public class DictionaryFragment : ListFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = getArguments()
        val book: LibraryDictionary = args.getParcelable(DICTIONARY_KEY)
        val list = TreeSet<String>()
        if(book.getId() != -1L){
            val file = book.getPath()
            if(file.exists()) {
                val bos = FileInputStream(file).bufferedReader(Charsets.UTF_8)
                bos.forEachLine {
                    list.add(it)
                }
                bos.close()
            }
        }else {
            val strings = getResources().getStringArray(R.array.worlds_english)
            list.addAll(strings)
        }
        setListAdapter(ArrayAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                ArrayList(list)))
    }

    companion object {

        private val DICTIONARY_KEY = "book"

        public fun create(root: LibraryDictionary?): Fragment {
            val fragment = DictionaryFragment()

            if (root != null) {
                val args = Bundle()
                args.putParcelable(DICTIONARY_KEY, root)
                fragment.setArguments(args)
            }
            return fragment
        }
    }
}