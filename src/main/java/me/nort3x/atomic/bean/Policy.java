package me.nort3x.atomic.bean;

import me.nort3x.atomic.annotation.Atomic;

import java.util.function.Consumer;

@Atomic
public abstract class Policy implements Consumer<DependencyGrapher> {
}
