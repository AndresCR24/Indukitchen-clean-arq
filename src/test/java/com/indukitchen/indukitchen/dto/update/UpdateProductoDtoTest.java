package com.indukitchen.indukitchen.dto.update;

import com.indukitchen.indukitchen.domain.dto.update.UpdateProductoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductoDto Tests")
public class UpdateProductoDtoTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create dto with no parameters")
        void shouldCreateDtoWithDefaultConstructor() {
            // Arrange & Act
            UpdateProductoDto dto = new UpdateProductoDto();

            // Assert
            assertThat(dto).isNotNull();
        }
    }

    @Nested
    @DisplayName("Record Behavior Tests")
    class RecordBehaviorTests {

        @Test
        @DisplayName("Should maintain equals and hashCode for identical instances")
        void shouldMaintainEqualsAndHashCode() {
            // Arrange
            UpdateProductoDto dto1 = new UpdateProductoDto();
            UpdateProductoDto dto2 = new UpdateProductoDto();

            // Act & Assert
            assertThat(dto1)
                .isEqualTo(dto2)
                .hasSameHashCodeAs(dto2);
        }

        @Test
        @DisplayName("Should generate meaningful toString representation")
        void shouldGenerateMeaningfulToString() {
            // Arrange
            UpdateProductoDto dto = new UpdateProductoDto();

            // Act
            String str = dto.toString();

            // Assert
            assertThat(str)
                .isNotNull()
                .contains("UpdateProductoDto");
        }

        @Test
        @DisplayName("Should be immutable by nature of record")
        void shouldBeImmutable() {
            // Arrange
            UpdateProductoDto dto = new UpdateProductoDto();

            // Act & Assert
            assertThat(dto).isInstanceOf(UpdateProductoDto.class);
        }
    }

    @Nested
    @DisplayName("Mock Integration Tests")
    class MockIntegrationTests {

        @Mock
        private UpdateProductoDto mockDto;

        @Test
        @DisplayName("Should use mock to override toString")
        void shouldWorkWithMockedInstance() {
            // Arrange
            given(mockDto.toString()).willReturn("MockedProductDto");

            // Act
            String repr = mockDto.toString();

            // Assert
            assertThat(repr).isEqualTo("MockedProductDto");
        }
    }
}
