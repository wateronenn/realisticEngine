package logic;

import component.Element;
import component.Unit.Unit;
import javafx.scene.image.Image;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Collections.shuffle;

public class RandomElementGenerator {
    public static ArrayList<Element> getRandomElement( ArrayList<? extends Unit> units){
        ArrayList<Element> elementsList = new ArrayList<>(Arrays.asList(Element.values()));
        shuffle(elementsList);
        ArrayList<Element> elements = new ArrayList<>();
        System.out.println(units.size());
        for(int i=0 ; i<3 ; i++){
            Element e = elementsList.get(i);
            //System.out.println();
            Unit u = units.get(i);
            elements.add(e);
            u.setElement(e);
        }
        return elements;
    }

    public static Image getElementImage(Element elements){
        String path = "/Element/" + elements.toString() +".png";
        URL url = RandomElementGenerator.class.getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("Missing element image: " + path);
        }
        return new Image(url.toExternalForm());
    }
}
