package com.example.demo.utils;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author Avijeet
 * @project UserRegistration
 * @github avijeetas
 * @date 02-11-2024
 **/
@Component
public class EmailUtils {
   public String buildEmail(String name, String link) {
      // Create a Thymeleaf context to populate dynamic variables
      TemplateEngine templateEngine = new TemplateEngine();
      Context context = new Context();
      context.setVariable("name", name);
      context.setVariable("link", link);

      // Process the Thymeleaf template to generate the HTML email content

       return templateEngine.process("emailTemplate", context);
   }
}
