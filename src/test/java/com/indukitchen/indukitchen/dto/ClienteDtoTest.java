package com.indukitchen.indukitchen.dto;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para ClienteDto (Record con validaciones):
 * - AAA (Arrange / Act / Assert)
 * - AssertJ para aserciones fluidas
 * - Verifica validaciones Jakarta Bean Validation
 * - Verifica comportamiento de records (equals, hashCode, toString, accessors)
 * - Mantiene tests rápidos y aislados (FIRST)
 */
class ClienteDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        if (factory != null) factory.close();
    }

    private ClienteDto valid() {
        return new ClienteDto(
                "CLI-1",
                "Ana",
                "Calle 1",
                "ana@dominio.com",
                "3001234567"
        );
    }

    @Test
    @DisplayName("Constructor y accessors funcionan correctamente")
    void constructor_and_accessors_work() {
        // Arrange & Act
        ClienteDto dto = new ClienteDto("ID-9", "Pepe", "Av 123", "p@e.co", "123");

        // Assert
        assertThat(dto.cedula()).isEqualTo("ID-9");
        assertThat(dto.nombre()).isEqualTo("Pepe");
        assertThat(dto.direccion()).isEqualTo("Av 123");
        assertThat(dto.correo()).isEqualTo("p@e.co");
        assertThat(dto.telefono()).isEqualTo("123");
    }

    @Test
    @DisplayName("Instancia válida pasa todas las validaciones")
    void valid_instance_passes_validation() {
        // Arrange
        ClienteDto dto = valid();

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Cédula @NotBlank - rechaza valores vacíos")
    void cedula_notblank_is_enforced() {
        // Arrange
        ClienteDto dto = new ClienteDto("", "Ana", "Calle", "a@b.com", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cedula") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
    }

    @Test
    @DisplayName("Cédula @NotBlank - rechaza valores solo con espacios")
    void cedula_notblank_rejects_whitespace() {
        // Arrange
        ClienteDto dto = new ClienteDto("   ", "Ana", "Calle", "a@b.com", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cedula"));
    }

    @Test
    @DisplayName("Nombre @NotBlank - rechaza valores vacíos")
    void nombre_notblank_rejects_blank() {
        // Arrange
        ClienteDto dto = new ClienteDto("ID", "  ", "Dir", "a@b.com", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("nombre") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
    }

    @Test
    @DisplayName("Nombre @Size(max=40) - rechaza valores que exceden el límite")
    void nombre_size_max_40_is_enforced() {
        // Arrange
        String longName = "x".repeat(41);
        ClienteDto dto = new ClienteDto("ID", longName, "Dir", "a@b.com", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("nombre") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class));
    }

    @Test
    @DisplayName("Nombre con exactamente 40 caracteres es válido")
    void nombre_with_exactly_40_chars_is_valid() {
        // Arrange
        String name40 = "x".repeat(40);
        ClienteDto dto = new ClienteDto("ID", name40, "Dir", "a@b.com", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Dirección @NotBlank - rechaza valores vacíos")
    void direccion_notblank_rejects_blank() {
        // Arrange
        ClienteDto dto = new ClienteDto("ID", "Ana", " ", "a@b.com", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("direccion") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
    }

    @Test
    @DisplayName("Dirección @Size(max=40) - rechaza valores que exceden el límite")
    void direccion_size_max_40_is_enforced() {
        // Arrange
        String longDir = "y".repeat(41);
        ClienteDto dto = new ClienteDto("ID", "Ana", longDir, "a@b.com", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("direccion") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class));
    }

    @Test
    @DisplayName("Correo @Email - rechaza formato inválido")
    void correo_email_format_is_enforced() {
        // Arrange
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", "no-valido@", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("correo") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(Email.class));
    }

    @Test
    @DisplayName("Correo @Email - rechaza correo sin @")
    void correo_rejects_email_without_at() {
        // Arrange
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", "invalido.com", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("correo"));
    }

    @Test
    @DisplayName("Correo @Size(max=100) - rechaza valores que exceden el límite")
    void correo_size_max_100_is_enforced() {
        // Arrange
        String longLocal = "a".repeat(95); // 95 + "@x.com"(6) = 101
        String longEmail = longLocal + "@x.com";
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", longEmail, "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("correo") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class));
    }

    @Test
    @DisplayName("Correo puede ser null (no tiene @NotBlank)")
    void correo_can_be_null() {
        // Arrange
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", null, "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Correo vacío es válido (solo tiene @Email, no @NotBlank)")
    void correo_can_be_empty() {
        // Arrange
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", "", "111");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Teléfono @NotBlank - rechaza valores vacíos")
    void telefono_notblank_rejects_blank() {
        // Arrange
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", "a@b.com", " ");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("telefono") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class));
    }

    @Test
    @DisplayName("Teléfono @Size(max=17) - rechaza valores que exceden el límite")
    void telefono_size_max_17_is_enforced() {
        // Arrange
        String longPhone = "1".repeat(18); // supera 17
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", "a@b.com", longPhone);

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("telefono") &&
                v.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class));
    }

    @Test
    @DisplayName("Teléfono con exactamente 17 caracteres es válido")
    void telefono_with_exactly_17_chars_is_valid() {
        // Arrange
        String phone17 = "1".repeat(17);
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", "a@b.com", phone17);

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("equals() compara todos los componentes del record")
    void equals_compares_all_components() {
        // Arrange
        ClienteDto a = new ClienteDto("ID", "Ana", "Dir", "a@b.com", "111");
        ClienteDto b = new ClienteDto("ID", "Ana", "Dir", "a@b.com", "111");
        ClienteDto c = new ClienteDto("ID2", "Ana", "Dir", "a@b.com", "111");

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    @DisplayName("hashCode() es coherente con equals()")
    void hashcode_is_consistent_with_equals() {
        // Arrange
        ClienteDto a = new ClienteDto("ID", "Ana", "Dir", "a@b.com", "111");
        ClienteDto b = new ClienteDto("ID", "Ana", "Dir", "a@b.com", "111");

        // Assert
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("toString() contiene información del record")
    void toString_contains_record_info() {
        // Arrange
        ClienteDto dto = new ClienteDto("ID", "Ana", "Dir", "a@b.com", "111");

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("ClienteDto")
                .contains("cedula=ID")
                .contains("nombre=Ana");
    }

    @Test
    @DisplayName("Record con caracteres especiales en campos de texto")
    void record_with_special_characters() {
        // Arrange & Act
        ClienteDto dto = new ClienteDto("ID-123", "José María", "Calle 123 #45-67",
                "jose.maria@dominio.com", "+57 300 1234567");

        // Assert
        assertThat(dto.cedula()).isEqualTo("ID-123");
        assertThat(dto.nombre()).contains("José María");
        assertThat(dto.direccion()).contains("#");
        assertThat(dto.telefono()).contains("+57");
    }

    @Test
    @DisplayName("Múltiples violaciones cuando todos los campos son inválidos")
    void multiple_violations_when_all_invalid() {
        // Arrange
        ClienteDto dto = new ClienteDto("", "", "", "invalid-email", "");

        // Act
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSizeGreaterThanOrEqualTo(4);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con null")
    void equals_returns_false_for_null() {
        // Arrange
        ClienteDto dto = valid();

        // Assert
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con otro tipo")
    void equals_returns_false_for_different_type() {
        // Arrange
        ClienteDto dto = valid();
        String otroTipo = "Not a ClienteDto";

        // Assert
        assertThat(dto).isNotEqualTo(otroTipo);
    }
}
