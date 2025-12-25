package com.socialmedia.interactionservice.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class CursorPaginationService {

    public String encodeCursor(String createdAt, Long id) {
        String cursorString = createdAt + "," + id;
        return Base64.getUrlEncoder().encodeToString(cursorString.getBytes());
    }

    public String[] decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return null;
        }
        String decoded = new String(Base64.getUrlDecoder().decode(cursor));
        return decoded.split(",");
    }

    public LocalDateTime parseCursorTimestamp(String[] cursorParts) {
        if (cursorParts == null || cursorParts.length < 2) {
            return null;
        }
        return LocalDateTime.parse(cursorParts[0]);
    }

    public Long parseCursorId(String[] cursorParts) {
        if (cursorParts == null || cursorParts.length < 2) {
            return null;
        }
        return Long.parseLong(cursorParts[1]);
    }
}
