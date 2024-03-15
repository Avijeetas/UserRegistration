package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Value;
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
      @Value("${otp.timeout-minute}")
      private int timeoutMinute;
      private final TemplateEngine templateEngine;

      public EmailUtils(TemplateEngine templateEngine) {
         this.templateEngine = templateEngine;
      }

      public String buildEmail(String name, String message) {
         Context context = new Context();
         context.setVariable("name", name);
         context.setVariable("message", message);
         return templateEngine.process("emailTemplate", context);
      }


}
