package com.ai.dixorai;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

// Singleton - User Preferences & Settings class ---->
// TODO: Retrieve from User Memory by using GSON
public class basic_user_data {
    private static basic_user_data instance;
    private static String user_name;
    private static ArrayList<Word> allWords = new ArrayList<>();
    private static ArrayList<Word> archiveWords = new ArrayList<>();
    private static final ArrayList<String> word_color_ids = new ArrayList<>();
    private static all_languages all_lang;
    private static String pref_lang_short_name;
    private static int pref_lang_index;
    private static boolean hasPrefLangIndex = false;
    private static int max_allWords_elements = 15; // Max Elements to show in Home Fragment, when this threshold is passed, last word of the list is removed and sent to archiveWords ArrayList
    private static int preferred_font; // Range [0,last_font_id], where last_font_id is from the succession 0,1,2,3,4,n and belongs to natural numbers
    private static boolean auto_define_words = false;
    private static int def_word_limit = 90;

    public static String getPref_lang_short_name() {
        return pref_lang_short_name;
    }

    public static void setPref_lang_short_name(String pref_lang_short_name) {
        basic_user_data.pref_lang_short_name = pref_lang_short_name;
    }

    public static void setHasPrefLangIndex(boolean hasPrefLangIndex) {
        basic_user_data.hasPrefLangIndex = hasPrefLangIndex;
    }

    public static int getDef_word_limit() {
        return def_word_limit;
    }

    public static void setDef_word_limit(int def_word_limit) {
        basic_user_data.def_word_limit = def_word_limit;
    }

    public static boolean isAuto_define_words() {
        return auto_define_words;
    }

    public static void setAuto_define_words(boolean auto_define_words) {
        basic_user_data.auto_define_words = auto_define_words;
    }

    public static int getWord_color_palette_size(){
        return word_color_ids.size();
    }

    // Public method to get the instance of the Singleton class
    public static synchronized basic_user_data getInstance() {
        // Check if the instance is null, create it if necessary
        if (instance == null) {
            instance = new basic_user_data();
        }
        return instance;
    }

    public static boolean isHasPrefLangIndex() {
        return hasPrefLangIndex;
    }

    private basic_user_data() {
        preferred_font = 0;
    }

    public static int getMax_allWords_elements() {
        return max_allWords_elements;
    }

    public static void setMax_allWords_elements(int max_allWords_elements) {
        basic_user_data.max_allWords_elements = max_allWords_elements;
    }

    public static void setInstance(basic_user_data instance) {
        basic_user_data.instance = instance;
    }

    public static ArrayList<String> getWord_color_ids(){
        return word_color_ids;
    }
    public static void addWordColor(String color){
        basic_user_data.word_color_ids.add(color);
    }

    public static int getPreferred_font() {
        return preferred_font;
    }

    public static void setPreferred_font(int preferred_font) {
        basic_user_data.preferred_font = preferred_font;
    }

    public static String getUser_name() {
        return user_name;
    }

    public static int getPref_lang_index() {
        return pref_lang_index;
    }

    public static void addWordTag(Tag tag, int word_pos){
        if(!basic_user_data.getAllWords().get(word_pos).getTags().contains(tag)){
            basic_user_data.getAllWords().get(word_pos).getTags().add(tag);
        }
    }
    public static ArrayList<Tag> getAllExistingTags(){
        ArrayList<Tag> result = new ArrayList<>();
        for(Word word_iterator : basic_user_data.getAllWords()){
            for(Tag tag_iterator : word_iterator.getTags()){
                if(!result.contains(tag_iterator)){
                    result.add(tag_iterator);
                }
            }
        }
        return result;
    }
    public static ArrayList<Tag> getAllExistingTags(int word_pos_for_tags_exclusion){
        ArrayList<Tag> result = new ArrayList<>();
        ArrayList<Tag> tags_excluded = basic_user_data.getAllWords().get(word_pos_for_tags_exclusion).getTags();
        for(Word word_iterator : basic_user_data.getAllWords()){
            for(Tag tag_iterator : word_iterator.getTags()){
                if(!result.contains(tag_iterator) && !tags_excluded.contains(tag_iterator)){
                    result.add(tag_iterator);
                }
            }
        }
        return result;
    }

    public static String getEarliestBookDate(String book_name){
        ArrayList<Word> bookWords = getAllWords(new BookTag(book_name));
        List<String> dates = Arrays.asList();
        for(Word current_word : bookWords){
            dates.add(current_word.getDate());
        }
        String earliestDate = dates.stream()
                .min(Comparator.comparing(date -> {
                    String[] parts = date.split("/");
                    // Convert dd/mm/yy to yyyy-mm-dd for easier comparison
                    return String.format("20%s-%s-%s", parts[2], parts[1], parts[0]);
                }))
                .orElse(null);
        return earliestDate;
    }
    public static boolean searchBook(String book_name){
        ArrayList<BookTag> allBooks = getAllExistingBookTags();
        for(int i = 0 ; i < allBooks.size() ; ++i){
            BookTag j = allBooks.get(i);
            if(j.getTag_name().equals(book_name)){
                return true; // is there
            }
        }
        return false; // isn't there
    }

    public static ArrayList<BookTag> getAllExistingBookTags(){
        ArrayList<BookTag> result = new ArrayList<>();
        for(Word word_iterator : basic_user_data.getAllWords()){
            for(BookTag tag_iterator : word_iterator.getBookTags()){
                if(!result.contains(tag_iterator)){
                    result.add(tag_iterator);
                }
            }
        }
        return result;
    }
    public static ArrayList<BookTag> getAllExistingBookTags(int word_pos_for_tags_exclusion){
        ArrayList<BookTag> result = new ArrayList<>();
        ArrayList<BookTag> tags_excluded = basic_user_data.getAllWords().get(word_pos_for_tags_exclusion).getBookTags();
        for(Word word_iterator : basic_user_data.getAllWords()){
            for(BookTag tag_iterator : word_iterator.getBookTags()){
                if(!result.contains(tag_iterator) && !tags_excluded.contains(tag_iterator)){
                    result.add(tag_iterator);
                }
            }
        }
        return result;
    }

    public static ArrayList<Word> getArchiveWords() {
        return archiveWords;
    }

    public static void setArchiveWords(ArrayList<Word> archiveWords) {
        basic_user_data.archiveWords = archiveWords;
    }

    public static void archiveWord(Word word){
        basic_user_data.archiveWords.add(0,word);
        int i = 0;
        for(Word word_iterator : basic_user_data.allWords){
            if(word_iterator.getWord().equals(word.getWord())){
                basic_user_data.allWords.remove(i);
                return;
            }
            ++i;
        }
    }

    public static void archiveAllWords(){
        while(!allWords.isEmpty()){
            archiveWords.add(0, allWords.get(0));
            allWords.remove(0);
        }
    }

    public static void archiveAllWords(BookTag book){
        ArrayList<Word> wordsToArchive = new ArrayList<>();
        for(Word word : allWords){
            if(word.getBookTags().contains(book)){
                archiveWords.add(0,word);
                wordsToArchive.add(0,word);
            }
        }
        for(Word word : allWords){
            if(wordsToArchive.contains(word)){
                allWords.remove(word);
            }
        }
    }

    public static void undoArchiveAllWords(ArrayList<Word> words_archived){
        while(!words_archived.isEmpty()){
            for(Word word_iterator : archiveWords){
                if(word_iterator.getWord().equals(words_archived.get(0))){
                    archiveWords.remove(word_iterator);
                    allWords.add(0,word_iterator);
                }
            }
        }
    }

    public static void unarchiveWord(Word word){
        basic_user_data.allWords.add(0,word);
        int i = 0;
        for(Word word_iterator : archiveWords){
            if(word_iterator.getWord().equals(word.getWord())){
                basic_user_data.archiveWords.remove(i);
                return;
            }
            ++i;
        }
    }

    public static void removeAllWords(){
        allWords = new ArrayList<>();
    }


    public static void setPref_lang_index(int pref_lang_index) {
        basic_user_data.pref_lang_index = pref_lang_index;
        basic_user_data.hasPrefLangIndex = true;
    }

    public static void replaceWordData(int pos, Word word){
        basic_user_data.allWords.set(pos,word);
    }

    public static void setUser_name(String user_name) {
        basic_user_data.user_name = user_name;
    }

    public static ArrayList<Word> getAllWords() {
        return allWords;
    }

    public static ArrayList<Word> getAllWords(BookTag tag){
        ArrayList<Word> allBookWords = new ArrayList<>();
        for(Word word_iterator : allWords){
            for(BookTag book_tag_it : word_iterator.getBookTags()){
                if(book_tag_it.getTag_name().equals(tag.getTag_name())){
                    allBookWords.add(0,word_iterator);
                    break;
                }
            }
        }
        return allBookWords;
    }

    public static ArrayList<Word> getAllWords(Tag tag){
        ArrayList<Word> allTagWords = new ArrayList<>();
        for(Word word_iterator : allWords){
            for(Tag tag_it : word_iterator.getTags()){
                if(tag_it.getTag_name().equals(tag.getTag_name())){
                    allTagWords.add(0, word_iterator);
                    break;
                }
            }
        }
        return allTagWords;
    }

    public static void setAllWords(ArrayList<Word> allWords) {
        basic_user_data.allWords = allWords;
    }

    public static void emptyDefs(){ // Used when preferred language is changed
        for(int i = 0 ; i < basic_user_data.getAllWords().size() ; ++i){
            if(!basic_user_data.getAllWords().get(i).getWord().isEmpty()){
                basic_user_data.getAllWords().get(i).setAi_generated_def(new String());
            }
        }
    }

    public static all_languages getAll_lang() {
        return all_lang;
    }
    public static void changeWordColor(int word_pos,String color){
        allWords.get(word_pos).setColor_id(color);
    }
    public static void archiveLastAllWords(){
        basic_user_data.archiveWords.add(0,basic_user_data.allWords.get(basic_user_data.allWords.size()-1));
        basic_user_data.allWords.remove(basic_user_data.getAllWords().size()-1);
    }
    public static void setAll_lang(all_languages all_lang) {
        basic_user_data.all_lang = all_lang;
    }

    public static void add_new_word(Word word){
        basic_user_data.allWords.add(0,word);
    }
    public static void add_new_word(int pos,Word word){
        basic_user_data.allWords.add(pos,word);
    }
    public static void remove_word(int pos){
        basic_user_data.allWords.remove(pos);
    }
    public static void add_word_ai_generated_def(int pos, String ai_generated_def){
        basic_user_data.allWords.get(pos).setAi_generated_def(ai_generated_def);
    }
    public static void setExpandedOnceTrue(int word_pos){
        basic_user_data.allWords.get(word_pos).setExpanded_once(true);
    }

    public static boolean isnull(){
        return instance == null;
    }
}
