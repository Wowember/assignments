package ru.spbau.mit;

/**
 * Created by Wowember on 29.09.2015.
 */
public abstract class Predicate<A> extends Function1<A, Boolean> {

    public Predicate<A> or(final Function1<A, Boolean> f) {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return f.apply(x) || Predicate.this.apply(x);
            }
        };
    }

    public Predicate<A> and(final Function1<A, Boolean> f) {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return f.apply(x) && Predicate.this.apply(x);
            }
        };
    }

    public Predicate<A> not(){
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return !Predicate.this.apply(x);
            }
        };
    }

    public final static Predicate<Object> ALWAYS_TRUE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object x) {
            return true;
        }
    };

    public final static Predicate<Object> ALWAYS_FALSE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object x) {
            return false;
        }
    };

}
