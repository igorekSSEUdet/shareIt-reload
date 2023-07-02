package ru.practicum.shareit.jsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.repository.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.booking.repository.model.Booking.Status.WAITING;

@JsonTest
public class BookingJsonTest {


    @Autowired
    private JacksonTester<Booking> jacksonTester;

    @Test
    public void checkBookingJsonFormat() throws IOException {
        Item item = Item.builder().name("itemName").description("desc").build();
        User user = User.builder().name("userName").build();
        Booking booking = Booking.builder().id(1L).status(WAITING).endTime(now().plusDays(2))
                .startTime(now().plusDays(1))
                .item(item).booker(user).build();

        JsonContent<Booking> result = jacksonTester.write(booking);

        System.out.println("\\n" + result.getJson());
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.item.id");
        assertThat(result).hasJsonPath("$.item.description");
        assertThat(result).extractingJsonPathStringValue("$.item.id").isEqualTo(item.getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(item.getDescription());
    }

}
