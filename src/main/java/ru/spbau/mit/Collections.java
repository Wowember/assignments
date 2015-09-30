package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wowember on 29.09.2015.
 */

public abstract class Collections{

    public static <A, R> Iterable<R> map(final Function1<A, R> f, final Iterable<A> a) {
        List<R> list = new ArrayList<>();
        for (A el: a) {
            list.add(f.apply(el));
        }
        return list;
    }

    public static <A> Iterable<A> filter(final Predicate<A> p, final Iterable<A> a) {
        List<A> list = new ArrayList<>();
        for (A el: a) {
            if (p.apply(el)) {
                list.add(el);
            }
        }
        return list;
    }

    public static <A> Iterable<A> takeWhile(final Predicate<A> p, final Iterable<A> a) {
        List<A> list = new ArrayList<>();
        for (A el: a) {
            if (!p.apply(el)) {
                break;
            }
            list.add(el);
        }
        return list;
    }

    public static <A> Iterable<A> takeUnless(final Predicate<A> p, final Iterable<A> a) {
        return takeWhile(p.not(), a);
    }

    public static <A, B> B foldr(final Function2<A, B, B> f, final Iterable<A> a, B start) {
        List<A> list = new ArrayList<>();
        for (A el: a) {
            list.add(el);
        }
        for (int i = list.size() - 1; i >=0 ;i--)
            start = f.apply(list.get(i), start);
        return start;
    }

    public static <A, B> B foldl(final Function2<B, A, B> f, final Iterable<A> a, B start) {
        for (A el: a) {
            start = f.apply(start, el);
        }
        return start;
    }

}
