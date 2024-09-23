package com.testcontainer.testcontainer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestcontainerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MySQLContainer<?> mysqlContainer;

	@BeforeAll
	public static void setUpDatabase(@Autowired MySQLContainer<?> mysqlContainer) throws SQLException {
		// Obtain JDBC connection details from the MySQLContainer
		String jdbcUrl = mysqlContainer.getJdbcUrl();
		String username = mysqlContainer.getUsername();
		String password = mysqlContainer.getPassword();

		// Connect to the database
		try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
			// Create a table (if not exists) and insert test data
			String createTableSQL = "CREATE TABLE IF NOT EXISTS user (id INT AUTO_INCREMENT, name VARCHAR(255), PRIMARY KEY (id))";
			String insertDataSQL = "INSERT INTO user (name) VALUES (?)";

			// Execute table creation query
			try (PreparedStatement createTableStmt = connection.prepareStatement(createTableSQL)) {
				createTableStmt.execute();
			}

			// Insert some test data
			try (PreparedStatement insertStmt = connection.prepareStatement(insertDataSQL)) {
				insertStmt.setString(1, "Alice");
				insertStmt.executeUpdate();

				insertStmt.setString(1, "Bob");
				insertStmt.executeUpdate();
			}
		}
	}

	@Test
	public void mysqlContainerShouldBeRunningWhenTestStarts() {
		assertNotNull(mysqlContainer);
		assert(mysqlContainer.isRunning());
	}

	@Test
	public void shouldReturnAllUsersWhenGettingUsers() throws Exception {
		mockMvc.perform(get("/api/users")) // Assuming the endpoint will be /api/users
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2))); // Asserts that there are exactly 2 users
	}

}
