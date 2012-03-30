/**
 * loginValidate
 * 
 * Australian National University Data Commons
 * 
 * Validates the form fields on the login form.
 * 
 * Returns a boolean value of true if all the fields on the form are valid, returns false if one or more fields on the form invalid.
 * 
 * Usage: <code>
 * <form onsubmit="loginValidate(this)">
 * </code>
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		16/03/2012	Rahul Khanna (RK)		Initial.
 * </pre>
 */

function loginValidate(loginForm)
{
	var username = loginForm.username.value;
	var password = loginForm.password.value;
	var isValid = true;

	// Check if username is blank. If blank, then display error and set return value as false.
	if (username != "")
	{
		isValid = isValid && true;
	}
	else
	{
		isValid = false;

		var divErr = document.getElementById("idUsernameError");
		divErr.innerHTML = "Username cannot be blank.";
		if (divErr.style.display == "none")
			divErr.style.display == "block";
	}

	// Check if password is blank. If blank, then display error and set return value as false.
	if (password != "")
	{
		isValid = isValid && true;
	}
	else
	{
		isValid = false;

		var divErr = document.getElementById("idPasswordError");
		divErr.innerHTML = "Password cannot be blank.";
		if (divErr.style.display == "none")
			divErr.style.display == "block";
	}

	return isValid;
}
