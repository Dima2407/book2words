package org.book2words.screens

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import org.book2words.R
import org.book2words.data.Configs
import org.book2words.data.ConfigsContext
import org.book2words.services.LibraryService
import java.io.File

public class SelectBookDialogFragment : Fragment() {

    private var directoriesView: ListView? = null
    private var selectedView: EditText? = null
    private var currentRoot: File? = null
    private var selectedFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_root, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view!!.findViewById(android.R.id.button1).setOnClickListener({
            if (selectedFile != null) {
                LibraryService.addBook(getActivity(), selectedFile!!)
            }
            getActivity().finish()
        })

        view.findViewById(android.R.id.button2).setOnClickListener({
            getActivity().finish()
        })

        directoriesView = view.findViewById(android.R.id.list) as ListView
        selectedView = view.findViewById(R.id.edit_root) as EditText
        view.findViewById(R.id.button_back).setOnClickListener({
            if (Configs.getRelativePath(currentRoot!!) != "/") {
                selectedFile = null
                currentRoot = currentRoot!!.getParentFile()
                updateView()
            }
        })
        directoriesView!!.setOnItemClickListener { adapterView, view, i, l ->
            val item = adapterView.getItemAtPosition(i) as File
            if (item.isDirectory()) {
                currentRoot = item
                selectedFile = null
            } else {
                selectedFile = item
            }
            updateView()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val configs = ConfigsContext.getConfigs(getActivity())
        currentRoot = configs.getCurrentRoot()
        updateView()
    }

    private fun updateView() {
        if (selectedFile != null) {
            selectedView!!.setText(Configs.getRelativePath(selectedFile!!))
        } else {
            selectedView!!.setText(Configs.getRelativePath(currentRoot!!))
            val directories = currentRoot!!.listFiles({ file ->
                val ext = file.extension
                (file.isDirectory() || ext.equals("epub")) && !file.isHidden()
            })
            val directoriesAdapter = DirectoriesAdapter(getActivity(), directories)
            directoriesView!!.setAdapter(directoriesAdapter)
        }
    }


    private class DirectoriesAdapter(context: Context, directories: Array<File>?) : ArrayAdapter<File>(context,
            android.R.layout.simple_list_item_1,
            android.R.id.text1, directories) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var view = convertView
            if (view == null) {
                view = View.inflate(getContext(), android.R.layout.simple_list_item_1, null);
            }
            val textView = view as TextView
            val name = getItem(position).getName()
            textView.setText(name)
            return view
        }
    }
}