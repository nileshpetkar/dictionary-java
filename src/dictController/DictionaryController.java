/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictController;

import dictModel.DictionaryModel;
import dictView.DictionaryViewWindow;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author nilesh petkar
 */
public class DictionaryController {
    private final DictionaryModel dicModel = new DictionaryModel();
    private List list;
    
    public List getMeaning(String word){
       list = dicModel.getInfo(word);
       return list;
    }
    
    public List getSuggestion(String keyWord){
        list = dicModel.getSuggestList(keyWord);
        return list;
    }
    
    public String getDate(){
        SimpleDateFormat fm = new SimpleDateFormat("MMM d, y");
        return fm.format(new Date());
    }
    
    public List<String> getWordOfDay(){
        list = dicModel.getTodayWord();
        return list;
    }
    
    public void setRecentColumn(String word){
        dicModel.setRecentCol(word);
    }
    
    public List getRecentColumn(){
        list = dicModel.getRecentCol();
        return list;
    }
    
    public List getFavColumn(){
        list = dicModel.getFavCol();
        return list;
    }
    
    public boolean isFavourite(String word){
         return dicModel.isFav(word); //return true if word is fav
    }
    
    public void setFavourite(String word){
        dicModel.setFav(word);
    }
    
    public void unSetFavourite(String word){
         dicModel.unSetFav(word);
    }
    
    public boolean isFavouriteList(){  
       return dicModel.isFavList();   //return true if not empty
    }
    
    public boolean isRecentList(){
       return dicModel.isRecList();
    }
    public void clearRecentWord(String word){
        dicModel.clearRecWord(word);
    }
    
    public void clearFavWord(String word){
        dicModel.clearFavWord(word);
    }
    
    public void clearAllRecent(){
        dicModel.clearAllRecent();
    }
    public void clearAllFav(){
       dicModel.clearAllFav();
    }
    
    public List getUsefulWordList(String category){
       return dicModel.getWordsList(category);
    }
    
    public void startDictionary()
    {
         DictionaryViewWindow view = new DictionaryViewWindow();
         view.setVisible(true);
    }
    
}
