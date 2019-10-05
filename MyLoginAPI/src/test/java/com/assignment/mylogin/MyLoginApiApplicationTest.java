package com.assignment.mylogin;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.StringJoiner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest (
			webEnvironment = WebEnvironment.RANDOM_PORT,
			classes = MyLoginApiApplication.class
		)
@AutoConfigureMockMvc
@PropertySource("classpath:application.properties")
public class MyLoginApiApplicationTest {

    @Autowired
    private MockMvc mockMvc;
    
    private static final long currentTime = System.currentTimeMillis();
    private static final String DEFAULT_TEST_EMAIL = "test" + currentTime + "@gmail.com";
    private static final String DEFAULT_TEST_PASSWORD = "Test123";
    private static final String DEFAULT_USER_FIRST_NAME = "TEST" + currentTime;
    private static final String DEFAULT_USER_LAST_NAME = "USER" + currentTime;

	public void contextLoads() {
	}
	
	/**
	 * sign up validation test for email format and first name non empty
	 * @throws Exception
	 */
	@Test
	public void sigupValiadtionTest() throws Exception {
		
			String json  = populateUserJson("", DEFAULT_USER_LAST_NAME, "test", DEFAULT_TEST_PASSWORD);
			mockMvc.perform(post("/api/signup").content(json)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.errorDesc").value("Missing Fields/Not Valid fields"))
						.andExpect(jsonPath("$.errorDetail").isArray())
						.andExpect(jsonPath("$.errorDetail",hasItem("Invalid Email ID")));
	}
	
	/**
	 * signup success scenario test
	 * @throws Exception
	 */
	@Test
	public void sigupSuccessTest() throws Exception {
			String json  = populateUserJson(DEFAULT_USER_FIRST_NAME, DEFAULT_USER_LAST_NAME, DEFAULT_TEST_EMAIL, DEFAULT_TEST_PASSWORD);
			mockMvc.perform(post("/api/signup").content(json)
					.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated());
	}
	
	/**
	 * signup with duplicate email address validation test
	 * @throws Exception
	 */
	@Test
	public void sigupDuplicateEmailTest() throws Exception {
			String json  = populateUserJson(DEFAULT_USER_FIRST_NAME, DEFAULT_USER_LAST_NAME, DEFAULT_TEST_EMAIL, DEFAULT_TEST_PASSWORD);
			mockMvc.perform(post("/api/signup").content(json)
					.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.errorDesc").value("Duplicate user found for the user name"));
	}
	
	@Test
	public void loginInvalidUserTest() throws Exception {
			String json  = populateLoginJson(DEFAULT_TEST_EMAIL+"aa", DEFAULT_TEST_PASSWORD);
			mockMvc.perform(post("/api/login").content(json)
					.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isUnauthorized())
						.andExpect(jsonPath("$.errorDesc").value("Invalid username / Password"));

	}
	
	@Test
	public void loginValidUserTest() throws Exception {
			String json  = populateLoginJson(DEFAULT_TEST_EMAIL, DEFAULT_TEST_PASSWORD);
			mockMvc.perform(post("/api/login").content(json)
					.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk());
	}
	
	@Test
	public void getUserInfoSuccessTest() throws Exception {
			String json  = populateLoginJson(DEFAULT_TEST_EMAIL, DEFAULT_TEST_PASSWORD);
			MvcResult result = mockMvc.perform(post("/api/login").content(json)
					.contentType(MediaType.APPLICATION_JSON)).andReturn();
			String token = result.getResponse().getContentAsString();
			
			mockMvc.perform(post("/api/userinfo").content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer "+token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.firstName").value(DEFAULT_USER_FIRST_NAME))
					.andExpect(jsonPath("$.lastName").value(DEFAULT_USER_LAST_NAME));
			
	}
	
	@Test
	public void getUserInfoWithoutTokenTest() throws Exception {

		mockMvc.perform(post("/api/userinfo")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.errorDesc").value("Unauthorized Access / Token starts with Bearer "));
			
	}
	
	@Test
	public void getUserInfoInvaidTokenTest() throws Exception {
			String json  = populateLoginJson(DEFAULT_TEST_EMAIL, DEFAULT_TEST_PASSWORD);
			MvcResult result = mockMvc.perform(post("/api/login").content(json)
					.contentType(MediaType.APPLICATION_JSON)).andReturn();
			String token = result.getResponse().getContentAsString();
			
			mockMvc.perform(post("/api/userinfo").content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer x" + token))
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.errorDesc").value("Invalid JWT Token"));
			
	}
	
	
	public String populateUserJson(String firstName, String lastName, 
			String emailId, String password) {
		
			return 	"{" + new StringJoiner(",")
					.add("\"firstName\":\""+firstName+"\"")
					.add("\"lastName\":\""+lastName+"\"")
					.add("\"emailId\":\""+emailId+"\"")
					.add("\"password\":\""+password+"\"")
					.toString() + "}";
	}

	public String populateLoginJson(String emailId, String password) {
		
			return 	"{" + new StringJoiner(",")
					.add("\"emailId\":\""+emailId+"\"")
					.add("\"password\":\""+password+"\"")
					.toString() + "}";
	}
}
