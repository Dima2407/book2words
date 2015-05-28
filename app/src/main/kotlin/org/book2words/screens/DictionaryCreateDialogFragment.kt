package org.book2words.screens

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import org.book2words.R
import org.book2words.dao.LibraryBook
import org.book2words.services.net.B2WService

public class DictionaryCreateDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(getActivity())
        val book: LibraryBook = getArguments().getParcelable(EXTRA_BOOK)
        val editText = EditText(getActivity())
        editText.setEnabled(false)
        editText.setText(book.getName())
        builder.setTitle(R.string.app_name)
        builder.setNegativeButton(android.R.string.no, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        });
        builder.setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                B2WService.updateBook(getActivity(), book)

                val dictionaryTitle = editText.getText().toString()

                B2WService.addDictionary(getActivity(), dictionaryTitle)
            }
        })
        builder.setView(editText)
        return builder.create();
    }

    companion object {

        private val EXTRA_BOOK = "_book"

        public fun create(book: LibraryBook): DialogFragment {
            val fragment = DictionaryCreateDialogFragment();

            val args = Bundle()
            args.putParcelable(EXTRA_BOOK, book)
            fragment.setArguments(args)

            return fragment
        }
    }
}
