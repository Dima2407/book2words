package org.models

public class Chapter(val key: String, val paragraphs: List<Paragraph>) {
    override fun toString(): String {
        return "${key} : ${paragraphs}";
    }

    public fun isEmpty(): Boolean {
        for(p in paragraphs){
            if(!p.words.isEmpty()){
                return false;
            }
        }
        return true;
    }
}