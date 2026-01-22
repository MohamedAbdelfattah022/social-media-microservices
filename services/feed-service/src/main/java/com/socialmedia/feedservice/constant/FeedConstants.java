package com.socialmedia.feedservice.constant;


public final class FeedConstants {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 100;

    private FeedConstants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
