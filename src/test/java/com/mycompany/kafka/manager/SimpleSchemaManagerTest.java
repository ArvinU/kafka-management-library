package com.mycompany.kafka.manager;

import com.mycompany.kafka.dto.SchemaInfo;
import com.mycompany.kafka.factory.ConnectionFactory;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleSchemaManagerTest {

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private SchemaRegistryClient schemaRegistryClient;

    private SimpleSchemaManager schemaManager;

    @BeforeEach
    void setUp() {
        when(connectionFactory.createSchemaRegistryClient()).thenReturn(schemaRegistryClient);
        schemaManager = new SimpleSchemaManager(connectionFactory);
    }

    @Test
    void testRegisterSchema() {
        // Given
        String subject = "test-subject";
        String schema = "{\"type\":\"string\"}";

        // When
        int result = schemaManager.registerSchema(subject, schema);

        // Then
        assertEquals(1, result); // Mock ID
        verify(connectionFactory).createSchemaRegistryClient();
    }

    @Test
    void testGetSchemaById() {
        // Given
        int schemaId = 1;

        // When
        SchemaInfo result = schemaManager.getSchemaById(schemaId);

        // Then
        assertNotNull(result);
        assertEquals(schemaId, result.getId());
        assertEquals("{\"type\":\"string\"}", result.getSchema());
        assertEquals("AVRO", result.getType());
        assertEquals("mock-subject", result.getSubject());
    }

    @Test
    void testListSubjects() throws Exception {
        // Given
        List<String> expectedSubjects = Arrays.asList("subject1", "subject2");
        when(schemaRegistryClient.getAllSubjects()).thenReturn(expectedSubjects);

        // When
        List<String> result = schemaManager.listSubjects();

        // Then
        assertEquals(expectedSubjects, result);
        verify(schemaRegistryClient).getAllSubjects();
    }

    @Test
    void testSubjectExists_Exists() throws Exception {
        // Given
        String subject = "existing-subject";
        List<String> subjects = Arrays.asList("existing-subject", "other-subject");
        when(schemaRegistryClient.getAllSubjects()).thenReturn(subjects);

        // When
        boolean result = schemaManager.subjectExists(subject);

        // Then
        assertTrue(result);
        verify(schemaRegistryClient).getAllSubjects();
    }

    @Test
    void testSubjectExists_NotExists() throws Exception {
        // Given
        String subject = "non-existing-subject";
        List<String> subjects = Arrays.asList("existing-subject", "other-subject");
        when(schemaRegistryClient.getAllSubjects()).thenReturn(subjects);

        // When
        boolean result = schemaManager.subjectExists(subject);

        // Then
        assertFalse(result);
        verify(schemaRegistryClient).getAllSubjects();
    }

    @Test
    void testSubjectExists_Exception() throws Exception {
        // Given
        String subject = "test-subject";
        when(schemaRegistryClient.getAllSubjects()).thenThrow(new RuntimeException("Connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> schemaManager.subjectExists(subject));
        assertTrue(exception.getMessage().contains("Failed to check if subject exists"));
    }

    @Test
    void testGetConnectionFactory() {
        // When
        ConnectionFactory result = schemaManager.getConnectionFactory();

        // Then
        assertEquals(connectionFactory, result);
    }

    @Test
    void testGetSchemaRegistryClient() {
        // When
        SchemaRegistryClient result = schemaManager.getSchemaRegistryClient();

        // Then
        assertEquals(schemaRegistryClient, result);
    }
}
