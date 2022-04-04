package com.ervelus.model;

/**
 * Enum for friend requests status.
 * Accepted - can be only in both friend lists,
 * Waiting - user sent request, waiting for other user's action,
 * Incoming - another user sent request, user should either accept or reject it.
 * Rejected requests are deleted.
 */
public enum FriendStatus {
    ACCEPTED,
    WAITING,
    INCOMING
}
