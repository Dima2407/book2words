package org.book2words.database;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.book2words.R;
import org.book2words.data.DataContext;
import org.book2words.database.model.WordDefinition;
import org.book2words.translate.core.Definition;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by dima on 24.04.17.
 */

public class RawLoader {

    private static final String TAG = RawLoader.class.getCanonicalName();

    private XPath xPath;
    private Executor executor;
    private Resources resources;
    private int[] arrayOfResources = {R.raw.a, R.raw.b, R.raw.c, R.raw.d, R.raw.e, R.raw.f, R.raw.g, R.raw.h, R.raw.i, R.raw.j, R.raw.k,
            R.raw.l, R.raw.m, R.raw.n, R.raw.o, R.raw.p, R.raw.q, R.raw.r, R.raw.s, R.raw.t, R.raw.u, R.raw.v, R.raw.w, R.raw.x, R.raw.y, R.raw.z};
 /*   private HashMap<Character, Integer> map = new HashMap<Character, Integer>(){{
        put('a', R.raw.a);
        put('b', R.raw.b);
        put('c', R.raw.c);
        put('d', R.raw.d);
        put('e', R.raw.e);
        put('f', R.raw.f);
        put('g', R.raw.g);
        put('h', R.raw.h);
        put('i', R.raw.i);
        put('j', R.raw.j);
        put('k', R.raw.k);
        put('l', R.raw.l);
        put('m', R.raw.m);
        put('n', R.raw.n);
        put('o', R.raw.o);
        put('p', R.raw.p);
        put('q', R.raw.q);
        put('r', R.raw.r);
        put('s', R.raw.s);
        put('t', R.raw.t);
        put('u', R.raw.u);
        put('v', R.raw.v);
        put('w', R.raw.w);
        put('x', R.raw.x);
        put('y', R.raw.y);
        put('z', R.raw.z);
    }};*/

    public RawLoader(Resources resources){
        this.resources = resources;
        xPath = XPathFactory.newInstance().newXPath();
        executor = Executors.newSingleThreadExecutor();
    }

  /*  public void transportRawToBb(Context context){
        executor.execute(fromRawToDB(context));
    }*/

    public void fromRawToDB(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // for (Map.Entry entry : map.entrySet()) {
                for (int resourceId : arrayOfResources) {
                    String expression = "/defs/d";
                    InputSource source = new InputSource(resources.openRawResource(resourceId));
                    try {
                        NodeList nodes = (NodeList) xPath.evaluate(expression, source, XPathConstants.NODESET);
                        if (nodes == null) {
                            Log.i(TAG, " nodes = null !");
                        }
                        Log.i(TAG, "nodes lenght = " + nodes.getLength());
                        for (int i = 0; i < nodes.getLength(); i++) {
                            Node node = nodes.item(i);
                            NamedNodeMap attributes = node.getAttributes();
                            WordDefinition word = new WordDefinition();
                            word.setText(attributes.getNamedItem("v").getNodeValue());
                            word.setTranslate(attributes.getNamedItem("tr").getNodeValue());
                            word.setPos(attributes.getNamedItem("p").getNodeValue());
                            word.setTranscription(attributes.getNamedItem("ts").getNodeValue());
                            DataContext.Companion.getDictionaryDao((Application) context).addWord(word);
                        }
                    }
                    catch (XPathExpressionException e) {
                        e.printStackTrace();
                    }

                }
                for (WordDefinition word : DataContext.Companion.getDictionaryDao((Application) context).getAllWords()) {
                    Log.i(TAG, "word = " + word.getText());
                }
            }
        }).start();

    }
}
