package ru.practicum.shareit.utills;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserHttpHeadersTest {

    @Test
    public void userHttpHeadersTest() {
        UserHttpHeaders userHttpHeaders = new UserHttpHeaders();
        assertEquals(userHttpHeaders.hashCode(), 0);
        assertEquals(UserHttpHeaders.USER_ID, "X-Sharer-User-Id");
    }

}
