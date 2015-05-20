package org.book2words.screens

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.easydictionary.app.R
import com.easydictionary.app.SplitActivity
import org.book2words.dao.LibraryBook
import org.book2words.data.DataContext

public class DictionaryDialogListFragment : DialogFragment() {

    private var book : LibraryBook? = null;

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle(R.string.app_name)
        builder.setNegativeButton(android.R.string.no, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        });
        builder.setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                openSplitActivity()
            }
        })
        val dictionaries = DataContext.getUserDictionaries()
        val marks = BooleanArray(dictionaries.size())
        val items = Array(dictionaries.size(), {
            marks.set(it, dictionaries.get(it).getUse())
            dictionaries.get(it).getName()
        })

        builder.setMultiChoiceItems(items, marks, null);
        return builder.create();
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        book = getArguments().getParcelable(EXTRA_BOOK)
    }

    private fun openSplitActivity() {
        val intent = Intent(getActivity(), javaClass<SplitActivity>())
        intent.putExtra(SplitActivity.EXTRA_BOOK, book)
        startActivity(intent)
    }

    companion object {

        private val EXTRA_BOOK = "_book"

        public fun create(book: LibraryBook): DialogFragment {
            val fragment = DictionaryDialogListFragment();

            val args = Bundle()
            args.putParcelable(EXTRA_BOOK, book)
            fragment.setArguments(args)

            return fragment
        }
    }
}
