package com.indukitchen.indukitchen.dto.request;

import com.indukitchen.indukitchen.domain.dto.request.EmailRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para EmailRequestDto (Record):
 * - AAA (Arrange / Act / Assert)
 * - AssertJ para aserciones fluidas
 * - Verifica comportamiento de records (equals, hashCode, toString, accessors)
 * - Mantiene tests rápidos y aislados (FIRST)
 */
class EmailRequestDtoTest {

    @Test
    @DisplayName("Constructor y accessors funcionan correctamente")
    void constructor_and_accessors_work() {
        // Arrange & Act
        EmailRequestDto dto = new EmailRequestDto(
                "cliente@test.com",
                "Confirmación de pedido",
                "Su pedido ha sido procesado exitosamente"
        );

        // Assert
        assertThat(dto.to()).isEqualTo("cliente@test.com");
        assertThat(dto.subject()).isEqualTo("Confirmación de pedido");
        assertThat(dto.text()).isEqualTo("Su pedido ha sido procesado exitosamente");
    }

    @Test
    @DisplayName("Record con todos los campos null")
    void record_with_all_null_fields() {
        // Arrange & Act
        EmailRequestDto dto = new EmailRequestDto(null, null, null);

        // Assert
        assertThat(dto.to()).isNull();
        assertThat(dto.subject()).isNull();
        assertThat(dto.text()).isNull();
    }

    @Test
    @DisplayName("Record con campos vacíos")
    void record_with_empty_fields() {
        // Arrange & Act
        EmailRequestDto dto = new EmailRequestDto("", "", "");

        // Assert
        assertThat(dto.to()).isEmpty();
        assertThat(dto.subject()).isEmpty();
        assertThat(dto.text()).isEmpty();
    }

    @Test
    @DisplayName("Record con email corporativo y mensaje largo")
    void record_with_corporate_email_and_long_message() {
        // Arrange
        String corporateEmail = "administracion@indukitchen.com";
        String longSubject = "Notificación importante sobre su pedido #12345 - Requiere verificación adicional";
        String longText = "Estimado cliente, su pedido ha sido recibido y está siendo procesado. " +
                "Debido al volumen de productos solicitados, requerimos verificación adicional " +
                "que puede tomar hasta 2-3 días hábiles. Agradecemos su paciencia.";

        // Act
        EmailRequestDto dto = new EmailRequestDto(corporateEmail, longSubject, longText);

        // Assert
        assertThat(dto.to())
                .isEqualTo(corporateEmail)
                .contains("@indukitchen.com");
        assertThat(dto.subject())
                .isEqualTo(longSubject)
                .contains("pedido #12345");
        assertThat(dto.text())
                .isEqualTo(longText)
                .contains("Estimado cliente")
                .hasSize(longText.length());
    }

    @Test
    @DisplayName("equals() compara correctamente todos los campos")
    void equals_compares_all_fields() {
        // Arrange
        EmailRequestDto a = new EmailRequestDto("test@test.com", "Subject", "Text");
        EmailRequestDto b = new EmailRequestDto("test@test.com", "Subject", "Text");
        EmailRequestDto c = new EmailRequestDto("other@test.com", "Subject", "Text");
        EmailRequestDto d = new EmailRequestDto("test@test.com", "Other Subject", "Text");
        EmailRequestDto e = new EmailRequestDto("test@test.com", "Subject", "Other Text");

        // Assert
        assertThat(a)
                .isEqualTo(b)
                .isNotEqualTo(c)
                .isNotEqualTo(d)
                .isNotEqualTo(e);
    }

    @Test
    @DisplayName("hashCode() es coherente con equals()")
    void hashcode_is_consistent_with_equals() {
        // Arrange
        EmailRequestDto a = new EmailRequestDto("user@example.com", "Test", "Content");
        EmailRequestDto b = new EmailRequestDto("user@example.com", "Test", "Content");

        // Assert
        assertThat(a).hasSameHashCodeAs(b);
    }

    @Test
    @DisplayName("hashCode() cambia cuando cambian los campos")
    void hashcode_changes_when_fields_change() {
        // Arrange
        EmailRequestDto a = new EmailRequestDto("email1@test.com", "Subject1", "Text1");
        EmailRequestDto b = new EmailRequestDto("email2@test.com", "Subject2", "Text2");

        // Assert
        assertThat(a).doesNotHaveSameHashCodeAs(b);
    }

    @Test
    @DisplayName("toString() contiene información del record")
    void toString_contains_record_info() {
        // Arrange
        EmailRequestDto dto = new EmailRequestDto(
                "admin@company.com",
                "Urgent Notice",
                "Please review immediately"
        );

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("EmailRequestDto")
                .contains("to=admin@company.com")
                .contains("subject=Urgent Notice")
                .contains("text=Please review immediately");
    }

    @Test
    @DisplayName("toString() con campos null")
    void toString_with_null_fields() {
        // Arrange
        EmailRequestDto dto = new EmailRequestDto(null, null, null);

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("EmailRequestDto")
                .contains("to=null")
                .contains("subject=null")
                .contains("text=null");
    }

    @Test
    @DisplayName("Record con caracteres especiales en todos los campos")
    void record_with_special_characters() {
        // Arrange & Act
        EmailRequestDto dto = new EmailRequestDto(
                "test+user@domain-name.co.uk",
                "¡Oferta especial! 50% descuento & más...",
                "Mensaje con acentos: café, niño, corazón. Símbolos: @#$%^&*()"
        );

        // Assert
        assertThat(dto.to())
                .contains("+", "-", ".")
                .endsWith(".co.uk");
        assertThat(dto.subject())
                .contains("¡", "!", "%", "&");
        assertThat(dto.text())
                .contains("café", "niño", "corazón")
                .contains("@#$%^&*()");
    }

    @Test
    @DisplayName("Record con HTML en el texto")
    void record_with_html_in_text() {
        // Arrange & Act
        EmailRequestDto dto = new EmailRequestDto(
                "client@test.com",
                "Factura HTML",
                "<html><body><h1>Factura</h1><p>Total: <strong>$150</strong></p></body></html>"
        );

        // Assert
        assertThat(dto.text())
                .contains("<html>", "<body>", "<h1>", "</h1>")
                .contains("<strong>", "$150", "</strong>");
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con null")
    void equals_returns_false_for_null() {
        // Arrange
        EmailRequestDto dto = new EmailRequestDto("test@test.com", "subject", "text");

        // Assert
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con otro tipo")
    void equals_returns_false_for_different_type() {
        // Arrange
        EmailRequestDto dto = new EmailRequestDto("test@test.com", "subject", "text");
        String otroTipo = "Not an EmailRequestDto";

        // Assert
        assertThat(dto).isNotEqualTo(otroTipo);
    }

    @Test
    @DisplayName("equals() es reflexivo")
    void equals_is_reflexive() {
        // Arrange
        EmailRequestDto dto = new EmailRequestDto("test@test.com", "subject", "text");

        // Act
        boolean result = dto.equals(dto);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("equals() es simétrico")
    void equals_is_symmetric() {
        // Arrange
        EmailRequestDto a = new EmailRequestDto("test@test.com", "subject", "text");
        EmailRequestDto b = new EmailRequestDto("test@test.com", "subject", "text");

        // Act & Assert
        assertThat(a.equals(b)).isEqualTo(b.equals(a));
    }

    @Test
    @DisplayName("equals() es transitivo")
    void equals_is_transitive() {
        // Arrange
        EmailRequestDto a = new EmailRequestDto("test@test.com", "subject", "text");
        EmailRequestDto b = new EmailRequestDto("test@test.com", "subject", "text");
        EmailRequestDto c = new EmailRequestDto("test@test.com", "subject", "text");

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(b).isEqualTo(c);
        assertThat(a).isEqualTo(c);
    }

    @Test
    @DisplayName("Dos records con null en todos los campos son iguales")
    void two_records_with_all_null_are_equal() {
        // Arrange
        EmailRequestDto a = new EmailRequestDto(null, null, null);
        EmailRequestDto b = new EmailRequestDto(null, null, null);

        // Assert
        assertThat(a)
                .isEqualTo(b)
                .hasSameHashCodeAs(b);
    }

    @Test
    @DisplayName("Record con solo espacios en todos los campos")
    void record_with_only_whitespace() {
        // Arrange & Act
        EmailRequestDto dto = new EmailRequestDto("   ", "   ", "   ");

        // Assert
        assertThat(dto.to()).isBlank().isNotEmpty();
        assertThat(dto.subject()).isBlank().isNotEmpty();
        assertThat(dto.text()).isBlank().isNotEmpty();
    }

    @Test
    @DisplayName("Record con saltos de línea y tabulaciones")
    void record_with_newlines_and_tabs() {
        // Arrange & Act
        EmailRequestDto dto = new EmailRequestDto(
                "user@test.com",
                "Línea 1\nLínea 2",
                "Texto con\ttabulaciones\ny\nsaltos de línea"
        );

        // Assert
        assertThat(dto.subject()).contains("\n", "Línea 1", "Línea 2");
        assertThat(dto.text()).contains("\t", "\n", "tabulaciones");
    }

    @Test
    @DisplayName("Record con múltiples emails separados por coma")
    void record_with_multiple_emails() {
        // Arrange & Act
        EmailRequestDto dto = new EmailRequestDto(
                "admin@test.com,user@test.com,support@test.com",
                "Notificación grupal",
                "Mensaje para todos los destinatarios"
        );

        // Assert
        assertThat(dto.to())
                .contains("admin@test.com")
                .contains("user@test.com")
                .contains("support@test.com")
                .contains(",");
    }
}
