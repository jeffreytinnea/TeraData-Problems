package com.jeffreytinnea.gridwalker.gridwalker;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.jeffreytinnea.gridwalker.controller.GridController;
import com.jeffreytinnea.gridwalker.model.Grid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GridWalkerTests {
	@LocalServerPort
	private int port;

	@Autowired
	private GridController controller;

	@Autowired
	private TestRestTemplate restTemplate;

	private ResponseEntity<String> callGridWalkerEndpoint(Grid testGrid) throws Exception {
		URI testHost = new URI("http://localhost:" + port + "/api/traversed-grid/");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> request = new HttpEntity<>(testGrid.toString(), headers);
		ResponseEntity<String> result = this.restTemplate.exchange(testHost, HttpMethod.POST, request, String.class);
		System.out.println(String.format("Response Code: %s\nResponse Body: %s", result.getStatusCodeValue(), result.getBody()));
		return result;
	}

	@Test
	public void contextLoads() throws Exception {
		assertThat(this.controller).isNotNull();
	}

	@Test
	public void threeByThreeGridSuccess() throws Exception {
		List<List<Integer>> matrix = new ArrayList<>(List.of(
			new ArrayList<>(List.of(0, 1, 2)),
			new ArrayList<>(List.of(3, 4, 5)),
			new ArrayList<>(List.of(6, 7, 8))));
		Grid testGrid = new Grid(matrix);

		ResponseEntity<String> result = this.callGridWalkerEndpoint(testGrid);

		assertThat(result.getStatusCodeValue()).isEqualTo(200);
		assertThat(result.getBody()).isEqualTo("0, 1, 2, 5, 8, 7, 6, 3, 4");
	}

	@Test
	public void sixBySixGridSuccess() throws Exception {
		List<List<Integer>> matrix = new ArrayList<>(List.of(
			new ArrayList<>(List.of(0, 1, 2, 3, 4, 5)),
			new ArrayList<>(List.of(6, 7, 8, 9, 10, 11)),
			new ArrayList<>(List.of(12, 13, 14, 15, 16, 17)),
			new ArrayList<>(List.of(18, 19, 20, 21, 22, 23)),
			new ArrayList<>(List.of(24, 25, 26, 27, 28, 29)),
			new ArrayList<>(List.of(30, 31, 32, 33, 34, 35))));
		Grid testGrid = new Grid(matrix);
		ResponseEntity<String> result = this.callGridWalkerEndpoint(testGrid);

		assertThat(result.getStatusCodeValue()).isEqualTo(200);
		assertThat(result.getBody()).isEqualTo("0, 1, 2, 3, 4, 5, 11, 17, 23, 29, 35, 34, 33, 32, 31, 30, 24, 18, 12, 6, 7, 8, 9, 10, 16, 22, 28, 27, 26, 25, 19, 13, 14, 15, 21, 20");
	}

	@Test
	public void oneByThreeGridSuccess() throws Exception {
		List<List<Integer>> matrix = new ArrayList<>(List.of(
			new ArrayList<>(List.of(0, 1, 2))));
		Grid testGrid = new Grid(matrix);

		ResponseEntity<String> result = this.callGridWalkerEndpoint(testGrid);

		assertThat(result.getStatusCodeValue()).isEqualTo(200);
		assertThat(result.getBody()).isEqualTo("0, 1, 2");
	}

	@Test
	public void threeByOneGridSuccess() throws Exception {
		List<List<Integer>> matrix = new ArrayList<>(List.of(
			new ArrayList<>(List.of(2)),
			new ArrayList<>(List.of(5)),
			new ArrayList<>(List.of(8))));
		Grid testGrid = new Grid(matrix);

		ResponseEntity<String> result = this.callGridWalkerEndpoint(testGrid);

		assertThat(result.getStatusCodeValue()).isEqualTo(200);
		assertThat(result.getBody()).isEqualTo("2, 5, 8");
	}

	@Test
	public void gridWithInterestingValuesSuccess() throws Exception {
		List<List<Integer>> matrix = new ArrayList<>(List.of(
			new ArrayList<>(List.of(Integer.MIN_VALUE, Integer.MAX_VALUE, 0))));
		Grid testGrid = new Grid(matrix);

		ResponseEntity<String> result = this.callGridWalkerEndpoint(testGrid);

		assertThat(result.getStatusCodeValue()).isEqualTo(200);
		assertThat(result.getBody()).isEqualTo(String.format("%s, %s, 0", Integer.MIN_VALUE, Integer.MAX_VALUE));
	}

	@Test
	public void noBodyThrowsError() throws Exception {
		URI testHost = new URI("http://localhost:" + port + "/api/traversed-grid/");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		ResponseEntity<String> result = this.restTemplate.exchange(testHost, HttpMethod.POST, request, String.class);
		System.out.println(String.format("Response Code: %s\nResponse Body: %s", result.getStatusCodeValue(), result.getBody()));

		assertThat(result.getStatusCodeValue()).isEqualTo(400);
	}

	@Test
	public void emptyMatrixThrowsError() throws Exception {
		List<List<Integer>> matrix = new ArrayList<>(List.of(
			new ArrayList<>(List.of())));
		Grid testGrid = new Grid(matrix);

		ResponseEntity<String> result = this.callGridWalkerEndpoint(testGrid);

		assertThat(result.getStatusCodeValue()).isEqualTo(400);
		assertThat(result.getBody()).contains("Matrix must have at least one value");
	}

	@Test
	public void nonIntegerValuesThrowsError() throws Exception {
		URI testHost = new URI("http://localhost:" + port + "/api/traversed-grid/");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> request = new HttpEntity<>("{\"matrix\":[[\"a\",0,1]]}", headers);
		ResponseEntity<String> result = this.restTemplate.exchange(testHost, HttpMethod.POST, request, String.class);
		System.out.println(String.format("Response Code: %s\nResponse Body: %s", result.getStatusCodeValue(), result.getBody()));

		assertThat(result.getStatusCodeValue()).isEqualTo(400);
	}

	@Test
	public void nonSquareMatrixThrowsError() throws Exception {
		List<List<Integer>> matrix = new ArrayList<>(List.of(
			new ArrayList<>(List.of(0, 1, 2)),
			new ArrayList<>(List.of(3, 4)),
			new ArrayList<>(List.of(6, 7, 8))));
		Grid testGrid = new Grid(matrix);

		ResponseEntity<String> result = this.callGridWalkerEndpoint(testGrid);

		assertThat(result.getStatusCodeValue()).isEqualTo(400);
		assertThat(result.getBody()).contains("Supplied matrix must be rectangular");
	}
}
