package org.screens

import android.app.ListFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.easydictionary.app.DictionaryActivity
import com.easydictionary.app.R
import org.book2words.B2WApplication
import org.book2words.dao.LibraryDictionary
import org.data.DataContext
import java.util.ArrayList

public class DictionaryListFragment : ListFragment() {

    private var items: MutableList<LibraryDictionary> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val header = View.inflate(getActivity(), R.layout.list_header_dictionary, null);
        header.setOnClickListener {
            startActivity(Intent(getActivity(), javaClass<DictionaryActivity>()))
        }
        getListView().addHeaderView(header, null, true);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE)
        getListView().setDividerHeight(6);
        android.R.layout.simple_list_item_multiple_choice
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        items = DataContext.getLibraryDictionaryDao(this).loadAll()
        items = DataContext.getUserDictionaries()
        val adapter = LibraryDictionaryAdapter(getActivity(), items)
        setListAdapter(adapter)
    }

    override fun onPause() {
        super.onPause()
        val checked = getListView().getCheckedItemPositions();
        var i = 0;
        while (i < checked.size()) {
            val index = checked.indexOfKey(i)
            items[index].setUse(checked.get(i))
            i++;
        }
        val application = getActivity().getApplication() as B2WApplication
        application.getDaoSession().getLibraryDictionaryDao().updateInTx(items)
    }

    override fun onResume() {
        super.onResume()
        items.forEachIndexed { i, item ->
            getListView().setItemChecked(i, item.getUse())
        }
    }

    private class LibraryDictionaryAdapter(context: Context, objects: List<LibraryDictionary>)
    : ArrayAdapter<LibraryDictionary>(context, -1, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var view = convertView;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.list_item_dictionary, null);
            }
            val titleView = view!!.findViewById(R.id.text_title) as TextView
            val item = getItem(position);

            view!!.findViewById(R.id.button_read).setOnClickListener{
                val intent = Intent(getContext(), javaClass<DictionaryActivity>())
                intent.putExtra(DictionaryActivity.EXTRA_DICTIONARY, item);
                getContext().startActivity(intent)
            }

            titleView.setText(item.getName())
            return view
        }
    }
}