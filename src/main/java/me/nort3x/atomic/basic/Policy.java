package me.nort3x.atomic.basic;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.bean.Provider;

import java.util.function.Consumer;

/**
 * inheritors of this class will act as extension and will be called after primary creation of graph
 */
@Atomic
public abstract class Policy implements Consumer<Provider> {
}
