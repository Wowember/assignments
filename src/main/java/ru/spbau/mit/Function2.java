package ru.spbau.mit;

/**
 * Created by Wowember on 29.09.2015.
 */

public abstract class Function2<A, B, R> {

    public abstract R apply(A x, B y);

    public <C> Function2<A, B, C> compose(final Function1<? super R, ? extends C> g){
        return new Function2<A, B, C>() {
            @Override
            public C apply(A x, B y) {
                return g.apply(Function2.this.apply(x, y));
            }
        };
    }

    public Function1<B, R> bind1(final A x) {
        return new Function1<B, R>() {
            @Override
            public R apply(B y) {
                return Function2.this.apply(x, y);
            }
        };
    }

    public Function1<A, R> bind2(final B y) {
        return new Function1<A, R>() {
            @Override
            public R apply(A x) {
                return Function2.this.apply(x, y);
            }
        };
    }

    public Function1<A, Function1<B, R>> curry(){
        return new Function1<A, Function1<B, R>>() {
            @Override
            public Function1<B, R> apply(A x) {
                return Function2.this.bind1(x);
            }
        };
    }

}
