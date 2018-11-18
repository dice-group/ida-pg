package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) {
        List<String> tokens = new ArrayList<String>();
        String SENTENCE_START = "<s>";
        String SENTENCE_END = "</s>";

        String text1 = "It was the pure Language of the World. It required no explanation, just as the universe needs none as it travels through endless time. What the boy felt at that moment was that he was in the presence of the only woman in his life, and that, with no need for words, she recognized the same thing. He was more certain of it than of anything in the world. He had been told by his parents and grandparents that he must fall in love and really know a person before becoming committed. But maybe people who felt that way had never learned the universal language. Because, when you know that language, it's easy to understand that someone in the world awaits you, whether it's in the middle of the desert or in some great city. And when two such people encounter each other, and their eyes meet, the past and the future become unimportant. There is only that moment, and the incredible certainty that everything under the sun has been written by one hand only. It is the hand that evokes love, and creates a twin soul for every person in the world. Without such love, one's dreams would have no meaning.";
//        String[] array = text1.split(" ");
        text1 = text1.toLowerCase();
        Pattern pattern = Pattern.compile("[\\w.?!]+");
        Matcher matcher = pattern.matcher(text1);
        while(matcher.find()){
            if(tokens.isEmpty()){
                tokens.add(SENTENCE_START);
            }
            else if(matcher.group().contains(".")){

                String s = matcher.group().replace(".","");
                tokens.add(s);
                tokens.add(SENTENCE_END);
                tokens.add(SENTENCE_START);


            }
            else if(matcher.group().contains("!")){

                String s = matcher.group().replace("!","");
                tokens.add(s);
                tokens.add(SENTENCE_END);
                tokens.add(SENTENCE_START);

            }
            else if(matcher.group().contains("?")){

                String s = matcher.group().replace("?","");
                tokens.add(s);
                tokens.add(SENTENCE_END);
                tokens.add(SENTENCE_START);

            }
            else{

                tokens.add(matcher.group());
            }
        }
       System.out.println(tokens.toString());
//        return tokens;
    }
}
