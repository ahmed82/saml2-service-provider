package com.sp.saml;

import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class MainController {
	
	@Autowired
	RestTemplate restTemplate;
	
	@GetMapping("/")
	public RedirectView index(
			@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal) {
		String userid = principal.getName();
		final String url = "http://localhost:8080/oauth/token";
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getPrincipal());
		System.out.println(auth.getDetails());
		System.out.println(auth.getDetails());
		/*
		 * String access_token_url = "http://localhost:8080/oauth/token";
		 * access_token_url += "?code=" + code; access_token_url +=
		 * "&grant_type=authorization_code"; access_token_url +=
		 * "&redirect_uri=http://localhost:8090/showEmployees";
		 */

		
		
		final String password = "webpass";
        //setting up the HTTP Basic Authentication header value
        String authorizationHeader = "Basic " + DatatypeConverter.printBase64Binary(("web" + ":" + password).getBytes());

        HttpHeaders requestHeaders = new HttpHeaders();
        //set up HTTP Basic Authentication Header
        requestHeaders.add("Authorization", authorizationHeader);
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     

        body.add("grant_type", "password");
        body.add("username", userid);
        body.add("password", "SAML-SECRET");

        // Note the body object as first parameter!
        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, requestHeaders);

     // send POST request
     ResponseEntity<OAuth2DTO> response = restTemplate.postForEntity(url, httpEntity, OAuth2DTO.class);
     //ResponseEntity<String> response = restTemplate.exchange(access_token_url, HttpMethod.POST, httpEntity, String.class);
     // check response
     if (response.getStatusCode() == HttpStatus.OK) {
         System.out.println("Request Successful");
         System.out.println(response.getBody());
     } else {
         System.out.println("Request Failed");
         System.out.println(response.getStatusCode());
     }
			OAuth2DTO tokenObject = (OAuth2DTO)response.getBody();
			String code = tokenObject.getAccess_token().trim().toString();
		//return userid.trim().toString()+"\n\n"+response.getBody();
		
		//return "http://localhost:3000"+"?code=";
		return new RedirectView("http://localhost:3000?code="+code);
	}
	
	
}
