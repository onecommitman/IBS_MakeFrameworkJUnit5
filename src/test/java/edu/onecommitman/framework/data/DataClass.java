package edu.onecommitman.framework.data;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class DataClass {
    public static Stream<Arguments> nameValues(){
        return Stream.of(
                Arguments.of("Достоевский Фёдор Михайлович"),
                Arguments.of("Пушкин Александр Сергеевич"),
                Arguments.of("Чехов Антон Павлович"));
    }
}
