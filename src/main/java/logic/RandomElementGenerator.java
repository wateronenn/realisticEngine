package logic;

import component.Element;
import component.Unit;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Collections.shuffle;

/**
 * Utility class responsible for assigning random elements to units
 * and retrieving corresponding element images.
 *
 * <p>This class provides helper methods used during the element rolling
 * phase of the game. Elements are randomly selected and applied to
 * heroes or other units participating in the battle.
 */
public class RandomElementGenerator {

    /**
     * Assigns random elements to the provided list of units.
     *
     * <p>This method:
     * <ul>
     *     <li>Randomizes the order of available {@link Element} values</li>
     *     <li>Selects the first three elements</li>
     *     <li>Assigns one element to each of the first three units</li>
     * </ul>
     *
     * <p>The assigned elements are also returned in a list for UI or
     * game state tracking.
     *
     * @param units list of units that will receive random elements
     * @return list of assigned elements
     */
    public static ArrayList<Element> getRandomElement(ArrayList<? extends Unit> units) {
        ArrayList<Element> elementsList = new ArrayList<>(Arrays.asList(Element.values()));
        shuffle(elementsList);

        ArrayList<Element> elements = new ArrayList<>();

        System.out.println(units.size());

        for (int i = 0; i < 3; i++) {
            Element e = elementsList.get(i);
            Unit u = units.get(i);

            elements.add(e);
            u.setElement(e);
        }

        return elements;
    }

    /**
     * Returns the image corresponding to a specific element type.
     *
     * <p>The image is loaded from the resources directory using the
     * following path format:
     * <pre>
     * /Element/ELEMENT_NAME.png
     * </pre>
     *
     * <p>If the resource cannot be found, an {@link IllegalArgumentException}
     * is thrown.
     *
     * @param elements element whose image will be loaded
     * @return JavaFX {@link Image} representing the element icon
     * @throws IllegalArgumentException if the element image resource is missing
     */
    public static Image getElementImage(Element elements) {
        String path = "/Element/" + elements.toString() + ".png";

        URL url = RandomElementGenerator.class.getResource(path);

        if (url == null) {
            throw new IllegalArgumentException("Missing element image: " + path);
        }

        return new Image(url.toExternalForm());
    }
}