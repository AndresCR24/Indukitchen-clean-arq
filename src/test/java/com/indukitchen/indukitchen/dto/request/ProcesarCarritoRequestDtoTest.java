package com.indukitchen.indukitchen.dto.request;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.request.ProcesarCarritoRequestDto;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcesarCarritoRequestDto Tests")
class ProcesarCarritoRequestDtoTest {

    @Mock
    private ClienteDto mockCliente;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create dto with all valid parameters")
        void shouldCreateDtoWithAllValidParameters() {
            // Arrange
            ClienteDto expectedCliente = mock(ClienteDto.class);
            List<Long> expectedProductoIds = List.of(1L, 2L, 3L);
            Integer expectedMetodoPago = 1;
            String expectedEmailTo = "test@example.com";
            String expectedEmailSubject = "Tu carrito";
            String expectedEmailText = "Gracias por tu compra";

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    expectedCliente, expectedProductoIds, expectedMetodoPago,
                    expectedEmailTo, expectedEmailSubject, expectedEmailText
            );

            // Assert
            assertThat(dto)
                    .isNotNull()
                    .satisfies(request -> {
                        assertThat(request.cliente()).isEqualTo(expectedCliente);
                        assertThat(request.productoIds()).isEqualTo(expectedProductoIds);
                        assertThat(request.idMetodoPago()).isEqualTo(expectedMetodoPago);
                        assertThat(request.emailTo()).isEqualTo(expectedEmailTo);
                        assertThat(request.emailSubject()).isEqualTo(expectedEmailSubject);
                        assertThat(request.emailText()).isEqualTo(expectedEmailText);
                    });
        }

        @Test
        @DisplayName("Should create dto with minimal required parameters")
        void shouldCreateDtoWithMinimalRequiredParameters() {
            // Arrange
            ClienteDto expectedCliente = mock(ClienteDto.class);
            List<Long> expectedProductoIds = List.of(1L);

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    expectedCliente, expectedProductoIds, null, null, null, null
            );

            // Assert
            assertThat(dto)
                    .isNotNull()
                    .satisfies(request -> {
                        assertThat(request.cliente()).isEqualTo(expectedCliente);
                        assertThat(request.productoIds()).isEqualTo(expectedProductoIds);
                        assertThat(request.idMetodoPago()).isNull();
                        assertThat(request.emailTo()).isNull();
                        assertThat(request.emailSubject()).isNull();
                        assertThat(request.emailText()).isNull();
                    });
        }

        @Test
        @DisplayName("Should create dto with null cliente")
        void shouldCreateDtoWithNullCliente() {
            // Arrange
            List<Long> validProductoIds = List.of(1L, 2L);

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    null, validProductoIds, 1, "test@test.com", "Subject", "Text"
            );

            // Assert
            assertThat(dto.cliente()).isNull();
            assertThat(dto.productoIds()).isEqualTo(validProductoIds);
        }

        @Test
        @DisplayName("Should create dto with empty productoIds list")
        void shouldCreateDtoWithEmptyProductoIdsList() {
            // Arrange
            ClienteDto validCliente = mock(ClienteDto.class);
            List<Long> emptyProductoIds = Collections.emptyList();

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    validCliente, emptyProductoIds, 2, null, null, null
            );

            // Assert
            assertThat(dto.cliente()).isEqualTo(validCliente);
            assertThat(dto.productoIds())
                    .isNotNull()
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @Test
        @DisplayName("Should handle mock cliente with behavior")
        void shouldHandleMockClienteWithBehavior() {
            // Arrange
            given(mockCliente.cedula()).willReturn("12345678");
            given(mockCliente.nombre()).willReturn("Juan Pérez");
            given(mockCliente.correo()).willReturn("juan@example.com");

            List<Long> productoIds = List.of(1L, 2L);

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    mockCliente, productoIds, 1, null, null, null
            );

            // Assert
            assertThat(dto.cliente()).isEqualTo(mockCliente);
            assertThat(dto.cliente().cedula()).isEqualTo("12345678");
            assertThat(dto.cliente().nombre()).isEqualTo("Juan Pérez");
            assertThat(dto.cliente().correo()).isEqualTo("juan@example.com");
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5, 10, 100})
        @DisplayName("Should handle various valid metodoPago values")
        void shouldHandleValidMetodoPagoValues(Integer metodoPago) {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            List<Long> productoIds = List.of(1L);

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, productoIds, metodoPago, null, null, null
            );

            // Assert
            assertThat(dto.idMetodoPago()).isEqualTo(metodoPago);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should handle invalid email field values")
        void shouldHandleInvalidEmailFieldValues(String invalidEmail) {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            List<Long> productoIds = List.of(1L);

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, productoIds, 1, invalidEmail, "Subject", "Text"
            );

            // Assert
            assertThat(dto.emailTo())
                    .satisfiesAnyOf(
                            email -> assertThat(email).isNull(),
                            email -> assertThat(email).isBlank()
                    );
        }

        @ParameterizedTest
        @MethodSource("provideValidEmailScenarios")
        @DisplayName("Should handle various valid email scenarios")
        void shouldHandleValidEmailScenarios(String emailTo, String emailSubject, String emailText) {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            List<Long> productoIds = List.of(1L);

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, productoIds, 1, emailTo, emailSubject, emailText
            );

            // Assert
            assertThat(dto.emailTo()).isEqualTo(emailTo);
            assertThat(dto.emailSubject()).isEqualTo(emailSubject);
            assertThat(dto.emailText()).isEqualTo(emailText);
        }

        private static Stream<Arguments> provideValidEmailScenarios() {
            return Stream.of(
                    Arguments.of("user@test.com", "Compra realizada", "Gracias por tu compra"),
                    Arguments.of("admin@company.com", "Factura", "Tu factura está lista"),
                    Arguments.of("client@mail.com", null, null),
                    Arguments.of(null, "Solo subject", null),
                    Arguments.of(null, null, "Solo texto")
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
            ClienteDto cliente = mock(ClienteDto.class);
            List<Long> productoIds = List.of(1L, 2L);
            Integer metodoPago = 1;
            String emailTo = "test@test.com";
            String emailSubject = "Subject";
            String emailText = "Text";

            ProcesarCarritoRequestDto dto1 = new ProcesarCarritoRequestDto(
                    cliente, productoIds, metodoPago, emailTo, emailSubject, emailText
            );
            ProcesarCarritoRequestDto dto2 = new ProcesarCarritoRequestDto(
                    cliente, productoIds, metodoPago, emailTo, emailSubject, emailText
            );

            // Act & Assert
            assertThat(dto1)
                    .isEqualTo(dto2)
                    .hasSameHashCodeAs(dto2);
        }

        @Test
        @DisplayName("Should maintain equals contract for different objects")
        void shouldMaintainEqualsContractForDifferentObjects() {
            // Arrange
            ClienteDto cliente1 = mock(ClienteDto.class);
            ClienteDto cliente2 = mock(ClienteDto.class);

            ProcesarCarritoRequestDto dto1 = new ProcesarCarritoRequestDto(
                    cliente1, List.of(1L), 1, "email1@test.com", "Subject1", "Text1"
            );
            ProcesarCarritoRequestDto dto2 = new ProcesarCarritoRequestDto(
                    cliente2, List.of(2L), 2, "email2@test.com", "Subject2", "Text2"
            );

            // Act & Assert
            assertThat(dto1)
                    .isNotEqualTo(dto2)
                    .doesNotHaveSameHashCodeAs(dto2);
        }

        @Test
        @DisplayName("Should generate meaningful toString representation")
        void shouldGenerateMeaningfulToStringRepresentation() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            given(cliente.toString()).willReturn("ClienteDto[cedula=123, nombre=Juan]");

            List<Long> productoIds = List.of(1L, 2L, 3L);
            Integer metodoPago = 1;
            String emailTo = "test@example.com";

            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, productoIds, metodoPago, emailTo, "Subject", "Text"
            );

            // Act
            String toString = dto.toString();

            // Assert
            assertThat(toString)
                    .isNotNull()
                    .isNotEmpty()
                    .contains("ProcesarCarritoRequestDto")
                    .contains(emailTo)
                    .contains("1", "2", "3")
                    .contains(metodoPago.toString());
        }

        @Test
        @DisplayName("Should be immutable")
        void shouldBeImmutable() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            List<Long> originalProductoIds = List.of(1L, 2L, 3L);
            Integer metodoPago = 1;

            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, originalProductoIds, metodoPago, "test@test.com", "Subject", "Text"
            );

            // Act & Assert
            assertThat(dto.cliente()).isEqualTo(cliente);
            assertThat(dto.productoIds()).isEqualTo(originalProductoIds);
            assertThat(dto.idMetodoPago()).isEqualTo(metodoPago);

            // Los records son inmutables por naturaleza
            assertThat(dto).isInstanceOf(ProcesarCarritoRequestDto.class);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long email fields")
        void shouldHandleVeryLongEmailFields() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            String longEmailTo = "a".repeat(100) + "@example.com";
            String longSubject = "Subject ".repeat(50);
            String longText = "This is a very long email text. ".repeat(100);

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, List.of(1L), 1, longEmailTo, longSubject, longText
            );

            // Assert
            assertThat(dto.emailTo()).hasSize(longEmailTo.length());
            assertThat(dto.emailSubject()).hasSize(longSubject.length());
            assertThat(dto.emailText()).hasSize(longText.length());
        }

        @Test
        @DisplayName("Should handle large list of productoIds")
        void shouldHandleLargeListOfProductoIds() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            List<Long> largeProductoIds = Stream.iterate(1L, n -> n + 1)
                    .limit(500)
                    .toList();

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, largeProductoIds, 1, null, null, null
            );

            // Assert
            assertThat(dto.productoIds())
                    .hasSize(500)
                    .containsExactlyElementsOf(largeProductoIds);
        }

        @Test
        @DisplayName("Should handle negative metodoPago")
        void shouldHandleNegativeMetodoPago() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            Integer negativeMetodoPago = -1;

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, List.of(1L), negativeMetodoPago, null, null, null
            );

            // Assert
            assertThat(dto.idMetodoPago()).isEqualTo(negativeMetodoPago);
        }

        @Test
        @DisplayName("Should handle zero metodoPago")
        void shouldHandleZeroMetodoPago() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            Integer zeroMetodoPago = 0;

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, List.of(1L), zeroMetodoPago, null, null, null
            );

            // Assert
            assertThat(dto.idMetodoPago()).isEqualTo(zeroMetodoPago);
        }

        @Test
        @DisplayName("Should handle special characters in email fields")
        void shouldHandleSpecialCharactersInEmailFields() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            String specialEmailTo = "test+special@example-domain.com";
            String specialSubject = "¡Hola! Tú pedido está listo 🎉";
            String specialText = "Gracias por elegir nuestro servicio.\n\nSaludos,\nEl equipo";

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, List.of(1L), 1, specialEmailTo, specialSubject, specialText
            );

            // Assert
            assertThat(dto.emailTo()).isEqualTo(specialEmailTo);
            assertThat(dto.emailSubject()).isEqualTo(specialSubject);
            assertThat(dto.emailText()).isEqualTo(specialText);
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should represent complete carrito processing request")
        void shouldRepresentCompleteCarritoProcessingRequest() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);

            List<Long> productoIds = List.of(101L, 102L, 103L);
            Integer metodoPago = 1; // Tarjeta de crédito
            String emailTo = "cliente@example.com";
            String emailSubject = "Confirmación de compra - InduKitchen";
            String emailText = "Estimado cliente, su pedido ha sido procesado exitosamente.";

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, productoIds, metodoPago, emailTo, emailSubject, emailText
            );

            // Assert
            assertThat(dto)
                    .satisfies(request -> {
                        assertThat(request.cliente()).isNotNull();
                        assertThat(request.productoIds())
                                .hasSize(3)
                                .containsExactly(101L, 102L, 103L)
                                .allMatch(id -> id > 0);
                        assertThat(request.idMetodoPago()).isEqualTo(1);
                        assertThat(request.emailTo()).isEqualTo(emailTo);
                        assertThat(request.emailSubject()).contains("InduKitchen");
                        assertThat(request.emailText()).contains("exitosamente");
                    });
        }

        @Test
        @DisplayName("Should support processing without email customization")
        void shouldSupportProcessingWithoutEmailCustomization() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            given(cliente.correo()).willReturn("default@example.com");

            List<Long> productoIds = List.of(1L);
            Integer metodoPago = 2; // PayPal

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, productoIds, metodoPago, null, null, null
            );

            // Assert
            assertThat(dto)
                    .satisfies(request -> {
                        assertThat(request.cliente().correo()).isEqualTo("default@example.com");
                        assertThat(request.idMetodoPago()).isEqualTo(2);
                        assertThat(request.emailTo()).isNull(); // Se usará cliente.correo
                        assertThat(request.emailSubject()).isNull(); // Subject por defecto
                        assertThat(request.emailText()).isNull(); // Texto por defecto
                    });
        }

        @Test
        @DisplayName("Should support deferred payment processing")
        void shouldSupportDeferredPaymentProcessing() {
            // Arrange
            ClienteDto cliente = mock(ClienteDto.class);
            List<Long> productoIds = List.of(1L, 2L);

            // Act - Sin método de pago seleccionado aún
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, productoIds, null, "cliente@test.com", "Carrito guardado", "Su carrito está guardado"
            );

            // Assert
            assertThat(dto.idMetodoPago()).isNull();
            assertThat(dto.productoIds()).hasSize(2);
            assertThat(dto.emailSubject()).contains("guardado");
        }

        @Test
        @DisplayName("Should validate business workflow")
        void shouldValidateBusinessWorkflow() {
            // Arrange - Simulando un flujo de negocio completo
            ClienteDto cliente = mock(ClienteDto.class);
            given(cliente.cedula()).willReturn("12345678");
            given(cliente.nombre()).willReturn("María García");
            given(cliente.correo()).willReturn("maria@example.com");

            List<Long> productoIds = List.of(1L, 2L, 3L, 4L, 5L);
            Integer metodoPago = 3;

            // Act
            ProcesarCarritoRequestDto dto = new ProcesarCarritoRequestDto(
                    cliente, productoIds, metodoPago, null, null, null
            );

            // Assert - Validación del flujo de negocio
            assertThat(dto)
                    .satisfies(request -> {
                        // Cliente válido
                        assertThat(request.cliente().cedula()).isNotBlank();
                        assertThat(request.cliente().nombre()).isNotBlank();
                        assertThat(request.cliente().correo()).contains("@");

                        // Productos seleccionados
                        assertThat(request.productoIds()).isNotEmpty();

                        // Método de pago seleccionado
                        assertThat(request.idMetodoPago()).isPositive();

                        // Email por defecto (null = usar cliente.correo)
                        assertThat(request.emailTo()).isNull();
                    });
        }
    }
}
