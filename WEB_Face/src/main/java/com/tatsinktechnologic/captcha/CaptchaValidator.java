/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.captcha;

/**
 *
 * @author olivier.tatsinkou
 */
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

@FacesValidator(value = "captchaValidator")
public class CaptchaValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		String captchaEntered = (String) value;

		System.out.println("captcha " + captchaEntered);

		FacesMessage message = null;

		try {

			if (captchaEntered == null || captchaEntered.isEmpty())
				message = new FacesMessage(
						"Please Enter Security Code shown in the image box");

			else {
				HttpServletRequest request = (HttpServletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest();
				javax.servlet.http.HttpSession session = request.getSession();
				String captcha = (String) session.getAttribute("CAPTCHA");
				System.out.println("captcha G " + captcha);
				if (!captchaEntered.equals(captcha)) {
					message = new FacesMessage("Captcha is invalid");
				}
			}

			if (message != null)
				throw new ValidatorException(message);

		} catch (Exception ex) {
			throw new ValidatorException(new FacesMessage(ex.getMessage()));
		}

	}

}
