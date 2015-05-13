package org.screens

import android.app.Fragment
import android.app.ListFragment
import android.os.Bundle
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import com.easydictionary.app.R
import org.book2words.dao.LibraryDictionary
import java.io.FileInputStream
import java.util.ArrayList

public class DictionaryFragment : ListFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = getArguments();
        if (args != null) {
            val book: LibraryDictionary = args.getParcelable(DICTIONARY_KEY);
            val bos = FileInputStream(book.getPath()).reader(Charsets.UTF_8).buffered()

            val list = ArrayList<String>();
            bos.forEachLine {
                list.add(it);
            }
            setListAdapter(ArrayAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_checked, android.R.id.text1,
                    list))
            getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE)
        } else {
            val strings = getResources().getStringArray(R.array.widely_worlds)
            setListAdapter(ArrayAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1,
                    strings))
        }
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