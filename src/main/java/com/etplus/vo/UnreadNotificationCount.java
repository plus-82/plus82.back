package com.etplus.vo;

public record UnreadNotificationCount(
    boolean hasUnreadNotification,
    long count
) {

}
