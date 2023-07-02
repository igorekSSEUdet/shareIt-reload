package ru.practicum.shareit.errorHandlerTests;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ErrorHandlerTests {


    private ErrorHandler handler = new ErrorHandler();

    @Test
    public void incorrectDataHandlerTest() {
        ErrorResponse response = handler.incorrectDataHandler(new IncorrectDataException("ex"));
        System.out.println(response);
        assertEquals(response.getError(), "ex");

    }

    @Test
    public void entityNotFoundExceptionHandlerTest() {
        ErrorResponse response = handler.entityNotFoundExceptionHandler(new EntityNotFoundException("ex"));
        System.out.println(response);
        assertEquals(response.getError(), "ex");

    }

    @Test
    public void logicExceptionHandlerTest() {
        ErrorResponse response = handler.logicExceptionHandler(new LogicException("ex"));
        System.out.println(response);
        assertEquals(response.getError(), "ex");

    }

    @Test
    public void throwableHandlerTest() {
        ErrorResponse response = handler.throwableHandler(new Throwable("ex"));
        System.out.println(response);
        assertEquals(response.getError(), "ex");

    }

    @Test
    public void dataIntegrityViolationExceptionHandler() {
        ErrorResponse response = handler.throwableHandler(new DataIntegrityViolationException("ex"));
        System.out.println(response);
        assertEquals(response.getError(), "ex");

    }
}
