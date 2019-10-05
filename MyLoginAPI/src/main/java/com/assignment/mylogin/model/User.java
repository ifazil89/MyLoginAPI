package com.assignment.mylogin.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel (description = "User detail object")
@Getter
@Setter
@Document (collection = "User")
public class User {

	@ApiModelProperty (notes = "Id of the record. Auto created ID", hidden = true)
	@Id
	private String id;
	
	@ApiModelProperty (notes = "First name of the user", dataType= "String", required = true, allowEmptyValue = false)
	@NotNull(message = "First name should not be null or empty")
	@NotEmpty (message = "First name should not be empty")
	private String firstName;
	
	@ApiModelProperty (notes = "Last name of the user", dataType= "String", required = false, allowEmptyValue = true)
	private String lastName;
	
	@ApiModelProperty (notes = "Email Id of the user. Also act as the userId. Emailid should be valid format (eg:test123@gmail.com)", dataType= "String", required = true, allowEmptyValue = false)
	@NotNull (message = "Email Id should not be null or empty")
	@NotEmpty (message = "Email Id should not be empty")
	@Pattern (regexp="[\\w_]+@\\w+\\..*" , message = "Invalid Email ID")
	private String emailId;
	
	@ApiModelProperty (notes = "Password for the user", dataType= "String", required = true, allowEmptyValue = false)
	@NotNull
	@NotEmpty
	private String password;
	
}
