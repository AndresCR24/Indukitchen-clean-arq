package com.indukitchen.indukitchen.dto.update;

import com.indukitchen.indukitchen.domain.dto.update.UpdateClienteDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateClienteDto Tests")
public class UpdateClienteDtoTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create dto with valid parameters")
        void shouldCreateDtoWithValidParameters() {
            // Arrange
            String cedula = "1234567890";
            String nombre = "Juan Perez";
            String direccion = "Calle Falsa 123";
            String correo = "juan.perez@example.com";
            String telefono = "5551234567";

            // Act
            UpdateClienteDto dto = new UpdateClienteDto(cedula, nombre, direccion, correo, telefono);

            // Assert
            assertThat(dto)
                .isNotNull()
                .extracting(UpdateClienteDto::cedula, UpdateClienteDto::nombre,
                            UpdateClienteDto::direccion, UpdateClienteDto::correo,
                            UpdateClienteDto::telefono)
                .containsExactly(cedula, nombre, direccion, correo, telefono);
        }
    }

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should handle invalid cedula values")
        void shouldHandleInvalidCedula(String invalid) {
            // Arrange
            UpdateClienteDto dto = new UpdateClienteDto(invalid, "Nom", "Dir", "a@b.com", "123");

            // Assert
            assertThat(dto.cedula())
                .satisfiesAnyOf(
                    val -> assertThat(val).isNull(),
                    val -> assertThat(val).isBlank()
                );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should handle invalid nombre values")
        void shouldHandleInvalidNombre(String invalid) {
            UpdateClienteDto dto = new UpdateClienteDto("1", invalid, "Dir", "a@b.com", "123");
            assertThat(dto.nombre())
                .satisfiesAnyOf(
                    val -> assertThat(val).isNull(),
                    val -> assertThat(val).isBlank()
                );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should handle invalid direccion values")
        void shouldHandleInvalidDireccion(String invalid) {
            UpdateClienteDto dto = new UpdateClienteDto("1", "Nom", invalid, "a@b.com", "123");
            assertThat(dto.direccion())
                .satisfiesAnyOf(
                    val -> assertThat(val).isNull(),
                    val -> assertThat(val).isBlank()
                );
        }

        @ParameterizedTest
        @ValueSource(strings = {"not-an-email", "@domain.com", "user@"})
        @DisplayName("Should accept invalid email formats as-is")
        void shouldAcceptInvalidCorreoFormats(String invalidEmail) {
            UpdateClienteDto dto = new UpdateClienteDto("1", "Nom", "Dir", invalidEmail, "123");
            assertThat(dto.correo()).isEqualTo(invalidEmail);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should handle invalid telefono values")
        void shouldHandleInvalidTelefono(String invalid) {
            UpdateClienteDto dto = new UpdateClienteDto("1", "Nom", "Dir", "a@b.com", invalid);
            assertThat(dto.telefono())
                .satisfiesAnyOf(
                    val -> assertThat(val).isNull(),
                    val -> assertThat(val).isBlank()
                );
        }
    }

    @Nested
    @DisplayName("Record Behavior Tests")
    class RecordBehaviorTests {

        @Test
        @DisplayName("Should maintain equals and hashCode for identical objects")
        void shouldMaintainEqualsHashCode() {
            UpdateClienteDto dto1 = new UpdateClienteDto("1","N","D","e@e.com","t");
            UpdateClienteDto dto2 = new UpdateClienteDto("1","N","D","e@e.com","t");
            assertThat(dto1)
                .isEqualTo(dto2)
                .hasSameHashCodeAs(dto2);
        }

        @Test
        @DisplayName("Should have meaningful toString")
        void shouldHaveMeaningfulToString() {
            UpdateClienteDto dto = new UpdateClienteDto("1","N","D","e@e.com","t");
            String str = dto.toString();
            assertThat(str)
                .contains("UpdateClienteDto")
                .contains("1","N","D","e@e.com","t");
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle max length fields")
        void shouldHandleMaxLengthFields() {
            String ced = "1".repeat(50);
            String nom = "n".repeat(40);
            String dir = "d".repeat(40);
            String mail = "u@" + "a".repeat(98);
            String tel = "9".repeat(17);
            UpdateClienteDto dto = new UpdateClienteDto(ced, nom, dir, mail, tel);
            assertThat(dto)
                .extracting(UpdateClienteDto::cedula, UpdateClienteDto::nombre,
                            UpdateClienteDto::direccion, UpdateClienteDto::correo,
                            UpdateClienteDto::telefono)
                .containsExactly(ced, nom, dir, mail, tel);
        }
    }
}
