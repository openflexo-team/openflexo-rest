package org.openflexo.technologyadapter.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
//import org.junit.jupiter.api.BeforeAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

// This test is a workshop for developers
@Ignore
public class GitHubIssuesTest {

	private static final String TOKEN = "XXXX"; // remplace par ton token
	private static final String OWNER = "octocat";
	private static final String REPO = "Hello-World";

	static {
		RestAssured.baseURI = "https://api.github.com";
	}

	/*@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "https://api.github.com";
	}*/

	@Test
	public void testListOpenIssues() {
		//@formatter:off
		ValidatableResponse response = 
		given()
			.header("Authorization", "Bearer " + TOKEN)
			.queryParam("state", "open")
			.queryParam("per_page", 5)
		.when()
			.get("/repos/{owner}/{repo}/issues", OWNER, REPO)
		.then()
			.statusCode(200)
			.body("size()", greaterThan(0))
			.body("[0].title", notNullValue());
		//@formatter:on

		JsonPath jsonPath = response.extract().jsonPath();

		System.out.println("jsonPath: " + jsonPath.prettyPrint());

		for (Object object : jsonPath.getList("")) {
			System.out.println(" *** " + object + " of " + object.getClass());
		}

	}

	@Test
	public void testListOpenIssues2() throws JsonMappingException, JsonProcessingException, IOException {
		//@formatter:off
			Response response = 
			given()
				.header("Authorization", "Bearer " + TOKEN)
				.queryParam("state", "open")
				.queryParam("per_page", 5)
			.when()
				.get("/repos/{owner}/{repo}/issues", OWNER, REPO);
			//@formatter:on

		// Tiens ca marche ca
		// JsonPath jsonPath = response.jsonPath();

		String jsonBody = response.getBody().asString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(jsonBody);

		// Exemple : parcourir et créer tes objets
		for (JsonNode node : root) {
			System.out.println(">>>>>> " + node);
			System.out.println("    node_id = " + node.get("node_id"));
			System.out.println("    url = " + node.get("url"));
		}

	}

	@Test
	public void testRepositories() {
		//@formatter:off
		String OWNER = "sylvain-openflexo";
		ValidatableResponse response = 
		given()
			.header("Authorization", "Bearer " + TOKEN)
			.queryParam("state", "open")
			.queryParam("per_page", 5)
		.when()
			.get("/users/{username}/repos", OWNER)
		.then()
			.statusCode(200)
			.body("size()", greaterThan(0));
			//.body("[0].title", notNullValue());
		//@formatter:on

		JsonPath jsonPath = response.extract().jsonPath();

		System.out.println("jsonPath: " + jsonPath.prettyPrint());

		for (Object object : jsonPath.getList("")) {
			System.out.println(" *** " + object + " of " + object.getClass());
		}

	}

	/*@Test
	public void testFilterIssuesByLabel() {
		given().header("Authorization", "Bearer " + TOKEN).queryParam("labels", "bug").when()
				.get("/repos/{owner}/{repo}/issues", OWNER, REPO).then().statusCode(200)
				.body("findAll { it.labels.any { it.name == 'bug' } }.size()", greaterThan(0));
	}*/

	// @Test
	// public void testCreateIssue() {

	/*String requestBody = """
			{
			  "title": "Issue test automatique",
			  "body": "Créée via Rest Assured",
			  "assignees": ["octocat"],
			  "labels": ["bug"]
			}
			""";
	
	given().header("Authorization", "Bearer " + TOKEN).header("Accept", "application/vnd.github+json").contentType("application/json")
			.body(requestBody).when().post("/repos/{owner}/{repo}/issues", OWNER, REPO).then().statusCode(201)
			.body("title", equalTo("Issue test automatique")).body("state", equalTo("open"));*/
	// }
}
