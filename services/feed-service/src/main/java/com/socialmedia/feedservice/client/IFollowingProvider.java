package com.socialmedia.feedservice.client;

import java.util.List;

public interface IFollowingProvider {
    List<String> getFollowingUserIds(String userId);
}
