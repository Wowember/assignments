package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wowember on 29.09.2015.
 */


import static org.junit.Assert.*;

public class Tests {

    Function1<Integer, Integer> doublingFunc = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer x) {
            return 2 * x;
        }
    };

    @Test
    public void testFunction1() {

        //testing "apply"
        assertTrue(doublingFunc.apply(3) == 6);
        assertTrue(doublingFunc.apply(-3) == -6);

        //testing "compose"
        Function1<Integer, Integer> doubleDoublingFunc = doublingFunc.compose(doublingFunc);
        assertTrue(doubleDoublingFunc.apply(3) == 12);
        assertTrue(doubleDoublingFunc.apply(-3) == -12);
        assertTrue(doublingFunc.apply(6).equals(doubleDoublingFunc.apply(3)));
        assertTrue(doublingFunc.apply(-6).equals(doubleDoublingFunc.apply(-3)));

    }

    Function2<Integer, Integer, Integer> multFunc = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer x, Integer y) {
            return x * y;
        }
    };

    Function2<Integer, Integer, Integer> minusFunc = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer x, Integer y) {
            return x - y;
        }
    };

    @Test
    public void testFunction2() {

        //testing "apply"
        assertTrue(multFunc.apply(7, 7) == 49);
        assertTrue(multFunc.apply(-7, 7) == -49);

        //testing "compose"
        Function2<Integer, Integer, Integer> doubleMultFunc = multFunc.compose(doublingFunc);
        assertTrue(doubleMultFunc.apply(3, 3) == 18);

        //testing "bind1"
        Function1<Integer, Integer> doublingFunc2 = multFunc.bind1(2);
        assertTrue(doublingFunc.apply(10).equals(doublingFunc2.apply(10)));

        //testing "bind2"
        Function1<Integer, Integer> doublingFunc3 = multFunc.bind2(2);
        assertTrue(doublingFunc.apply(100).equals(doublingFunc3.apply(100)));
        assertTrue(doublingFunc3.apply(-30).equals(doublingFunc2.apply(-30)));

        //testing "curry"
        Function1<Integer, Function1<Integer, Integer>> multFuncCurry = multFunc.curry();
        assertTrue(multFuncCurry.apply(3).apply(4).equals(multFunc.apply(3, 4)));

    }

    Predicate<Integer> lessThanSix = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x < 6;
        }
    };

    Predicate<Integer> lessThanTen = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x < 10;
        }
    };

    Predicate<Integer> lessThanTenFail = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            assertTrue(false);
            return x < 10;
        }
    };

    @Test
    public void testPredicate() {

        //test "apply"
        assertTrue(lessThanSix.apply(5));
        assertFalse(lessThanSix.apply(7));
        assertTrue(lessThanTen.apply(9));
        assertFalse(lessThanTen.apply(11));

        //testing "or"
        Predicate<Integer> lessThanSixOrLessThanTen = lessThanSix.or(lessThanTen);
        assertTrue(lessThanSixOrLessThanTen.apply(5));
        assertTrue(lessThanSixOrLessThanTen.apply(7));
        assertTrue(lessThanSixOrLessThanTen.apply(9));
        assertFalse(lessThanSixOrLessThanTen.apply(11));

        // testing laziness "or" , "and"
        Predicate<Integer> lessThanSixOrLessThanTenFail = lessThanSix.or(lessThanTenFail);
        Predicate<Integer> lessThanSixAndLessThanTenFail = lessThanSix.and(lessThanTenFail);
        assertTrue(lessThanSixOrLessThanTenFail.apply(5));
        assertFalse(lessThanSixAndLessThanTenFail.apply(8));

        //testing "and"
        Predicate<Integer> lessThanSixAndLessThanTen = lessThanSix.and(lessThanTen);
        assertTrue(lessThanSixAndLessThanTen.apply(5));
        assertFalse(lessThanSixAndLessThanTen.apply(7));
        assertFalse(lessThanSixAndLessThanTen.apply(9));
        assertFalse(lessThanSixAndLessThanTen.apply(11));

        //testing "not"
        Predicate<Integer> notLessThanSix = lessThanSix.not();
        assertNotEquals(notLessThanSix.apply(6), lessThanSix.apply(6));
        assertNotEquals(notLessThanSix.apply(7), lessThanSix.apply(7));

        //testing const predicate
        Predicate<Object> falsePredicate = Predicate.ALWAYS_FALSE;
        Predicate<Object> truePredicate = Predicate.ALWAYS_TRUE;
        assertNotEquals(falsePredicate.apply("asdf"), truePredicate.apply("asdf"));
        assertNotEquals(falsePredicate.apply(123), truePredicate.apply(123));
        assertNotEquals(falsePredicate.apply(false), truePredicate.apply(false));
        assertNotEquals(falsePredicate.apply(true), truePredicate.apply(true));
        assertNotEquals(falsePredicate.apply(23454), true);

    }



    @Test
    public void testCollections() {

        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(5);
        list.add(7);
        list.add(9);
        list.add(11);

        //testing "map"
        List<Integer> doubleList = (List<Integer>) Collections.map(doublingFunc, list);
        for (int i = 0; i < list.size(); i++) {
            assertTrue(list.get(i) * 2 == (int) doubleList.get(i));
        }

        //testing "filter"
        List<Integer> lessThanSixList = (List<Integer>) Collections.filter(lessThanSix, list);
        assertTrue(2 == lessThanSixList.size());
        for (Integer el: lessThanSixList) assertTrue(el < 6);

        List<Integer> notLessThanSixList = (List<Integer>) Collections.filter(lessThanSix.not(), list);
        assertTrue(3 == notLessThanSixList.size());
        for (Integer el: notLessThanSixList) assertTrue(el >= 6);

        //testing "takeWhile"
        List<Integer> lessThanSixTakeWhile = (List<Integer>) Collections.takeWhile(lessThanSix, list);
        assertTrue(2 == lessThanSixList.size());

        //testing "takeUnless"
        List<Integer> lessThanSixTakeUnless = (List<Integer>) Collections.takeUnless(lessThanSix.not(), list);
        assertTrue(2 == lessThanSixList.size());

        //testing "foldr", "foldl"
        assertEquals(7, (int) Collections.foldr(minusFunc, list, 0));
        assertEquals(-35, (int) Collections.foldl(minusFunc, list, 0));

    }
}
