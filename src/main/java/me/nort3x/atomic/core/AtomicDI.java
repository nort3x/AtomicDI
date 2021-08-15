package me.nort3x.atomic.core;

import me.nort3x.atomic.core.internal.GreedyBag;
import me.nort3x.atomic.core.internal.Resolver;

public class AtomicDI {

    final Resolver rs;
    final GreedyBag gb;

    public AtomicDI() {
        // assemble
        gb = new GreedyBag();
        rs = new Resolver(gb);
    }


    private final static AtomicDI instance = new AtomicDI();

    public static AtomicDI getInstance() {
        return instance;
    }

    public Resolver getResolver() {
        return rs;
    }

    public GreedyBag getGreedyBag() {
        return gb;
    }
}
