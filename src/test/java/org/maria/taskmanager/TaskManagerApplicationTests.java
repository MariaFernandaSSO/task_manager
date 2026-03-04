package org.maria.taskmanager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@DisplayName("TaskManager Application")
class TaskManagerApplicationTests {

    @Test
    @DisplayName("deve carregar o contexto da aplicação com sucesso")
    void contextLoads() {
    }
}
