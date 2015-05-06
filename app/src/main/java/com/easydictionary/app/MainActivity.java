package com.easydictionary.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            transaction.add(android.R.id.content, LibraryListFragment.create(Configs.getBooksDirectory()), "root_files");
            transaction.commit();
        }
    }

    public static class LibraryListFragment extends ListFragment {

        private static final String DIRECTORY_ROOT_KEY = "dir_root";

        private static Fragment create(String root) {
            LibraryListFragment fragment = new LibraryListFragment();
            Bundle args = new Bundle();
            args.putString(DIRECTORY_ROOT_KEY, root);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new LibraryFileAdapter(getActivity(), files()));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            LibraryFile file = (LibraryFile) l.getItemAtPosition(position);
            if (file.isBook()) {
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.setData(file.takeUri());
                startActivity(intent);
            } else {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, LibraryListFragment.create(file.getPath()), "root_files");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        private List<LibraryFile> files() {
            File root = new File(getDirectoryRoot());
            List<LibraryFile> libraryFiles = new ArrayList<LibraryFile>();
            if (root.exists()) {
                File[] files = root.listFiles();
                for (File file : files) {
                    LibraryFile libraryFile = LibraryFile.tryFrom(file);
                    if (libraryFile != null) {
                        libraryFiles.add(libraryFile);
                    }
                }
            }
            return libraryFiles;
        }

        private String getDirectoryRoot() {
            Bundle arguments = getArguments();
            if (arguments != null) {
                return arguments.getString(DIRECTORY_ROOT_KEY, Configs.getBooksDirectory());
            }
            return Configs.getBooksDirectory();
        }

    }

    private static class LibraryFile {

        private String title;

        private String path;

        private int files;

        public static LibraryFile tryFrom(File f) {
            if (f.isHidden() || (f.isFile() && !f.getName().endsWith(".epub"))) {
                return null;
            }
            LibraryFile l = new LibraryFile();
            l.title = f.getName();
            l.path = f.getAbsolutePath();
            l.files = f.isFile() ? -1 : f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(".epub");
                }
            }).length;

            return l;
        }

        public boolean isBook() {
            return files == -1;
        }

        @Override
        public String toString() {
            return String.format("%-40s%s", title, (files == -1 ? "" : "->"));
        }

        public String getPath() {
            return path;
        }

        public Uri takeUri() {
            return Uri.fromFile(new File(path));
        }
    }

    private static class LibraryFileAdapter extends ArrayAdapter<LibraryFile> {

        public LibraryFileAdapter(Context context, List<LibraryFile> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
        }
    }


}
