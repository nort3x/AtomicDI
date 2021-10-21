package me.nort3x.atomic.Test1;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.Predefined;

@Atomic
public class Class1 {
    public @Predefined("littleInt") Integer num;
}
