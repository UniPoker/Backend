package utils;

import cards.Card;

import java.util.HashMap;
import java.util.Map;

/**
 * adapted from
 * http://stackoverflow.com/questions/10429230/map-with-multiple-value-types-with-advantages-of-generics
 */
public class TypedMap {


    private Map<AbstractKey<?>, Object> map = new HashMap<AbstractKey<?>, Object>();

    public <T> T get(AbstractKey<T> key) {
        return key.getType().cast(map.get(key));
    }

    public <T> T put(AbstractKey<T> key, T value) {
        return key.getType().cast(map.put(key, key.getType().cast(value)));
    }

    public static interface AbstractKey<K> {

        Class<K> getType();
    }

    public static enum StringKey implements AbstractKey<String> {

        username, B;

        public Class<String> getType() {
            return String.class;
        }
    }

    public static enum BooleanKey implements AbstractKey<Boolean> {

        is_small_blind, is_big_blind, is_active, show_cards;

        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    public static enum CardArrayKey implements AbstractKey<Card[]> {

        hand_cards;

        public Class<Card[]> getType() {
            return Card[].class;
        }
    }

    public Map getMap(){
        return map;
    }
}