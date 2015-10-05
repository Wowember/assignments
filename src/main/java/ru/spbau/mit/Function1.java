package ru.spbau.mit;

/**
 * Created by Wowember on 29.09.2015.
 */

public abstract class Function1<A, R> {

    public abstract R apply(A x);

    public <B> Function1<A, B> compose(final Function1<? super R, ? extends B> g) {
        return new Function1<A, B>() {
            @Override
            public B apply(A x) {
                return g.apply(Function1.this.apply(x));
            }
        };
    }

}
