package featuretest.text;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/text/package-tree.html
 * <p>
 * java.text.StringCharacterIterator (implements java.text.CharacterIterator)
 * <p>
 * java.text.CharacterIterator
 * java.text.AttributedCharacterIterator
 */

public class CharacterIteratorTest {
//    Interface CharacterIterator

//    StringCharacterIterator

//    AttributedCharacterIterator

    public static void main(String[] args) {
        CharacterIterator it = new StringCharacterIterator("AbCdeF");

        System.out.println(it.setIndex(1));

        System.out.println(it.getIndex());

        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            System.out.println(c);
            System.out.println(it.getIndex());
        }

        System.out.println(it.next());


        System.out.println(CharacterIterator.DONE);

        System.out.println(it.first());
        System.out.println(it.last());

        System.out.println(it.getBeginIndex());
        System.out.println(it.getEndIndex());
        System.out.println(it.getIndex());
        System.out.println();

        for (char c = it.last(); c != CharacterIterator.DONE; c = it.previous()) {
            System.out.println(c);
        }


    }
}


class AttributedCharacterIterator {
//    java.text.AttributedString
//    java.text.Annotation

//    java.text.AttributedCharacterIterator
//    AttributedCharacterIterator.Attribute
}
