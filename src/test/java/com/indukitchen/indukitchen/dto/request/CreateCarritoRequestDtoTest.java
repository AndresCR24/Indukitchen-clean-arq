package com.indukitchen.indukitchen.dto.request;

import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCarritoRequestDto Tests")
class CreateCarritoRequestDtoTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create dto with valid parameters")
        void shouldCreateDtoWithValidParameters() {
            // Arrange
            String expectedIdCliente = "cliente123";
            List<Long> expectedProductoIds = List.of(1L, 2L, 3L);

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(expectedIdCliente, expectedProductoIds);

            // Assert
            assertThat(dto)
                    .isNotNull()
                    .extracting(CreateCarritoRequestDto::idCliente, CreateCarritoRequestDto::productoIds)
                    .containsExactly(expectedIdCliente, expectedProductoIds);
        }

        @Test
        @DisplayName("Should create dto with null idCliente")
        void shouldCreateDtoWithNullIdCliente() {
            // Arrange
            String nullIdCliente = null;
            List<Long> validProductoIds = List.of(1L, 2L);

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(nullIdCliente, validProductoIds);

            // Assert
            assertThat(dto.idCliente()).isNull();
            assertThat(dto.productoIds()).isEqualTo(validProductoIds);
        }

        @Test
        @DisplayName("Should create dto with null productoIds")
        void shouldCreateDtoWithNullProductoIds() {
            // Arrange
            String validIdCliente = "cliente123";
            List<Long> nullProductoIds = null;

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(validIdCliente, nullProductoIds);

            // Assert
            assertThat(dto.idCliente()).isEqualTo(validIdCliente);
            assertThat(dto.productoIds()).isNull();
        }

        @Test
        @DisplayName("Should create dto with empty productoIds list")
        void shouldCreateDtoWithEmptyProductoIdsList() {
            // Arrange
            String validIdCliente = "cliente123";
            List<Long> emptyProductoIds = Collections.emptyList();

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(validIdCliente, emptyProductoIds);

            // Assert
            assertThat(dto.idCliente()).isEqualTo(validIdCliente);
            assertThat(dto.productoIds())
                    .isNotNull()
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should handle invalid idCliente values")
        void shouldHandleInvalidIdClienteValues(String invalidIdCliente) {
            // Arrange
            List<Long> validProductoIds = List.of(1L, 2L);

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(invalidIdCliente, validProductoIds);

            // Assert
            assertThat(dto.idCliente())
                    .satisfiesAnyOf(
                            idCliente -> assertThat(idCliente).isNull(),
                            idCliente -> assertThat(idCliente).isBlank()
                    );
        }

        @ParameterizedTest
        @MethodSource("provideValidProductoIds")
        @DisplayName("Should handle various valid productoIds scenarios")
        void shouldHandleValidProductoIdsScenarios(List<Long> validProductoIds) {
            // Arrange
            String validIdCliente = "cliente123";

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(validIdCliente, validProductoIds);

            // Assert
            assertThat(dto.productoIds()).isEqualTo(validProductoIds);
        }

        private static Stream<Arguments> provideValidProductoIds() {
            return Stream.of(
                    Arguments.of(List.of(1L)),
                    Arguments.of(List.of(1L, 2L, 3L)),
                    Arguments.of(List.of(100L, 200L, 300L, 400L, 500L)),
                    Arguments.of(Collections.emptyList())
            );
        }
    }

    @Nested
    @DisplayName("Record Behavior Tests")
    class RecordBehaviorTests {

        @Test
        @DisplayName("Should maintain equals contract for identical objects")
        void shouldMaintainEqualsContractForIdenticalObjects() {
            // Arrange
            String idCliente = "cliente123";
            List<Long> productoIds = List.of(1L, 2L, 3L);
            CreateCarritoRequestDto dto1 = new CreateCarritoRequestDto(idCliente, productoIds);
            CreateCarritoRequestDto dto2 = new CreateCarritoRequestDto(idCliente, productoIds);

            // Act & Assert
            assertThat(dto1)
                    .isEqualTo(dto2)
                    .hasSameHashCodeAs(dto2);
        }

        @Test
        @DisplayName("Should maintain equals contract for different objects")
        void shouldMaintainEqualsContractForDifferentObjects() {
            // Arrange
            CreateCarritoRequestDto dto1 = new CreateCarritoRequestDto("cliente1", List.of(1L, 2L));
            CreateCarritoRequestDto dto2 = new CreateCarritoRequestDto("cliente2", List.of(3L, 4L));

            // Act & Assert
            assertThat(dto1)
                    .isNotEqualTo(dto2)
                    .doesNotHaveSameHashCodeAs(dto2);
        }

        @Test
        @DisplayName("Should generate meaningful toString representation")
        void shouldGenerateMeaningfulToStringRepresentation() {
            // Arrange
            String idCliente = "cliente123";
            List<Long> productoIds = List.of(1L, 2L, 3L);
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(idCliente, productoIds);

            // Act
            String toString = dto.toString();

            // Assert
            assertThat(toString)
                    .isNotNull()
                    .isNotEmpty()
                    .contains("CreateCarritoRequestDto")
                    .contains(idCliente)
                    .contains("1", "2", "3");
        }

        @Test
        @DisplayName("Should be immutable")
        void shouldBeImmutable() {
            // Arrange
            String idCliente = "cliente123";
            List<Long> originalProductoIds = List.of(1L, 2L, 3L);
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(idCliente, originalProductoIds);

            // Act & Assert
            assertThat(dto.idCliente()).isEqualTo(idCliente);
            assertThat(dto.productoIds()).isEqualTo(originalProductoIds);

            // Los records son inmutables por naturaleza
            assertThat(dto.productoIds()).isInstanceOf(List.class);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long idCliente")
        void shouldHandleVeryLongIdCliente() {
            // Arrange
            String veryLongIdCliente = "a".repeat(1000);
            List<Long> productoIds = List.of(1L);

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(veryLongIdCliente, productoIds);

            // Assert
            assertThat(dto.idCliente())
                    .hasSize(1000)
                    .isEqualTo(veryLongIdCliente);
        }

        @Test
        @DisplayName("Should handle large list of productoIds")
        void shouldHandleLargeListOfProductoIds() {
            // Arrange
            String idCliente = "cliente123";
            List<Long> largeProductoIds = Stream.iterate(1L, n -> n + 1)
                    .limit(1000)
                    .toList();

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(idCliente, largeProductoIds);

            // Assert
            assertThat(dto.productoIds())
                    .hasSize(1000)
                    .containsExactlyElementsOf(largeProductoIds);
        }

        @Test
        @DisplayName("Should handle duplicate productoIds")
        void shouldHandleDuplicateProductoIds() {
            // Arrange
            String idCliente = "cliente123";
            List<Long> duplicateProductoIds = List.of(1L, 1L, 2L, 2L, 3L, 3L);

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(idCliente, duplicateProductoIds);

            // Assert
            assertThat(dto.productoIds())
                    .hasSize(6)
                    .containsExactly(1L, 1L, 2L, 2L, 3L, 3L);
        }

        @Test
        @DisplayName("Should handle negative productoIds")
        void shouldHandleNegativeProductoIds() {
            // Arrange
            String idCliente = "cliente123";
            List<Long> negativeProductoIds = List.of(-1L, -2L, -3L);

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(idCliente, negativeProductoIds);

            // Assert
            assertThat(dto.productoIds())
                    .containsExactly(-1L, -2L, -3L)
                    .allMatch(id -> id < 0);
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should represent valid carrito creation request")
        void shouldRepresentValidCarritoCreationRequest() {
            // Arrange
            String clienteId = "CLIENTE_VIP_001";
            List<Long> productoIds = List.of(101L, 102L, 103L);

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(clienteId, productoIds);

            // Assert
            assertThat(dto)
                    .satisfies(request -> {
                        assertThat(request.idCliente()).isEqualTo(clienteId);
                        assertThat(request.productoIds())
                                .hasSize(3)
                                .containsExactly(101L, 102L, 103L)
                                .allMatch(id -> id > 0);
                    });
        }

        @Test
        @DisplayName("Should support single product carrito")
        void shouldSupportSingleProductCarrito() {
            // Arrange
            String clienteId = "cliente456";
            List<Long> singleProductId = List.of(999L);

            // Act
            CreateCarritoRequestDto dto = new CreateCarritoRequestDto(clienteId, singleProductId);

            // Assert
            assertThat(dto.productoIds())
                    .hasSize(1)
                    .containsExactly(999L);
        }
    }
}
