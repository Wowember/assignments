package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class Tests {

    Function1<Integer, Integer> doublingFunc = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer x) {
            return 2 * x;
        }
    };


    @Test
    public void Function1Testing() {
        assertTrue(doublingFunc.apply(3) == 6);
        assertTrue(doublingFunc.apply(-3) == -6);
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

    @Test
    public void Function2Testing() {
        assertTrue(multFunc.apply(7, 7) == 49);
        assertTrue(multFunc.apply(-7, 7) == -49);
        Function2<Integer, Integer, Integer> doubleMultFunc = multFunc.compose(doublingFunc);
        assertTrue(doubleMultFunc.apply(3, 3) == 18);
        Function1<Integer, Integer> doublingFunc2 = multFunc.bind1(2);
        assertTrue(doublingFunc.apply(10).equals(doublingFunc2.apply(10)));
        Function1<Integer, Integer> doublingFunc3 = multFunc.bind2(2);
        assertTrue(doublingFunc.apply(100).equals(doublingFunc3.apply(100)));
        assertTrue(doublingFunc3.apply(-30).equals(doublingFunc2.apply(-30)));
        Function1<Integer, Function1<Integer, Integer>> multFuncCurry = multFunc.curry();
        assertTrue(multFuncCurry.apply(3).apply(4).equals(multFunc.apply(3, 4)));
    }


}
