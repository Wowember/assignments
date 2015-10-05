package ru.spbau.mit;

/**
 * Created by Wowember on 29.09.2015.
 */
public abstract class Predicate<A> extends Function1<A, Boolean> {

    public <B extends A> Predicate<B> or(final Predicate<? super B> pr) {
        return new Predicate<B>() {
            @Override
            public Boolean apply(B x) {
                return Predicate.this.apply(x) || pr.apply(x);
            }
        };
    }

    public <B extends A> Predicate<B> and(final Predicate<? super B> pr) {
        return new Predicate<B>() {
            @Override
            public Boolean apply(B x) {
                return Predicate.this.apply(x) && pr.apply(x);
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
