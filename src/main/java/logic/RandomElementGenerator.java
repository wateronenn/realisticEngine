package logic;

import component.Element;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Collections.shuffle;

public class RandomElementGenerator {
    public static ArrayList<Element> getRandomElement(){
        Element[] elementsArray = Element.values();
        ArrayList<Element> elementsList = new ArrayList<>(Arrays.asList(elementsArray));
        shuffle(elementsList);
        ArrayList<Element> elements = new ArrayList<>(3);
        for(int i=0;i<3;i++){
            elements.add(elementsList.get(i));
        }
        return elements;

    }
}
