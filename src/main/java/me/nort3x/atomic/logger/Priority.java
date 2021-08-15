package me.nort3x.atomic.logger;

public enum Priority {
    DEBUG(0),
    VERY_VERBOSE(1),
    VERBOSE(2),
    IMPORTANT(3),
    VERY_IMPORTANT(4);
    final int comparable;

    Priority(int comparable) {
        this.comparable = comparable;
    }
}
