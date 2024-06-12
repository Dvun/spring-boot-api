package com.codeapi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TestContainersTest extends AbstractTestContainers {


    @Test
    void canStartPostgresDB() {
        assertThat(postgreSQLContainer.isRunning()).isEqualTo(true);
        assertThat(postgreSQLContainer.isCreated()).isEqualTo(true);
    }

}
