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
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
    void constructor_and_accessors_work() {
        var dto = new ClienteDto("ID-9", "Pepe", "Av 123", "p@e.co", "123");
        assertEquals("ID-9", dto.cedula());
        assertEquals("Pepe", dto.nombre());
        assertEquals("Av 123", dto.direccion());
        assertEquals("p@e.co", dto.correo());
        assertEquals("123", dto.telefono());
    }

    @Test
    void valid_instance_passes_validation() {
        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(valid());
        assertTrue(violations.isEmpty(), "No debería haber violaciones con datos válidos");
    }

    @Test
    void cedula_notblank_is_enforced() {
        var dto = new ClienteDto("", "Ana", "Calle", "a@b.com", "111");
        var v = validator.validate(dto);
        assertTrue(v.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("cedula")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class)));
    }

    @Test
    void nombre_notblank_and_size_max_40() {
        var blank = new ClienteDto("ID", "  ", "Dir", "a@b.com", "111");
        var v1 = validator.validate(blank);
        assertTrue(v1.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("nombre")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class)));

        String longName = "x".repeat(41);
        var tooLong = new ClienteDto("ID", longName, "Dir", "a@b.com", "111");
        var v2 = validator.validate(tooLong);
        assertTrue(v2.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("nombre")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class)));
    }

    @Test
    void direccion_notblank_and_size_max_40() {
        var blank = new ClienteDto("ID", "Ana", " ", "a@b.com", "111");
        var v1 = validator.validate(blank);
        assertTrue(v1.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("direccion")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class)));

        String longDir = "y".repeat(41);
        var tooLong = new ClienteDto("ID", "Ana", longDir, "a@b.com", "111");
        var v2 = validator.validate(tooLong);
        assertTrue(v2.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("direccion")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class)));
    }

    @Test
    void correo_email_format_and_size_max_100() {
        // formato inválido
        var badMail = new ClienteDto("ID", "Ana", "Dir", "no-valido@", "111");
        var v1 = validator.validate(badMail);
        assertTrue(v1.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("correo")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(Email.class)));

        // tamaño > 100 pero formato válido
        String longLocal = "a".repeat(95); // 95 + "@x.com"(6) = 101
        String longEmail = longLocal + "@x.com";
        var tooLong = new ClienteDto("ID", "Ana", "Dir", longEmail, "111");
        var v2 = validator.validate(tooLong);
        assertTrue(v2.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("correo")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class)));
    }

    @Test
    void correo_can_be_null() {
        var dto = new ClienteDto("ID", "Ana", "Dir", null, "111");
        var v = validator.validate(dto);
        assertTrue(v.isEmpty(), "Correo null es válido (no tiene @NotBlank)");
    }

    @Test
    void telefono_notblank_and_size_max_17() {
        var blank = new ClienteDto("ID", "Ana", "Dir", "a@b.com", " ");
        var v1 = validator.validate(blank);
        assertTrue(v1.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("telefono")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class)));

        String longPhone = "1".repeat(18); // supera 17
        var tooLong = new ClienteDto("ID", "Ana", "Dir", "a@b.com", longPhone);
        var v2 = validator.validate(tooLong);
        assertTrue(v2.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("telefono")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(Size.class)));
    }

    @Test
    void equals_hashcode_and_toString_work_as_record() {
        var a = new ClienteDto("ID", "Ana", "Dir", "a@b.com", "111");
        var b = new ClienteDto("ID", "Ana", "Dir", "a@b.com", "111");
        var c = new ClienteDto("ID2", "Ana", "Dir", "a@b.com", "111");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        var s = a.toString();
        assertTrue(s.contains("ClienteDto"));
        assertTrue(s.contains("cedula=ID"));
        assertTrue(s.contains("nombre=Ana"));
    }

    @Test
    void cedula_null_is_rejected() {
        var dto = new ClienteDto(null, "Ana", "Calle 1", "a@b.com", "300");
        var v = validator.validate(dto);
        assertTrue(v.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("cedula")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class)));
    }

    @Test
    void nombre_null_is_rejected() {
        var dto = new ClienteDto("ID", null, "Calle 1", "a@b.com", "300");
        var v = validator.validate(dto);
        assertTrue(v.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("nombre")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class)));
    }

    @Test
    void direccion_null_is_rejected() {
        var dto = new ClienteDto("ID", "Ana", null, "a@b.com", "300");
        var v = validator.validate(dto);
        assertTrue(v.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("direccion")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class)));
    }

    @Test
    void telefono_null_is_rejected() {
        var dto = new ClienteDto("ID", "Ana", "Calle 1", "a@b.com", null);
        var v = validator.validate(dto);
        assertTrue(v.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals("telefono")
                        && cv.getConstraintDescriptor().getAnnotation().annotationType().equals(NotBlank.class)));
    }

}

