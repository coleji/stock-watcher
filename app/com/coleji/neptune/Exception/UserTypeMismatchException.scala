package com.coleji.neptune.Exception

class UserTypeMismatchException (
	private val message: String = "Unauthorized Access Denied",
	private val cause: Throwable = null
) extends Exception(message, cause)