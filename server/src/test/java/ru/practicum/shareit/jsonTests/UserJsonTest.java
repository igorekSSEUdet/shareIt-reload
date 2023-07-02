package ru.practicum.shareit.jsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserJsonTest {

    @Autowired
    private JacksonTester<User> jacksonTester;

    @Test
    public void checkUserJsonFormat() throws IOException {
        User user = User.builder().name("userName").id(1L).email("mail@mail.ru").build();

        JsonContent<User> result = jacksonTester.write(user);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(user.getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(user.getId().intValue());

    }
}
