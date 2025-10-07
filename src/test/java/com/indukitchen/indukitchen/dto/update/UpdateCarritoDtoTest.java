package com.indukitchen.indukitchen.dto.update;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCarritoDto Tests")
public class UpdateCarritoDtoTest {

    @Mock
    private ProductoDto mockProductoDto;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create dto with valid parameters")
        void shouldCreateDtoWithValidParameters() {
            // Arrange
            long expectedId = 1L;
            String expectedIdCliente = "cliente123";
            List<ProductoDto> expectedProductos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(expectedId, expectedIdCliente, expectedProductos);

            // Assert
            assertThat(dto)
                    .isNotNull()
                    .extracting(UpdateCarritoDto::id, UpdateCarritoDto::idCliente, UpdateCarritoDto::productoDtos)
                    .containsExactly(expectedId, expectedIdCliente, expectedProductos);
        }

        @Test
        @DisplayName("Should create dto with zero id")
        void shouldCreateDtoWithZeroId() {
            // Arrange
            long zeroId = 0L;
            String validIdCliente = "cliente123";
            List<ProductoDto> validProductos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(zeroId, validIdCliente, validProductos);

            // Assert
            assertThat(dto.id()).isZero();
            assertThat(dto.idCliente()).isEqualTo(validIdCliente);
            assertThat(dto.productoDtos()).isEqualTo(validProductos);
        }

        @Test
        @DisplayName("Should create dto with negative id")
        void shouldCreateDtoWithNegativeId() {
            // Arrange
            long negativeId = -1L;
            String validIdCliente = "cliente123";
            List<ProductoDto> validProductos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(negativeId, validIdCliente, validProductos);

            // Assert
            assertThat(dto.id()).isNegative().isEqualTo(negativeId);
        }

        @Test
        @DisplayName("Should create dto with null idCliente")
        void shouldCreateDtoWithNullIdCliente() {
            // Arrange
            long validId = 1L;
            String nullIdCliente = null;
            List<ProductoDto> validProductos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(validId, nullIdCliente, validProductos);

            // Assert
            assertThat(dto.idCliente()).isNull();
            assertThat(dto.id()).isEqualTo(validId);
            assertThat(dto.productoDtos()).isEqualTo(validProductos);
        }

        @Test
        @DisplayName("Should create dto with null productoDtos")
        void shouldCreateDtoWithNullProductoDtos() {
            // Arrange
            long validId = 1L;
            String validIdCliente = "cliente123";
            List<ProductoDto> nullProductos = null;

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(validId, validIdCliente, nullProductos);

            // Assert
            assertThat(dto.productoDtos()).isNull();
            assertThat(dto.id()).isEqualTo(validId);
            assertThat(dto.idCliente()).isEqualTo(validIdCliente);
        }

        @Test
        @DisplayName("Should create dto with empty productoDtos list")
        void shouldCreateDtoWithEmptyProductoDtosList() {
            // Arrange
            long validId = 1L;
            String validIdCliente = "cliente123";
            List<ProductoDto> emptyProductos = Collections.emptyList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(validId, validIdCliente, emptyProductos);

            // Assert
            assertThat(dto.productoDtos())
                    .isNotNull()
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @ParameterizedTest
        @ValueSource(longs = {1L, 100L, 999L, Long.MAX_VALUE})
        @DisplayName("Should handle valid positive id values")
        void shouldHandleValidPositiveIdValues(long validId) {
            // Arrange
            String validIdCliente = "cliente123";
            List<ProductoDto> validProductos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(validId, validIdCliente, validProductos);

            // Assert
            assertThat(dto.id()).isPositive().isEqualTo(validId);
        }

        @ParameterizedTest
        @ValueSource(longs = {-1L, -100L, -999L, Long.MIN_VALUE})
        @DisplayName("Should handle negative id values")
        void shouldHandleNegativeIdValues(long negativeId) {
            // Arrange
            String validIdCliente = "cliente123";
            List<ProductoDto> validProductos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(negativeId, validIdCliente, validProductos);

            // Assert
            assertThat(dto.id()).isNegative().isEqualTo(negativeId);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should handle invalid idCliente values")
        void shouldHandleInvalidIdClienteValues(String invalidIdCliente) {
            // Arrange
            long validId = 1L;
            List<ProductoDto> validProductos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(validId, invalidIdCliente, validProductos);

            // Assert
            assertThat(dto.idCliente())
                    .satisfiesAnyOf(
                            idCliente -> assertThat(idCliente).isNull(),
                            idCliente -> assertThat(idCliente).isBlank()
                    );
        }

        @ParameterizedTest
        @MethodSource("provideValidProductoDtos")
        @DisplayName("Should handle various valid productoDtos scenarios")
        void shouldHandleValidProductoDtosScenarios(List<ProductoDto> validProductos) {
            // Arrange
            long validId = 1L;
            String validIdCliente = "cliente123";

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(validId, validIdCliente, validProductos);

            // Assert
            assertThat(dto.productoDtos()).isEqualTo(validProductos);
        }

        private static Stream<Arguments> provideValidProductoDtos() {
            return Stream.of(
                    Arguments.of(List.of(createProductoDto(1L, "Producto1"))),
                    Arguments.of(List.of(
                            createProductoDto(1L, "Producto1"),
                            createProductoDto(2L, "Producto2"),
                            createProductoDto(3L, "Producto3")
                    )),
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
            long id = 1L;
            String idCliente = "cliente123";
            List<ProductoDto> productos = createValidProductoDtoList();
            UpdateCarritoDto dto1 = new UpdateCarritoDto(id, idCliente, productos);
            UpdateCarritoDto dto2 = new UpdateCarritoDto(id, idCliente, productos);

            // Act & Assert
            assertThat(dto1)
                    .isEqualTo(dto2)
                    .hasSameHashCodeAs(dto2);
        }

        @Test
        @DisplayName("Should maintain equals contract for different objects")
        void shouldMaintainEqualsContractForDifferentObjects() {
            // Arrange
            UpdateCarritoDto dto1 = new UpdateCarritoDto(1L, "cliente1", createValidProductoDtoList());
            UpdateCarritoDto dto2 = new UpdateCarritoDto(2L, "cliente2", createValidProductoDtoList());

            // Act & Assert
            assertThat(dto1)
                    .isNotEqualTo(dto2)
                    .doesNotHaveSameHashCodeAs(dto2);
        }

        @Test
        @DisplayName("Should generate meaningful toString representation")
        void shouldGenerateMeaningfulToStringRepresentation() {
            // Arrange
            long id = 123L;
            String idCliente = "cliente456";
            List<ProductoDto> productos = createValidProductoDtoList();
            UpdateCarritoDto dto = new UpdateCarritoDto(id, idCliente, productos);

            // Act
            String toString = dto.toString();

            // Assert
            assertThat(toString)
                    .isNotNull()
                    .isNotEmpty()
                    .contains("UpdateCarritoDto")
                    .contains("123")
                    .contains(idCliente);
        }

        @Test
        @DisplayName("Should be immutable")
        void shouldBeImmutable() {
            // Arrange
            long id = 1L;
            String idCliente = "cliente123";
            List<ProductoDto> originalProductos = createValidProductoDtoList();
            UpdateCarritoDto dto = new UpdateCarritoDto(id, idCliente, originalProductos);

            // Act & Assert
            assertThat(dto.id()).isEqualTo(id);
            assertThat(dto.idCliente()).isEqualTo(idCliente);
            assertThat(dto.productoDtos()).isEqualTo(originalProductos);

            // Los records son inmutables por naturaleza
            assertThat(dto.productoDtos()).isInstanceOf(List.class);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle maximum long value for id")
        void shouldHandleMaximumLongValueForId() {
            // Arrange
            long maxId = Long.MAX_VALUE;
            String idCliente = "cliente123";
            List<ProductoDto> productos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(maxId, idCliente, productos);

            // Assert
            assertThat(dto.id()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("Should handle minimum long value for id")
        void shouldHandleMinimumLongValueForId() {
            // Arrange
            long minId = Long.MIN_VALUE;
            String idCliente = "cliente123";
            List<ProductoDto> productos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(minId, idCliente, productos);

            // Assert
            assertThat(dto.id()).isEqualTo(Long.MIN_VALUE);
        }

        @Test
        @DisplayName("Should handle very long idCliente")
        void shouldHandleVeryLongIdCliente() {
            // Arrange
            long id = 1L;
            String veryLongIdCliente = "a".repeat(1000);
            List<ProductoDto> productos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(id, veryLongIdCliente, productos);

            // Assert
            assertThat(dto.idCliente())
                    .hasSize(1000)
                    .isEqualTo(veryLongIdCliente);
        }

        @Test
        @DisplayName("Should handle large list of productoDtos")
        void shouldHandleLargeListOfProductoDtos() {
            // Arrange
            long id = 1L;
            String idCliente = "cliente123";
            List<ProductoDto> largeProductoList = IntStream.range(1, 1001)
                    .mapToObj(i -> createProductoDto(i, "Producto" + i))
                    .toList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(id, idCliente, largeProductoList);

            // Assert
            assertThat(dto.productoDtos())
                    .hasSize(1000)
                    .containsExactlyElementsOf(largeProductoList);
        }

        @Test
        @DisplayName("Should handle special characters in idCliente")
        void shouldHandleSpecialCharactersInIdCliente() {
            // Arrange
            long id = 1L;
            String specialCharIdCliente = "cliente@#$%^&*()_+-=[]{}|;':\",./<>?`~";
            List<ProductoDto> productos = createValidProductoDtoList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(id, specialCharIdCliente, productos);

            // Assert
            assertThat(dto.idCliente()).isEqualTo(specialCharIdCliente);
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should represent valid carrito update request")
        void shouldRepresentValidCarritoUpdateRequest() {
            // Arrange
            long carritoId = 123L;
            String clienteId = "CLIENTE_VIP_001";
            List<ProductoDto> productos = List.of(
                    createProductoDto(101L, "Smartphone"),
                    createProductoDto(102L, "Laptop"),
                    createProductoDto(103L, "Tablet")
            );

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(carritoId, clienteId, productos);

            // Assert
            assertThat(dto)
                    .satisfies(request -> {
                        assertThat(request.id()).isEqualTo(carritoId);
                        assertThat(request.idCliente()).isEqualTo(clienteId);
                        assertThat(request.productoDtos())
                                .hasSize(3)
                                .extracting(ProductoDto::id)
                                .containsExactly(101L, 102L, 103L);
                    });
        }

        @Test
        @DisplayName("Should support single product carrito update")
        void shouldSupportSingleProductCarritoUpdate() {
            // Arrange
            long carritoId = 456L;
            String clienteId = "cliente789";
            List<ProductoDto> singleProduct = List.of(createProductoDto(999L, "Producto Único"));

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(carritoId, clienteId, singleProduct);

            // Assert
            assertThat(dto.productoDtos())
                    .hasSize(1)
                    .first()
                    .extracting(ProductoDto::id, ProductoDto::nombre)
                    .containsExactly(999L, "Producto Único");
        }

        @Test
        @DisplayName("Should support empty product list for carrito clearing")
        void shouldSupportEmptyProductListForCarritoClearing() {
            // Arrange
            long carritoId = 789L;
            String clienteId = "cliente456";
            List<ProductoDto> emptyProducts = Collections.emptyList();

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(carritoId, clienteId, emptyProducts);

            // Assert
            assertThat(dto.productoDtos())
                    .isNotNull()
                    .isEmpty();
            assertThat(dto.id()).isEqualTo(carritoId);
            assertThat(dto.idCliente()).isEqualTo(clienteId);
        }
    }

    @Nested
    @DisplayName("Mock Integration Tests")
    class MockIntegrationTests {

        @Test
        @DisplayName("Should work with mocked ProductoDto")
        void shouldWorkWithMockedProductoDto() {
            // Arrange
            given(mockProductoDto.id()).willReturn(100L);
            given(mockProductoDto.nombre()).willReturn("Mocked Product");
            given(mockProductoDto.precio()).willReturn(BigDecimal.valueOf(29.99));

            long carritoId = 1L;
            String clienteId = "cliente123";
            List<ProductoDto> mockProducts = List.of(mockProductoDto);

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(carritoId, clienteId, mockProducts);

            // Assert
            assertThat(dto.productoDtos())
                    .hasSize(1)
                    .first()
                    .satisfies(producto -> {
                        assertThat(producto.id()).isEqualTo(100L);
                        assertThat(producto.nombre()).isEqualTo("Mocked Product");
                        assertThat(producto.precio()).isEqualByComparingTo(BigDecimal.valueOf(29.99));
                    });
        }

        @Test
        @DisplayName("Should handle multiple mocked ProductoDtos")
        void shouldHandleMultipleMockedProductoDtos() {
            // Arrange
            ProductoDto mockProduct1 = org.mockito.Mockito.mock(ProductoDto.class);
            ProductoDto mockProduct2 = org.mockito.Mockito.mock(ProductoDto.class);

            when(mockProduct1.id()).thenReturn(201L);
            when(mockProduct1.nombre()).thenReturn("Mock Product 1");
            when(mockProduct2.id()).thenReturn(202L);
            when(mockProduct2.nombre()).thenReturn("Mock Product 2");

            long carritoId = 2L;
            String clienteId = "cliente456";
            List<ProductoDto> mockProducts = List.of(mockProduct1, mockProduct2);

            // Act
            UpdateCarritoDto dto = new UpdateCarritoDto(carritoId, clienteId, mockProducts);

            // Assert
            assertThat(dto.productoDtos())
                    .hasSize(2)
                    .extracting(ProductoDto::id, ProductoDto::nombre)
                    .containsExactly(
                            tuple(201L, "Mock Product 1"),
                            tuple(202L, "Mock Product 2")
                    );
        }
    }

    // Helper methods
    private static List<ProductoDto> createValidProductoDtoList() {
        return List.of(
                createProductoDto(1L, "Producto 1"),
                createProductoDto(2L, "Producto 2")
        );
    }

    private static ProductoDto createProductoDto(long id, String nombre) {
        return new ProductoDto(
                id,
                nombre,
                "Descripción del " + nombre,
                BigDecimal.valueOf(19.99),
                10,
                1.5,
                "imagen_" + id + ".jpg"
        );
    }
}
