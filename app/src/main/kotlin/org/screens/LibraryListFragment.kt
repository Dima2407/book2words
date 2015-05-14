package org.screens

import android.app.Fragment
import android.app.ListFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.easydictionary.app.Configs
import com.easydictionary.app.ContentActivity
import com.easydictionary.app.R
import com.easydictionary.app.SplitActivity
import com.nostra13.universalimageloader.core.ImageLoader
import org.book2words.B2WApplication
import org.book2words.Storage
import org.book2words.dao.LibraryBook
import org.book2words.services.LibraryService
import org.data.DataContext
import org.models.LibraryFile
import java.io.File
import java.util.ArrayList

public class LibraryListFragment : ListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val items = DataContext.getLibraryBookDao(this).loadAll()
        val adapter = LibraryFileAdapter(getActivity(), items)
        setListAdapter(adapter)
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.getItemId() == R.id.action_sync) {
            LibraryService.syncBooks(getActivity(), getDirectoryRoot())
            return true;
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getDirectoryRoot(): String {
        val arguments = getArguments()
        if (arguments != null) {
            return arguments.getString(DIRECTORY_ROOT_KEY, Configs.getBooksDirectory())
        }
        return Configs.getBooksDirectory()
    }

    companion object {

        private val DIRECTORY_ROOT_KEY = "dir_root"

        public fun create(root: String): Fragment {
            val args = Bundle()
            args.putString(DIRECTORY_ROOT_KEY, root)

            val fragment = LibraryListFragment()
            fragment.setArguments(args)
            return fragment
        }
    }

    private class LibraryFileAdapter(context: Context, objects: List<LibraryBook>)
    : ArrayAdapter<LibraryBook>(context, -1, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var view = convertView;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.list_item_book, null);
            }
            val titleView = view!!.findViewById(R.id.text_title) as TextView
            val authorsView = view!!.findViewById(R.id.text_author) as TextView

            val coverView = view!!.findViewById(R.id.image_cover) as ImageView

            val item = getItem(position);
            val coverUri = Storage.imageCoverUri(item.getId())

            ImageLoader.getInstance().displayImage(coverUri, coverView);
            titleView.setText(item.getName())
            authorsView.setText(item.getAuthors())

            view!!.findViewById(R.id.button_adapt).setOnClickListener({
                val intent = Intent(getContext(), javaClass<SplitActivity>())
                intent.putExtra(SplitActivity.EXTRA_BOOK, item)
                getContext().startActivity(intent)
            })

            if(item.getAdapted()){
                view!!.findViewById(R.id.button_adapt).setVisibility(View.GONE)
                view!!.findViewById(R.id.button_read).setVisibility(View.VISIBLE)
                view!!.findViewById(R.id.button_split).setVisibility(View.VISIBLE)
            }else{
                view!!.findViewById(R.id.button_adapt).setVisibility(View.VISIBLE)
                view!!.findViewById(R.id.button_read).setVisibility(View.GONE)
                view!!.findViewById(R.id.button_split).setVisibility(View.GONE)
            }
            return view
        }
    }
}