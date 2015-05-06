package org.screens

import android.app.Fragment
import android.app.ListFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.easydictionary.app.Configs
import com.easydictionary.app.ContentActivity
import org.models.LibraryFile
import java.io.File
import java.util.ArrayList

public class LibraryListFragment : ListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setListAdapter(LibraryFileAdapter(getActivity(), files()))
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val file = l!!.getItemAtPosition(position) as LibraryFile
        if (file.isBook()) {
            val intent = Intent(getActivity(), javaClass<ContentActivity>())
            intent.setData(file.asUri())
            startActivity(intent)
        } else {
            val transaction = getFragmentManager().beginTransaction()
            transaction.replace(android.R.id.content, LibraryListFragment.create(file.path), "root_files")
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun files(): List<LibraryFile> {
        val root = File(getDirectoryRoot())
        val libraryFiles = ArrayList<LibraryFile>()
        if (root.exists()) {
            val files = root.listFiles()
            for (file in files) {
                val libraryFile = LibraryFile.create(file)
                if (libraryFile != null) {
                    libraryFiles.add(libraryFile)
                }
            }
        }
        return libraryFiles
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

    private class LibraryFileAdapter(context: Context, objects: List<LibraryFile>) : ArrayAdapter<LibraryFile>(context, android.R.layout.simple_list_item_1, objects)
}